
package io.github.zzzyyylllty.sertraline.util

import io.github.zzzyyylllty.sertraline.Sertraline.gjsScriptCache
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.util.ScriptHelper.engineManager
import io.github.zzzyyylllty.sertraline.util.serialize.generateHash
import javax.script.Bindings
import javax.script.Compilable
import javax.script.CompiledScript
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import javax.script.SimpleBindings

val GJS_LANG_ID = "Graal.js"

class GraalJsUtil {

    // 使用 by lazy 确保 engine 只创建一次
    private val scriptEngine: ScriptEngine by lazy {
        devLog("${engineManager.engineFactories}")
        engineManager.getEngineByName(GJS_LANG_ID) ?: throw IllegalStateException("Graal.js ScriptEngine not found.")
    }

    fun compile(script: String, vars: Map<String, Any?>): CompiledScript? {
        return (scriptEngine as Compilable).compile(script)
    }

    fun directEval(script: String, vars: Map<String, Any?>): Any? {
        val bindings: Bindings = SimpleBindings(vars)
        return scriptEngine.eval(script, bindings)
    }

    fun cachedEval(script: String, vars: Map<String, Any?>): Any? {

        val hash = script.generateHash()
        val compiledScript = gjsScriptCache.getOrPut(hash) {
            compile(script, vars)
        }

        val bindings: Bindings = SimpleBindings(vars)
        return compiledScript?.eval(bindings) ?: scriptEngine.eval(script, bindings)
    }
}
