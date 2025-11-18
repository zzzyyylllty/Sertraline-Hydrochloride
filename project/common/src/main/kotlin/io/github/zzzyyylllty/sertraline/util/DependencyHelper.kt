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
    "arim",
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


@Awake(LifeCycle.CONST)
fun initDependencies() {
    devLog("Starting loading dependencies...")
    for (name in dependencies) {
        try {
            devLog("Trying load dependencies from file $name")
            SertralineLocalDependencyHelper().loadFromLocalFile(
                Sertraline::class.java.classLoader.getResource("META-INF/dependencies/$name.json")
            )
//            RuntimeEnv.ENV_DEPENDENCY.loadFromLocalFile(
//                Sertraline::class.java.classLoader.getResource("META-INF/dependencies/$name.json")
//            )
            devLog("Trying load dependencies from file $name ... DONE.")
        } catch (e: Exception) {
            severeS("Trying load dependencies from file $name FAILED.")
            severeS("Exception: $e")
            e.printStackTrace()
        }
    }
}