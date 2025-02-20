package io.github.zzzyyylllty.functions.load

import io.github.zzzyyylllty.SertralineHydrochloride.console
import io.github.zzzyyylllty.debugMode.debugLog
import io.github.zzzyyylllty.functions.load.part.loadCompatbilityPart
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.info
import taboolib.common.platform.function.releaseResourceFile
import taboolib.common.platform.function.warning
import taboolib.module.lang.asLangText
import java.io.File

fun loadConfigs() {

    info(console.asLangText("enable.load.start"))

    val files = File(getDataFolder(), "items").listFiles() ?: run {
        warning(console.asLangText("enable.load.not_found"))
        return
    }

    info(console.asLangText("enable.load.find"), files.size)

    if (!File(getDataFolder(), "quests").exists()) {

        warning(console.asLangText("enable.load.regen"))
        releaseResourceFile("items/consumable_item_examples.yml")
        releaseResourceFile("items/full_item_examples.yml")

    }

    for (file in files) {

        val config = YamlConfiguration.loadConfiguration(file)

        debugLog(console.asLangText("debug.load.loading_file"), file.name)

        for (iroot in config.getKeys(false)) {

            debugLog(console.asLangText("debug.load.loading_item"), file.name, iroot)

            val compatibilityData = loadCompatbilityPart(config, iroot)

        }
    }
}
