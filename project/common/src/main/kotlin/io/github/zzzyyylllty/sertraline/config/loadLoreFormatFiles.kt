package io.github.zzzyyylllty.sertraline.config

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.loreFormats
import io.github.zzzyyylllty.sertraline.data.LineMode
import io.github.zzzyyylllty.sertraline.data.LoreElement
import io.github.zzzyyylllty.sertraline.data.LoreFormat
import io.github.zzzyyylllty.sertraline.data.LoreSetting
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.logger.infoL
import io.github.zzzyyylllty.sertraline.logger.infoS
import io.github.zzzyyylllty.sertraline.logger.warningL
import io.github.zzzyyylllty.sertraline.util.serialize.parseToMap
// import org.yaml.snakeyaml.Yaml
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFile
import taboolib.common.platform.function.warning
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.io.File


fun loadLoreFormatFiles() {
    infoL("LoreFormat_Load")
    if (!File(getDataFolder(), "lore-formats").exists()) {
        warningL("LoreFormat_Load_Regen")
        releaseResourceFile("lore-formats/loreGenerator.yml")
    }
    val files = File(getDataFolder(), "lore-formats").listFiles()
    for (file in files) {
        // If directory load file in it...
        if (file.isDirectory) file.listFiles()?.forEach {
            loadLoreFormatFile(it)
        }
        else loadLoreFormatFile(file)
    }
}
fun loadLoreFormatFile(file: File) {
    devLog("Loading file ${file.name}")

    if (file.isDirectory) file.listFiles()?.forEach {
        loadLoreFormatFile(it)
    } else {
        if (!checkRegexMatch(file.name, (config["file-load.lore-format"] ?: ".*").toString())) {
            devLog("${file.name} not match regex, skipping...")
            return
        }
        val map = multiExtensionLoader(file)
        if (map != null) for (it in map.entries) {
            val key = it.key
            val value = map[key]
            loadLoreFormat(key, value as Map<String, Any?>? ?: linkedMapOf())
        } else {
            devLog("Map is null, skipping.")
        }
    }
}

fun loadLoreFormat(key: String, arg: Map<String, Any?>) {
    val c = ConfigUtil
    val elementConfigs = c.getDeep(arg, "elements") as List<*>
    val elements = mutableListOf<LoreElement>()
    elementConfigs.forEach {
        elements.add(
            if (it is String) LoreElement(it, null)
            else if (it is Map<*,*>) LoreElement(
                it["content"].toString(),
                it["key"] as? String,
                (it["lineMode"] as? String)?.let { value -> LineMode.valueOf(value) },
                it["lineRequire"] as? List<String>
            ) else throw IllegalArgumentException("Invaild lore format data type!")
        )
    }
    val loreFormat = LoreFormat(
        settings = LoreSetting(
            overwrite = c.getDeep(arg, "settings.overwrite") as? Boolean ?: true,
            visual = c.getDeep(arg, "settings.visual") as? Boolean ?: true,
            skipBlank = c.getDeep(arg, "settings.auto-skip-blank") as? Boolean ?: true,
        ),
        elements = elements
    )
    loreFormats[key] = loreFormat
}