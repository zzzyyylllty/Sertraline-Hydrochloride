package io.github.zzzyyylllty.sertraline.load

import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.logger.infoL
import io.github.zzzyyylllty.sertraline.logger.warningL
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFile
import taboolib.module.configuration.Configuration
import taboolib.module.lang.asLangText
import java.io.File
import kotlin.collections.set


fun loadItemFiles() {
    infoL("ItemLoad")
    if (!File(getDataFolder(), "workspace").exists()) {
        warningL("ItemLoadRegen")
        releaseResourceFile("workspace/chotenpack/test.yml")
    }
    val files = File(getDataFolder(), "workspace").listFiles()
    if (files == null) {
        warningL("ItemLoadNotFound")
        return
    }
    infoL("ItemLoad")
    for (file in files) {
        // If directory load file in it...
        if (file.isDirectory) file.listFiles()?.forEach {
            loadItemFile(it)
        }
        else loadItemFile(file)
    }
}
fun loadItemFile(file: File) {
    val config = Configuration.loadFromFile(file)

    devLog(console.asLangText("DebugLoadingFile", file.name))

    for (iroot in config.getKeys(false)) {

        devLog(console.asLangText("DebugLoadingItem", file.name.toString(), iroot.toString()))
        val item = loadItem(config, iroot)
        itemMap[iroot.getKey()] = item

    }
}