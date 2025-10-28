package io.github.zzzyyylllty.sertraline.config

import io.github.zzzyyylllty.sertraline.Sertraline.mappings
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.logger.infoL
import io.github.zzzyyylllty.sertraline.logger.warningL
import org.yaml.snakeyaml.Yaml
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFile
import taboolib.common.platform.function.warning
import java.io.File


fun loadMappingFiles() {
    infoL("MappingLoad")
    if (!File(getDataFolder(), "internal/mapping").exists()) {
        warningL("MappingLoadRegen")
        releaseResourceFile("internal/mapping/mappings.yml")
    }
    val files = File(getDataFolder(), "internal/mapping").listFiles()
    if (files == null) {
        warningL("MappingLoadNotFound")
        return
    }
    infoL("MappingLoad")
    for (file in files) {
        // If directory load file in it...
        if (file.isDirectory) file.listFiles()?.forEach {
            loadMappingFile(it)
        }
        else loadMappingFile(file)
    }
}
fun loadMappingFile(file: File) {
    devLog("Loading file ${file.name}")

    val map = multiExtensionLoader(file)
    val entries = map?.entries ?: emptyList()
    for (it in entries) {
        val key = it.key
        val value = map?.get(key) as List<String>
        devLog("Mapping Loaded: $key - $value")
        mappings[key] = value
    }
}