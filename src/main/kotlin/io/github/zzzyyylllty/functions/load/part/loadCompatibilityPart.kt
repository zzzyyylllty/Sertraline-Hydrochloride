package io.github.zzzyyylllty.functions.load.part

import io.github.zzzyyylllty.SertralineHydrochloride.console
import io.github.zzzyyylllty.data.CompatibilityData
import io.github.zzzyyylllty.data.MMOItemsComp
import io.github.zzzyyylllty.debugMode.debugLog
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.common.platform.function.warning
import taboolib.module.lang.asLangText

fun loadCompatbilityPart(config: YamlConfiguration,root: String) : CompatibilityData? {

    val section = "$root.compatibility" // = testItem.compatibility
    val source = "${config.name}-$root"

    config.get(/* path = */ "$section.mmoitems") ?: run {
        debugLog(console.asLangText("debug.load.no_compatibility"))
        return null
    }

    val type = (config["$section.mmoitems.type"] ?: run {
        warning(console.asLangText("enable.load.error_compatibility", source, "mmoitems.type"))
        return null
    }).toString()
    val id = (config["$section.mmoitems.id"] ?: run {
        warning(console.asLangText("enable.load.error_compatibility", source, "mmoitems.id"))
        return null
    }).toString()
    val revid = (config["$section.mmoitems.revid"] ?: run {
        warning(console.asLangText("enable.load.error_compatibility", source, "mmoitems.revid"))
        return null
    }).toString().toLong()
    val tier = (config["$section.mmoitems.tier"] ?: run {
        warning(console.asLangText("enable.load.error_compatibility", source, "mmoitems.tier"))
        return null
    }).toString()

    return CompatibilityData(MMOItemsComp(type,id,revid,tier))
}
