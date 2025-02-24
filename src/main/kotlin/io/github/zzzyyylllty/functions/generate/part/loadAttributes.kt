package io.github.zzzyyylllty.functions.generate.part

import io.github.zzzyyylllty.SertralineHydrochloride.console
import io.github.zzzyyylllty.data.*
import io.github.zzzyyylllty.debugMode.debugLog
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.lang.asLangText

fun generateAttributes(item: ItemStack, data: SertralineItemData) : ItemStack {

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
            atb["idef"] as String?,
            atb["override"].toString().toBoolean(),
            (atb["chance"] ?: 100.0).toString().toDouble(),
            (atb["amount"] ?: 1).toString(),
            AtbNbtSection(atb["nbt.save-in-nbt"].toString().toBoolean(),
                (atb["nbt.sertraline"] ?: !(atb["nbt.mmoitems"] as Boolean)).toString().toBoolean(),
                (atb["nbt.mmoitems"] ?: false).toString().toBoolean()),
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