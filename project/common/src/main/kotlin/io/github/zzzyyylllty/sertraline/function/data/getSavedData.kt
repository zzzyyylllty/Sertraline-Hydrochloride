package io.github.zzzyyylllty.sertraline.function.data

import io.github.zzzyyylllty.sertraline.config.asListEnhanced
import io.github.zzzyyylllty.sertraline.data.ItemData
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.function.kether.evalKether
import io.github.zzzyyylllty.sertraline.util.ItemTagUtil.parseMapNBT
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.getItemTag


fun getSavedData(item: ModernSItem?,itemStack: ItemStack?,evalDynamic: Boolean,player: Player?): ItemData {

    val itemVal = item?.getDeepData("sertraline:vals") as Map<String, Any?>?
    val itemVar = mutableMapOf<String, Any?>()
    ((itemStack?.getItemTag(true)["sertraline_data"]?.parseMapNBT()))?.let {
        (it).let { it ->
            devLog("SavedData: $it")
            itemVar.putAll(it)
        }
    } ?: run {
        (item?.getDeepData("sertraline:vars") as Map<String, Any?>?)?.let { itemVar.putAll(it) }
    }
    val itemDynamic = (item?.getDeepData("sertraline:dynamics") as Map<String, Any?>?)

    val data = ItemData(itemVal, itemVar, itemDynamic, item?.key)

    if (evalDynamic) {
        val newDynamic = mutableMapOf<String, Any>()
        itemDynamic?.forEach { entry ->
            entry.value.asListEnhanced()?.evalKether(player, data.collect())?.get()?.let { newDynamic[entry.key] = it }
            return ItemData(itemVal, itemVar, newDynamic, item.key)
        }
    }
    return data
}

fun ItemStack.getSertralineId(): String? {

    val tag = this.clone().getItemTag(true)
    return tag["sertraline_id"]?.asString()
}