package io.github.zzzyyylllty.sertraline.config

import io.github.zzzyyylllty.sertraline.Sertraline.config
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
    infoL("Mapping_Load")
    releaseResourceFile("internal/mapping/mappings.yml", true)
    val files = File(getDataFolder(), "internal/mapping").listFiles()
    if (files == null) {
        warningL("Mapping_Load_Not_Found")
        return
    }
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

    if (file.isDirectory) file.listFiles()?.forEach {
        loadMappingFile(it)
    } else {

    if (!checkRegexMatch(file.name, (config["file-load.mapping"] ?:".*").toString())) {
        devLog("${file.name} not match regex, skipping...")
        return
    }
    val map = multiExtensionLoader(file) ?: return
    for ((key, value) in map) {
        val listValue = value as? List<*> ?: continue
        val mapped = listValue.filterIsInstance<String>()

        val existing = mappings[key]
        if (existing != null) {
            val merged = existing.toMutableList()
            merged.addAll(mapped)
            mappings[key] = merged
            devLog("Mapping Merged: $key - $merged")
        } else {
            mappings[key] = mapped
            devLog("Mapping Loaded: $key - $mapped")
        }
    }
    }
}