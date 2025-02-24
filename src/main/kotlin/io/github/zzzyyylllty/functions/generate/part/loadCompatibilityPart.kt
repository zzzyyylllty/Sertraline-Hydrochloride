package io.github.zzzyyylllty.functions.generate.part

import io.github.zzzyyylllty.SertralineHydrochloride.console
import io.github.zzzyyylllty.data.CompatibilityData
import io.github.zzzyyylllty.data.MMOItemsComp
import io.github.zzzyyylllty.data.SertralineItemData
import io.github.zzzyyylllty.debugMode.debugLog
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.warning
import taboolib.module.lang.asLangText

fun generateCompatbilityPart(item: ItemStack,data: SertralineItemData) : ItemStack {

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
/*
* testItem:
  compatibility:
    mmoitems: # 添加 MMOItems NBT以与大部分支持MI的插件自动进行兼容，使用该选项的同时如果下文属性使用生成MI的NBT，必须关闭MI的物品删除自动失效功能。
      type: 'CONSUMABLE'
      id: 'DEPAZ_PILLS'
      revid: 114514 # 如果你不想让你的物品被MI自动更新，关闭MI的自动更新功能或确保它不低于MI内同名同种物品。
      tier: RARE
* */