package io.github.zzzyyylllty.sertraline.function.kts

import io.github.zzzyyylllty.sertraline.debugMode.devLog
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.platform.util.bukkitPlugin
import javax.script.ScriptEngineManager

fun runKotlinScriptJsr223(
    script: String,
    vars: Map<String, Any?>,
    contextClassLoader: ClassLoader
): Any? {
    val originalContextClassLoader = Thread.currentThread().contextClassLoader
    Thread.currentThread().contextClassLoader = contextClassLoader
    try {
        val engineManager = ScriptEngineManager(contextClassLoader)
        val scriptEngine = engineManager.getEngineByName("kotlin")

        if (scriptEngine == null) {
            devLog("[ERROR] Kotlin script engine not found! Make sure kotlin-scripting-jsr223 is shaded correctly.")
            return null
        }

        // 绑定变量
        vars.forEach { (key, value) ->
            scriptEngine.put(key, value)
        }

        // 执行脚本
        return scriptEngine.eval(script)

    } catch (e: Exception) {
        devLog("[ERROR] Script execution failed:")
        e.printStackTrace()
        return null
    } finally {
        // 恢复原始类加载器
        Thread.currentThread().contextClassLoader = originalContextClassLoader
    }
}

