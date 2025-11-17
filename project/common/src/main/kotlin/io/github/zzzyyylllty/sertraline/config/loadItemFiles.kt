package io.github.zzzyyylllty.sertraline.config

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.event.ItemLoadEvent
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


fun loadItemFiles() {
    infoL("Item_Load")
    if (!File(getDataFolder(), "workspace").exists()) {
        warningL("ItemLoad_Regen")
        releaseResourceFile("workspace/item.yml")
    }
    val files = File(getDataFolder(), "workspace").listFiles()
    if (files == null) {
        warningL("Item_Load_Not_Found")
        return
    }
    for (file in files) {
        // If directory load file in it...
        if (file.isDirectory) file.listFiles()?.forEach {
            loadItemFile(it)
        }
        else loadItemFile(file)
    }
}
fun loadItemFile(file: File) {
    devLog("Loading file ${file.name}")
    if (!checkRegexMatch(file.name, (config["file-load.item"] ?:".*").toString())) {
        devLog("${file.name} not match regex, skipping...")
        return
    }
    /*
    val yaml = Yaml()
    val obj = yaml.load<Map<String?, Any?>?>(file.inputStream())
    val entries = obj.entries
    for (it in entries) {
        val key = it.key ?: continue
        val value = obj[key]
        devLog("Key: $key")
        devLog("Value: $value")
        ItemLoadEvent(key, value as Map<String, Any?>, linkedMapOf()).call()
    }*/
    val map = multiExtensionLoader(file)
    if (map != null) for (it in map.entries) {
        val key = it.key
        val value = map[key]
        ItemLoadEvent(key, value as Map<String, Any?>, linkedMapOf()).call()
    } else {
        devLog("Map is null, skipping.")
    }
}