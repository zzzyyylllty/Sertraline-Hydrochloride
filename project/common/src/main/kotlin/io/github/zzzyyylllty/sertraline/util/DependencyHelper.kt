package io.github.zzzyyylllty.sertraline.util

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.github.zzzyyylllty.sertraline.Sertraline
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.logger.severeS
import org.bukkit.Bukkit
import taboolib.common.ClassAppender
import taboolib.common.LifeCycle
import taboolib.common.PrimitiveIO
import taboolib.common.PrimitiveSettings
import taboolib.common.env.DependencyScope
import taboolib.common.env.JarRelocation
import taboolib.common.env.RuntimeEnv
import taboolib.common.env.legacy.Artifact
import taboolib.common.env.legacy.Dependency
import taboolib.common.env.legacy.DependencyDownloader
import taboolib.common.env.legacy.Repository
import taboolib.common.platform.Awake
import java.io.File
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*


val dependencies = listOf(
    "adventure",
    // "arim",
    "caffeine",
    "datafixerupper",
    "fluxon",
    "graaljs",
    "gson",
    "jackson",
    // "kotlincrypto",
    "uniitem"
)

class DependencyHelper {
    fun isPluginInstalled(name: String): Boolean {
        return (Bukkit.getPluginManager().getPlugin(name) != null)
    }

}

@Awake(LifeCycle.INIT)
fun initDependencies() {
    devLog("Starting loading dependencies...")
    for (name in dependencies) {
        try {
            devLog("Trying to load dependencies from file $name")
            val resource = Sertraline::class.java.classLoader.getResource("META-INF/dependencies/$name.json")
            if (resource == null) {
                severeS("Resource META-INF/dependencies/$name.json not found!")
                continue // 跳过这个依赖文件
            }

            devLog("Resource URL: $resource")
            devLog("Using classloader: ${Sertraline::class.java.classLoader}")

            SertralineLocalDependencyHelper().loadFromLocalFile(resource)

            devLog("Trying to load dependencies from file $name ... DONE.")
        } catch (e: Exception) {
            severeS("Trying to load dependencies from file $name FAILED.")
            severeS("Exception: $e")
            e.printStackTrace()
        }
    }
}
