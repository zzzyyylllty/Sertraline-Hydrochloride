package io.github.zzzyyylllty.sertraline.util

import io.github.zzzyyylllty.sertraline.Sertraline.gjsScriptCache
import io.github.zzzyyylllty.sertraline.logger.severeS
import io.github.zzzyyylllty.sertraline.util.serialize.generateHash
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.Engine
import org.graalvm.polyglot.HostAccess
import org.graalvm.polyglot.Source
import org.graalvm.polyglot.Value
import taboolib.common.LifeCycle
import taboolib.common.env.RuntimeEnv
import taboolib.common.platform.Awake
import java.util.Collections
import java.util.WeakHashMap
import javax.script.*
import kotlin.String
import kotlin.collections.set


val GJS_LANG_ID = "js"

val globalGJSEngine: Engine by lazy {
    Engine.newBuilder(GJS_LANG_ID)
        .allowExperimentalOptions(true)
        .option("js.ecmascript-version", "latest")
        .option("js.nashorn-compat", "true") // Nashorn 兼容模式
        .build()
}

val hostAccess: HostAccess? by lazy {
    HostAccess.newBuilder()
//允许不受限制地访问所有公共构造函数、公共类的方法或字段
        .allowPublicAccess(true)
//允许客户端语言实现任何 Java 接口
        .allowAllImplementations(true)
//允许客户端语言实现（扩展）任何 Java 类
        .allowAllClassImplementations(true)
//允许访问数组
        .allowArrayAccess(true)
//允许访问 List
        .allowListAccess(true)
//允许客户应用程序以缓冲区元素的形式访问 ByteBuffers
        .allowBufferAccess(false)
//允许客户应用程序使用迭代器将可迭代对象作为值进行访问
        .allowIterableAccess(false)
//允许客户应用程序将迭代器作为迭代器值进行访问。
        .allowIteratorAccess(true)
//允许客户应用程序以哈希值形式访问 Map 对象
        .allowMapAccess(true)
//允许客户应用程序继承对允许方法的访问权限
        .allowAccessInheritance(false)
        .build()
}

object GraalJsUtil {

    /**
     * 所有存活 Graal Context 的弱引用集合，仅用于 DISABLE 时确定性关闭（释放 native 内存）。
     * 弱引用：调用方随手丢弃的一次性 Context 被 GC 后不会反向阻止集合清空。
     */
    private val liveContexts: MutableSet<Context> =
        Collections.newSetFromMap(Collections.synchronizedMap(WeakHashMap<Context, Boolean>()))

    fun compile(script: String): Source? {
        return try {
            Source.newBuilder(GJS_LANG_ID, script, "script.js").build()
        } catch (e: Exception) {
            e.printStackTrace()
            null // 编译失败时返回 null
        }
    }

    fun newGraalContext(): Context {

        val context = Context.newBuilder(GJS_LANG_ID)
//            .allowAllAccess(true) // 过于宽松，由下方细粒度配置替代
//            .allowHostAccess(hostAccess)
            .engine(globalGJSEngine)
            .allowHostAccess(hostAccess ?: HostAccess.ALL) // 使用细粒度配置
            .allowHostClassLookup { name -> name.startsWith("io.github.zzzyyylllty.sertraline") } // 仅允许访问插件自身类
            .build()
        liveContexts.add(context)
        return context
    }


    fun directEval(script: String, vars: Map<String, Any?>): Any? {
        // legacy12（Java 8）：GraalJS 不捆绑且运行时下载描述被排除，
        // 低版本映射到 JDK 内置 Nashorn（JSR 223），避免 NoClassDefFoundError
        if (VersionHelper().isLegacy()) return evalNashorn(script, vars)
        return executeScript(script, vars)

    }
    fun cachedEval(script: String, vars: Map<String, Any?>): Any? {
        if (VersionHelper().isLegacy()) return evalNashorn(script, vars)

        val hash = script.generateHash()
        val source = gjsScriptCache.getOrPut(hash) {
            compile(script)
        }

        if (source == null) {
            // 编译失败
            severeS("Script compilation failed for script: $script")
            return null
        }

        return executeScript(source, vars)

    }

