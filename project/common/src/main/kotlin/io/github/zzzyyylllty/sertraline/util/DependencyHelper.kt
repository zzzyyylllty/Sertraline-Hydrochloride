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
import taboolib.common.env.RuntimeEnv
import taboolib.common.platform.Awake
import java.io.File
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*
import javax.script.ScriptEngineManager


val dependencies = listOf(
    "adventure",
    // "arim",
    "caffeine",
    "datafixerupper",
    "fluxon",
    // "graaljs",
    "gson",
    "jackson",
    // "kotlincrypto",
    "uniitem"
)

object DependencyHelper {

    val mmLib by lazy {
        isPluginInstalled("MythicLib")
    }

    val papi by lazy {
        isPluginInstalled("PlaceholderAPI")
    }

    val pe by lazy {
        isPluginInstalled("packetevents")
    }




    fun isPluginInstalled(name: String): Boolean {
        return (Bukkit.getPluginManager().getPlugin(name) != null)
    }

}


