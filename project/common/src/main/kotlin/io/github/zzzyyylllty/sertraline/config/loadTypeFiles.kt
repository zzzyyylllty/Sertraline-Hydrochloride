package io.github.zzzyyylllty.sertraline.config

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.types
import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.data.Type
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.logger.infoL
import io.github.zzzyyylllty.sertraline.logger.infoS
import io.github.zzzyyylllty.sertraline.logger.severeL
import io.github.zzzyyylllty.sertraline.logger.warningL
import io.github.zzzyyylllty.sertraline.util.serialize.parseToMap
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFile
import taboolib.common.platform.function.warning
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type as ConfigType
import java.io.File

fun loadTypeFiles() {
    infoL("Type_Load")
    if (!File(getDataFolder(), "types").exists()) {
        warningL("Type_Load_Regen")
        releaseResourceFile("types/default.yml")
    }
    val files = File(getDataFolder(), "types").listFiles()
    if (files != null) {
        for (file in files) {
            // If directory load file in it...
            if (file.isDirectory) file.listFiles()?.forEach {
                loadTypeFile(it)
            }
            else loadTypeFile(file)
        }
    }
}

fun loadTypeFile(file: File) {
    devLog("Loading type file ${file.name}")

    if (file.isDirectory) file.listFiles()?.forEach {
        loadTypeFile(it)
    } else {
        if (!checkRegexMatch(file.name, (config["file-load.type"] ?: ".*").toString())) {
            devLog("${file.name} not match regex, skipping...")
            return
        }
        val map = multiExtensionLoader(file)
        if (map != null) {
            if (map.isEmpty()) {
                severeL("Config_Load_Error_Empty", file.name)
                return
            }
            for (it in map.entries) {
                val key = it.key
                val value = map[key]
                loadType(key, value as Map<String, Any?>? ?: linkedMapOf())
            }
        }
    }
}

fun loadType(key: String, arg: Map<String, Any?>) {
    val c = ConfigUtil
    val type = Type(
        id = key,
        name = c.getDeep(arg, "name") as? String ?: key,
        parent = c.getDeep(arg, "parent") as? String,
        description = c.getDeep(arg, "description") as? String ?: "",
        extra = c.getDeep(arg, "extra") as? Map<String, Any?> ?: mapOf()
    )
    types[key] = type
    devLog("Loaded type: $key")
}