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

    fun compile(script: String): Source? {
        return try {
            Source.newBuilder(GJS_LANG_ID, script, "script.js").build()
        } catch (e: Exception) {
            e.printStackTrace()
            null // 编译失败时返回 null
        }
    }

    fun newGraalContext(): Context {

        return Context.newBuilder(GJS_LANG_ID)
            .allowAllAccess(true)
            .allowHostAccess(hostAccess)
            .engine(globalGJSEngine)
            .build()
    }


    fun directEval(script: String, vars: Map<String, Any?>): Any? {

        return executeScript(script, vars)

    }
    fun cachedEval(script: String, vars: Map<String, Any?>): Any? {

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

    private fun executeScript(scriptOrSource: Any, vars: Map<String, Any?>): Any? {
        createContext(vars).use { context ->
            val bindings: Value = context.getBindings(GJS_LANG_ID)
            vars.forEach {
                bindings.putMember(it.key, it.value)
            }

            val result: Value = when (scriptOrSource) {
                is String -> context.eval(GJS_LANG_ID, scriptOrSource)
                is Source -> context.eval(scriptOrSource)
                else -> throw IllegalArgumentException("Unsupported script type: ${scriptOrSource::class.java}")
            }

            return result.`as`(Any::class.java)
        }
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

    internal fun loadDependencies(name: String) {
        RuntimeEnv.ENV_DEPENDENCY.loadFromLocalFile(
            this::class.java.classLoader.getResource("META-INF/dependencies/$name.json")
        )
    }

}
