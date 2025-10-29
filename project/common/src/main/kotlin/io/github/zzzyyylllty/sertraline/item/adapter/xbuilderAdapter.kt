package io.github.zzzyyylllty.sertraline.item.adapter

import io.github.zzzyyylllty.sertraline.config.AdapterUtil
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XItemStack

fun xbuilderAdapter(item: ItemStack,sItem: ModernSItem,player: Player?): ItemStack {
    val configMap = (sItem.config["xbuilder"] as Map<String, Any?>?)?.toMutableMap()
    configMap?.let { it["material"] = item.type.name }
    val xItem = configMap?.let { XItemStack.deserialize(it) } ?: item
    val c = AdapterUtil(sItem.data)
    val prefix = "xbuilder"
    val meta = xItem.itemMeta
    val name = c.getTextComponent("$prefix:name", sItem, player)
    if (name != null) meta.displayName(name)
    val lore = c.getTextComponentList("$prefix:lore", sItem, player)
    if (lore != null) meta.lore(lore)
    xItem.itemMeta = meta
    xItem.type = item.type
    return xItem
}


