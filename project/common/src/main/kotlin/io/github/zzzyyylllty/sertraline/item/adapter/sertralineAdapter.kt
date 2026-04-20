package io.github.zzzyyylllty.sertraline.item.adapter

import io.github.zzzyyylllty.sertraline.config.AdapterUtil
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.util.loreformat.handleLoreFormat
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import com.cryptomorin.xseries.XItemStack
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag

fun sertralineAdapter(item: ItemStack, sItem: ModernSItem, player: Player?): ItemStack {

    val dataMap = (sItem.getDeepData("sertraline:vars") as Map<String, Any?>?)?.toMutableMap()
    val tag = item.getItemTag(true)
    tag["sertraline_data"] = dataMap

    // 写入类型到NBT
    val typeData = sItem.getDeepData("sertraline:type")
    if (typeData != null) {
        val typeId = when (typeData) {
            is io.github.zzzyyylllty.sertraline.data.Type -> typeData.id
            else -> typeData.toString()
        }
        tag["sertraline_type"] = typeId
    }

    // 写入品质到NBT
    val tierData = sItem.getDeepData("sertraline:tier")
    if (tierData != null) {
        val tierId = when (tierData) {
            is io.github.zzzyyylllty.sertraline.data.Tier -> tierData.id
            else -> tierData.toString()
        }
        tag["sertraline_tier"] = tierId
    }

    val item = item.setItemTag(tag, true)

    // lore format
    handleLoreFormat(sItem, player, item.lore(), false)?.let {
        item.lore(it)
    }

    return item

}



