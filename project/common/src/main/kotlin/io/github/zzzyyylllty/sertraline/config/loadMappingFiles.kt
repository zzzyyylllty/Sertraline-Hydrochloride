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

    val map = multiExtensionLoader(file)
    if (!checkRegexMatch(file.name, (config["file-load.mapping"] ?:".*").toString())) {
        devLog("${file.name} not match regex, skipping...")
        return
    }
    val entries = map?.entries ?: emptyList()
    for (it in entries) {
        val key = it.key
        val value = map?.get(key) as List<String>

        // 判断是否mappings里面已经包含对应的键。
        if (mappings.containsKey(key)) {

            val merged = mappings[key]?.toMutableList()
            merged?.addAll(value)

            // 合并重复的键
            merged?.let { mappings[key] = it }

            devLog("Mapping Merged: $key - $merged")

        } else {
            mappings[key] = value
            devLog("Mapping Loaded: $key - $value")
        }
    }
    }
}