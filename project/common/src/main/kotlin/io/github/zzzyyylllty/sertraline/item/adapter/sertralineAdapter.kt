package io.github.zzzyyylllty.sertraline.item.adapter

import io.github.zzzyyylllty.sertraline.config.AdapterUtil
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XItemStack
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag

fun sertralineAdapter(item: ItemStack, sItem: ModernSItem, player: Player?): ItemStack {

    val dataMap = (sItem.data["sertraline:vars"] as Map<String, Any?>?)?.toMutableMap()
    val tag = item.getItemTag(true)
    tag["sertraline_data"] = dataMap
    return item.setItemTag(tag, true)

}



