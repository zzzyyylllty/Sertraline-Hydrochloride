package io.github.zzzyyylllty.sertraline.item.adapter

import io.github.zzzyyylllty.sertraline.config.AdapterUtil
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.util.loreformat.handleLoreFormat
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XItemStack
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag

fun sertralineAdapter(item: ItemStack, sItem: ModernSItem, player: Player?): ItemStack {

    val dataMap = (sItem.getDeepData("sertraline:vars") as Map<String, Any?>?)?.toMutableMap()
    val tag = item.getItemTag(true)
    tag["sertraline_data"] = dataMap
    val item = item.setItemTag(tag, true)

    // lore format
    handleLoreFormat(sItem, player, item.lore(), false)?.let {
        item.lore(it)
    }

    return item

}



