package io.github.zzzyyylllty.functions.generate.part

import io.github.zzzyyylllty.SertralineHydrochloride.console
import io.github.zzzyyylllty.data.SertralineItemData
import io.github.zzzyyylllty.data.SingleData
import io.github.zzzyyylllty.data.SingleDataTypes
import io.github.zzzyyylllty.debugMode.debugLog
import io.github.zzzyyylllty.functions.generate.resolveItemStack
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.warning
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.lang.asLangText

fun generateSertralinePart(item: ItemStack,data: SertralineItemData) : ItemStack {

    val section = "$root.sertraline" // = testItem.sertraline
    val source = "${config.name}-$root"

    config.get(section) ?: run {
        debugLog(console.asLangText("debug.load.no_sertraline"))
        return null
    }

    val name = (config["$section.name"] ?: run {
        warning(console.asLangText("enable.load.error_sertraline", source, "mmoitems.type"))
        return null
    }).toString()
    val material = resolveItemStack((config["$section.material"] ?: run {
        warning(console.asLangText("enable.load.error_sertraline", source, "mmoitems.id"))
        return null
    }).toString(), source)
    val nbtList = (config["$section.nbts"] ?: run {
        warning(console.asLangText("enable.load.error_sertraline", source, "mmoitems.nbts"))
        return null
    }) as List<ConfigurationSection>
    val nbts = LinkedHashMap<String, Any?>()
    for (l in nbtList) {
        nbts[l["node"].toString()] = l["value"]
    }
    val lore = (config["$section.lore"] ?: run {
        warning(console.asLangText("enable.load.error_sertraline", source, "mmoitems.type"))
        return null
    }) as List<String>
    val model = (config["$section.model"]).toString().toDouble()
    val updateId = (config["$section.update-id"] ?: 1.0).toString().toDouble()
    val fixedList = (config["$section.fixed"] ?: run {
        warning(console.asLangText("enable.load.error_sertraline", source, "mmoitems.nbts"))
        return null
    }) as List<ConfigurationSection>
    val fixedData = LinkedHashMap<String, SingleData>()
    for (l in fixedList) {
        fixedData[l["idef"].toString()] = SingleData(SingleDataTypes.valueOf(l["type"].toString()), l["value"])
    }

    val valList = (config["$section.vals"] ?: run {
        warning(console.asLangText("enable.load.error_sertraline", source, "mmoitems.nbts"))
        return null
    }) as List<ConfigurationSection>
    val valData = LinkedHashMap<String, SingleData>()
    for (l in valList) {
        valData[l["idef"].toString()] = SingleData(SingleDataTypes.valueOf(l["type"].toString()), l["value"])
    }

    val varList = (config["$section.vars"] ?: run {
        warning(console.asLangText("enable.load.error_sertraline", source, "mmoitems.nbts"))
        return null
    }) as List<ConfigurationSection>
    val varData = LinkedHashMap<String, SingleData>()
    for (l in varList) {
        varData[l["idef"].toString()] = SingleData(SingleDataTypes.valueOf(l["type"].toString()), l["value"])
    }

    return SertralineItemData(name,material,nbts,lore,model,updateId,fixedData,valData,varData)
}
/*
*   sertraline: # sertraline 属性
    name: '一个非常牛逼的物品'
    material: STONE
    nbts:
      - node: package
        value: needy.girl.overdose
    lore:
    model: 10000
    update-id: 1
    fixed:
      - idef: attack
        type: value
        values: 1
    vals:
    vars:
* */