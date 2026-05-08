package io.github.zzzyyylllty.sertraline.item.adapter

import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag

fun customDataAdapter(item: ItemStack, sItem: ModernSItem, player: Player?): ItemStack {
    val rawData = sItem.getDeepData("chotenatb") as? Map<String, Any?> ?: run {
        devLog("chotenatb data is null or not a map, skipping adapting.")
        return item
    }

    devLog("Adapting chotenatb data: $rawData")

    val tag = item.getItemTag(true)
    tag["chotenatb"] = listOf(transferBooleanToByte(rawData))

    return item.setItemTag(tag, true)
}
