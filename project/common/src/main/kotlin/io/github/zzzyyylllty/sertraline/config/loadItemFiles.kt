package io.github.zzzyyylllty.sertraline.config

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.event.ItemLoadEvent
import io.github.zzzyyylllty.sertraline.logger.infoL
import io.github.zzzyyylllty.sertraline.logger.infoS
import io.github.zzzyyylllty.sertraline.logger.severeL
import io.github.zzzyyylllty.sertraline.logger.warningL
import io.github.zzzyyylllty.sertraline.util.serialize.parseToMap
// import org.yaml.snakeyaml.Yaml
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFile
import taboolib.common.platform.function.releaseResourceFolder
import taboolib.common.platform.function.warning
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.io.File


fun loadItemFiles() {
    infoL("Item_Load")
    var loadedCount = 0

    if (!File(getDataFolder(), "workspace").exists()) {
        warningL("Item_Load_Regen")
        releaseResourceFolder("workspace/default")
    }

    val files = File(getDataFolder(), "workspace").listFiles()
    if (files == null) {
        severeL("Item_Load_Not_Found")
        return
    }

    for (file in files) {
        if (file.isDirectory) {
            file.listFiles()?.forEach {
                if (loadItemFile(it)) loadedCount++
            }
        } else {
            if (loadItemFile(file)) loadedCount++
        }
    }

    infoL("Item_Load_Complete", loadedCount)
}

fun loadItemFile(file: File): Boolean {
    if (file.isDirectory) {
        file.listFiles()?.forEach {
            loadItemFile(it)
        }
        return false
    }

    val regex = (config["file-load.item"] ?: ".*").toString()
    if (!checkRegexMatch(file.name, regex)) {
        return false
    }

    return try {
        val map = multiExtensionLoader(file)

        if (map == null || map.isEmpty()) {
            severeL("Item_Load_Error_Empty", file.name)
            return false
        }

        for (entry in map.entries) {
            val key = entry.key
            val value = map[key]
            ItemLoadEvent(key, value as? Map<String, Any?>? ?: linkedMapOf(), linkedMapOf()).call()
        }

        true
    } catch (e: Exception) {
        severeL("Item_Load_Error_Parse", file.name, e.message ?: "Unknown error")
        false
    }
}