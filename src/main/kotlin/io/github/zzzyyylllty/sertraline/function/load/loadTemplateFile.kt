package io.github.zzzyyylllty.sertraline.function.load

import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.Sertraline.templateMap
import io.github.zzzyyylllty.sertraline.data.DepazItems
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.logger.infoL
import io.github.zzzyyylllty.sertraline.logger.warningL
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.common.platform.function.getDataFolder
import io.github.zzzyyylllty.sertraline.function.internalMessage.infoS
import taboolib.common.platform.function.releaseResourceFile
import taboolib.module.lang.asLangText
import java.io.File

fun loadTemplateFiles() {
    infoS(console.asLangText("TEMPLATE_START_LOAD"))

    val files = File(getDataFolder(), "template").listFiles() ?: run {
        warningL("TEMPLATE_LOAD_NOT_FOUND")
        return
    }

    if (!File(getDataFolder(), "template").exists()) {

        warningL("TEMPLATE_LOAD_REGEN")
        releaseResourceFile("template/example.yml")

    }

    infoL("TEMPLATE_LOAD_FOUND")

    for (file in files) {

        for (file in files) {
            // If directory load file in it...
            if (file.isDirectory) file.listFiles()?.forEach {
                loadTemplateFile(it)
            }
            else loadTemplateFile(file)
        }
    }
}

fun loadTemplateFile(file: File) {

    val config = YamlConfiguration.loadConfiguration(file)

    devLog(console.asLangText("DEBUG_LOADING_FILE", file.name))

    for (iroot in config.getKeys(false)) {

        devLog(console.asLangText("DEBUG_LOADING_TEMPLATE", file.name, iroot))
        val template = loadTemplate(config, iroot)
        templateMap[iroot] = template as ConfigurationSection
    }
}