    /**
     * 以 JS 语义字符串返回执行结果（等价于 GraalJS 的 Value.toString），
     * 用于保持旧 evalGraalJs 的输出格式（如整数 2 → "2" 而非 "2.0"）。
     */
    fun evalToJsString(script: String, vars: Map<String, Any?>): Any? {
        if (VersionHelper().isLegacy()) return evalNashorn(script, vars)
        val (context, bindings) = prepareContext(vars)
        val result: Value = context.eval(GJS_LANG_ID, script)
        return result?.toString() ?: result
    }

    /**
     * 每线程复用的 Nashorn 引擎。
     * JSR-223 引擎非线程安全（同一实例并发 eval 会出问题），且每次创建 ScriptEngineManager + engine 开销较大，
     * 因此按线程缓存而非全局共享。
     */
    internal val nashornEngineHolder = object : ThreadLocal<ScriptEngine>() {
        override fun initialValue(): ScriptEngine =
            ScriptEngineManager(GraalJsUtil::class.java.classLoader).getEngineByName("js")
    }

    private fun evalNashorn(script: String, vars: Map<String, Any?>): Any? {
        val engine = nashornEngineHolder.get() ?: run {
            severeS("JavaScript engine not available (Nashorn)")
            return null
        }
        val bindings = engine.createBindings()
        vars.forEach { (k, v) -> bindings[k] = v }
        return engine.eval(script, bindings)
    }

    /** 每线程复用的 Graal 上下文，避免每次 eval 都创建/销毁 Context（上下文创建是主要开销） */
    private val contextHolder = ThreadLocal<Context>()
    private val addedKeysHolder = ThreadLocal<MutableSet<String>>()

    /** 取（或创建）当前线程的上下文，注入变量，返回上下文与 bindings */
    private fun prepareContext(vars: Map<String, Any?>): Pair<Context, Value> {
        val context = contextHolder.get() ?: newGraalContext().also { contextHolder.set(it) }
        val bindings: Value = try {
            context.getBindings(GJS_LANG_ID)
        } catch (e: IllegalStateException) {
            // 上下文已被 DISABLE 关闭（插件热重载后旧线程池线程仍存活），重建
            val fresh = newGraalContext()
            contextHolder.set(fresh)
            fresh.getBindings(GJS_LANG_ID)
        }
        val addedKeys = addedKeysHolder.get() ?: mutableSetOf<String>().also { addedKeysHolder.set(it) }
        // 仅清理上一次追加的 key（即本次注入的变量），避免变量残留污染；不清理脚本自身声明的全局量
        for (key in addedKeys) {
            try {
                bindings.removeMember(key)
            } catch (_: Exception) {
            }
        }
        addedKeys.clear()
        vars.forEach {
            bindings.putMember(it.key, it.value)
            addedKeys.add(it.key)
        }
        return context to bindings
    }

    private fun executeScript(scriptOrSource: Any, vars: Map<String, Any?>): Any? {
        val (context, _) = prepareContext(vars)

        val result: Value = when (scriptOrSource) {
            is String -> context.eval(GJS_LANG_ID, scriptOrSource)
            is Source -> context.eval(scriptOrSource)
            else -> throw IllegalArgumentException("Unsupported script type: ${scriptOrSource::class.java}")
        }

        return result.`as`(Any::class.java)
    }

    fun createContext(vars: Map<String, Any?>): Context {
        // 初始化预热上下文
        val context = newGraalContext()
        return context
    }

    private fun createScriptSource(script: String, cached: Boolean = true): Source {
        return Source.newBuilder(GJS_LANG_ID, script, "<eval>").cached(cached).build()
    }

    @Awake(LifeCycle.INIT)
    private fun initialize() {
        loadDependencies("graaljs")
    }

    @Awake(LifeCycle.DISABLE)
    private fun shutdown() {
        // 关闭所有存活 Graal Context，确定性释放其 native 内存。
        // 若不显式 close，ThreadLocal 强引用会使 Context 永不进入 GC，native 内存随线程池线程持续泄漏
        synchronized(liveContexts) {
            liveContexts.forEach { ctx -> runCatching { ctx.close(true) } }
            liveContexts.clear()
        }
        contextHolder.remove()
        addedKeysHolder.remove()
    }

    internal fun loadDependencies(name: String) {
        RuntimeEnv.ENV_DEPENDENCY.loadFromLocalFile(
            this::class.java.classLoader.getResource("META-INF/dependencies/$name.json")
        )
    }

}
