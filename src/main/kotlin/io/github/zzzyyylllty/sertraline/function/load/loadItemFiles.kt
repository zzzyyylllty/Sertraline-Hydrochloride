package io.github.zzzyyylllty.sertraline.function.load

import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.logger.infoL
import io.github.zzzyyylllty.sertraline.logger.warningL
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.common.platform.function.getDataFolder
import io.github.zzzyyylllty.sertraline.logger.infoS
import taboolib.common.platform.function.releaseResourceFile
import taboolib.module.lang.asLangText
import java.io.File
import kotlin.collections.set

fun loadItemFiles() {
    infoS(console.asLangText("ITEM_START_LOAD"))

    val files = File(getDataFolder(), "item").listFiles() ?: run {
        warningL("ITEM_LOAD_NOT_FOUND")
        return
    }

    if (!File(getDataFolder(), "item").exists()) {

        warningL("ITEM_LOAD_REGEN")
        releaseResourceFile("item/test.yml")

    }

    infoL("ITEM_LOAD_FOUND")

    for (file in files) {
        // If directory load file in it...
        if (file.isDirectory) file.listFiles()?.forEach {
            loadItemFile(it)
        }
        else loadItemFile(file)
    }
}

fun loadItemFile(file: File) {

    val config = YamlConfiguration.loadConfiguration(file)

    devLog(console.asLangText("DEBUG_LOADING_FILE", file.name))

    for (iroot in config.getKeys(false)) {

        devLog(console.asLangText("DEBUG_LOADING_ITEM", file.name.toString(), iroot.toString()))
        val item = loadItem(config, iroot)
        itemMap[iroot] = item

    }
}