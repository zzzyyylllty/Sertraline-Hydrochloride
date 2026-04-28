package io.github.zzzyyylllty.sertraline.config

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.levels
import io.github.zzzyyylllty.sertraline.data.Level
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.logger.infoL
import io.github.zzzyyylllty.sertraline.logger.infoS
import io.github.zzzyyylllty.sertraline.logger.warningL
import io.github.zzzyyylllty.sertraline.util.serialize.parseToMap
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFile
import taboolib.common.platform.function.warning
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.io.File

fun loadLevelFiles() {
    infoL("Level_Load")
    if (!File(getDataFolder(), "levels").exists()) {
        warningL("Level_Load_Regen")
        releaseResourceFile("levels/default.yml")
    }
    val files = File(getDataFolder(), "levels").listFiles()
    if (files != null) {
        for (file in files) {
            // If directory load file in it...
            if (file.isDirectory) file.listFiles()?.forEach {
                loadLevelFile(it)
            }
            else loadLevelFile(file)
        }
    }
}

fun loadLevelFile(file: File) {
    devLog("Loading level file ${file.name}")

    if (file.isDirectory) file.listFiles()?.forEach {
        loadLevelFile(it)
    } else {
        if (!checkRegexMatch(file.name, (config["file-load.level"] ?: ".*").toString())) {
            devLog("${file.name} not match regex, skipping...")
            return
        }
        val map = multiExtensionLoader(file)
        if (map != null) for (it in map.entries) {
            val key = it.key
            val value = map[key]
            loadLevel(key, value as Map<String, Any?>? ?: linkedMapOf())
        } else {
            devLog("Map is null, skipping.")
        }
    }
}

fun loadLevel(key: String, arg: Map<String, Any?>) {
    val c = ConfigUtil
    val level = Level(
        id = key,
        name = c.getDeep(arg, "name") as? String ?: key,
        description = c.getDeep(arg, "description") as? String ?: "",
        color = c.getDeep(arg, "color") as? String ?: "<white>",
        weight = (c.getDeep(arg, "weight") as? Number)?.toInt() ?: 1,
        extra = c.getDeep(arg, "extra") as? Map<String, Any?> ?: mapOf()
    )
    levels[key] = level
    devLog("Loaded level: $key")
}