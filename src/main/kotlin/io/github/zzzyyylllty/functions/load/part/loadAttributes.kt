package io.github.zzzyyylllty.functions.load.part

import com.willfp.eco.util.toSingletonList
import io.github.zzzyyylllty.SertralineHydrochloride.console
import io.github.zzzyyylllty.data.*
import io.github.zzzyyylllty.debugMode.debugLog
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.common.platform.function.warning
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.lang.asLangText

fun loadAttributes(config: YamlConfiguration,root: String) : List<SingleActionsData>? {

    val section = "$root.attributes" // = testItem.attributes
    val source = "${config.name}-$root"

    val attributesList : List<SingleActionsData> = emptyList()


    val attributes = (config[section] ?: run {
        debugLog(console.asLangText("debug.load.no_attributes"))
        return null
    }) as List<ConfigurationSection>

    for (atb in attributes) {
        SingleAttribute(
            AttributeSources.valueOf(atb["type"].toString()),
            atb["attr"].toString(),
            atb["idef"].toString(),
            atb["override"].toString().toBoolean(),
            atb["chance"].toString().toDouble(),
            atb["amount"].toString(),
            AtbNbtSection(atb["nbt.save-in-nbt"].toString().toBoolean(),
                atb["nbt.sertraline"].toString().toBoolean(),
                atb["nbt.mmoitems"].toString().toBoolean()),
                )
    }


    return attributesList
}
/*
*       - type: MYTHICLIB
      attr: ATTACK_SPEED
      amount: "1.6" # 固定1.6
      nbt:
        save-in-nbt: true
        original:
          sertraline: true
          mmoitems: false
* */