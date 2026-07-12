package io.github.zzzyyylllty.sertraline.config

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.tiers
import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.data.Tier
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.logger.infoL
import io.github.zzzyyylllty.sertraline.logger.infoS
import io.github.zzzyyylllty.sertraline.logger.severeL
import io.github.zzzyyylllty.sertraline.logger.warningL
import io.github.zzzyyylllty.sertraline.util.serialize.parseToMap
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFile
import taboolib.common.platform.function.warning
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.io.File

fun loadTierFiles() {
    infoL("Tier_Load")
    if (!File(getDataFolder(), "tiers").exists()) {
        warningL("Tier_Load_Regen")
        releaseResourceFile("tiers/default.yml")
    }
    val files = File(getDataFolder(), "tiers").listFiles()
    if (files != null) {
        for (file in files) {
            // If directory load file in it...
            if (file.isDirectory) file.listFiles()?.forEach {
                loadTierFile(it)
            }
            else loadTierFile(file)
        }
    }
}

fun loadTierFile(file: File) {
    devLog("Loading tier file ${file.name}")

    if (file.isDirectory) file.listFiles()?.forEach {
        loadTierFile(it)
    } else {
        if (!checkRegexMatch(file.name, (config["file-load.tier"] ?: ".*").toString())) {
            devLog("${file.name} not match regex, skipping...")
            return
        }
        val raw = multiExtensionLoader(file) ?: return
        val map = TemplateManager.resolveInMap(raw)
        if (map != null) {
            if (map.isEmpty()) {
                severeL("Config_Load_Error_Empty", file.name)
                return
            }
            for (it in map.entries) {
                val key = it.key
                val value = map[key]
                loadTier(key, value as Map<String, Any?>? ?: linkedMapOf())
            }
        }
    }
}

fun loadTier(key: String, arg: Map<String, Any?>) {
    val c = ConfigUtil
    val tier = Tier(
        id = key,
        name = c.getDeep(arg, "name") as? String ?: key,
        description = c.getDeep(arg, "description") as? String ?: "",
        color = c.getDeep(arg, "color") as? String ?: "<white>",
        weight = (c.getDeep(arg, "weight") as? Number)?.toInt() ?: 1,
        extra = c.getDeep(arg, "extra") as? Map<String, Any?> ?: mapOf()
    )
    tiers[key] = tier
    devLog("Loaded tier: $key")
}