package io.github.zzzyyylllty.sertraline.function.load

import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.data.DepazItems
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.logger.infoL
import io.github.zzzyyylllty.sertraline.logger.warningL
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.info
import taboolib.common.platform.function.releaseResourceFile
import taboolib.common.platform.function.warning
import taboolib.module.lang.asLangText
import java.io.File

fun loadItemFile() {
    info(console.asLangText("ITEM_START_LOAD"))

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

        val config = YamlConfiguration.loadConfiguration(file)

        devLog("DEBUG_LOADING_FILE", file.name)

        for (iroot in config.getKeys(false)) {

            devLog("DEBUG_LOADING_ITEM", file.name, iroot)
            val item = loadMaterialPart(config, iroot)
            itemMap[iroot] = DepazItems(
                id = iroot,
                originalItem = item,
                actions = linkedMapOf()
            )

        }
    }
}
