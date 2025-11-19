package io.github.zzzyyylllty.sertraline.util

import io.github.zzzyyylllty.sertraline.Sertraline
import io.github.zzzyyylllty.sertraline.Sertraline.gjsScriptCache
import io.github.zzzyyylllty.sertraline.Sertraline.jexlScriptCache
import io.github.zzzyyylllty.sertraline.util.serialize.generateHash
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.Engine
import org.graalvm.polyglot.HostAccess
import org.graalvm.polyglot.Source
import org.graalvm.polyglot.Value
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

class GraalJsUtil {

    fun compile(script: String, vars: Map<String, Any?>): Source? {
        return Source.newBuilder(GJS_LANG_ID, script, "script.js").build()
    }

    fun newGraalContext(vars: Map<String, Any?>): Context {

        return Context.newBuilder(GJS_LANG_ID)
            .hostClassLoader(Sertraline::class.java.classLoader)
            .engine(globalGJSEngine)
            .build()
    }


    fun directEval(script: String, vars: Map<String, Any?>): Any? {

        createContext(vars).use { context ->


            val bindings: Value = context.getBindings(GJS_LANG_ID)
            vars.forEach {
                bindings.putMember(it.key, it.value)
            }

            val result: Value = context.eval(GJS_LANG_ID, script)

            return result.`as`(Any::class.java)
        }

    }
    fun cachedEval(script: String, vars: Map<String, Any?>): Any? {

        val hash = script.generateHash()
        val cache = gjsScriptCache[hash]
        if (cache != null) {
            val bindings = SimpleBindings()
            bindings.putAll(vars)
            createContext(vars).use { context ->

                val bindings: Value = context.getBindings(GJS_LANG_ID)
                vars.forEach {
                    bindings.putMember(it.key, it.value)
                }

                val result: Value = context.eval(cache)

                return result.`as`(Any::class.java)
            }
        } else {
            val compiled = GraalJsUtil().compile(script, vars)
            compiled.let { it ->
                gjsScriptCache[hash] = it
                createContext(vars).use { context ->

                    val bindings: Value = context.getBindings(GJS_LANG_ID)
                    vars.forEach {
                        bindings.putMember(it.key, it.value)
                    }

                    val result: Value = context.eval(cache)

                    return result.`as`(Any::class.java)
                }
            }
        }

    }

    fun createContext(vars: Map<String, Any?>): Context {
        // 初始化预热上下文
        val context = newGraalContext(vars)
        return context
    }

    private fun createScriptSource(script: String, cached: Boolean = true): Source {
        return Source.newBuilder(GJS_LANG_ID, script, "<eval>").cached(cached).build()
    }
}