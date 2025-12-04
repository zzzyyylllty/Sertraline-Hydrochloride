package io.github.zzzyyylllty.sertraline.item.adapter

import io.github.zzzyyylllty.sertraline.config.AdapterUtil
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.util.loreformat.handleLoreFormat
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag
import java.util.LinkedHashMap

fun customDataAdapter(item: ItemStack, sItem: ModernSItem, player: Player?): ItemStack {

    val tag = item.getItemTag(true)
    val map = (sItem.getDeepData("custom_data") as Map<String, Any>?)
    if (map == null || map.isEmpty()) {
        devLog("custom_data is null or empty, skipping adapting.")
        return item
    }
    (transferBooleanToByte(map) as Map<String, Any>).forEach {
        tag.put(it.key, it.value)
    }
    val item = item.setItemTag(tag, true)

    return item

}



