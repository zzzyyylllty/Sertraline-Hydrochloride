package io.github.zzzyyylllty.sertraline.function.data

import io.github.zzzyyylllty.sertraline.config.asListEnhanded
import io.github.zzzyyylllty.sertraline.data.ItemData
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.function.kether.evalKether
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.getItemTag


fun getSavedData(item: ModernSItem?,itemStack: ItemStack?,evalDynamic: Boolean,player: Player?): ItemData {

    val itemVal = item?.data["sertraline:vals"] as Map<String, Any>?
    val itemVar = (itemStack?.getItemTag(true)["sertraline_data"] ?: item?.data["sertraline:vars"]) as Map<String, Any>?
    val itemDynamic = (item?.data["sertraline:dynamics"] as Map<String, Any>?)

    val data = ItemData(itemVal, itemVar, itemDynamic, item?.key)

    if (evalDynamic) {
        val newDynamic = mutableMapOf<String, Any>()
        itemDynamic?.forEach { entry ->
            entry.value.asListEnhanded()?.evalKether(player, data.collect())?.get()?.let { newDynamic[entry.key] = it }
            return ItemData(itemVal, itemVar, newDynamic, item.key)
        }
    }
    return data
}