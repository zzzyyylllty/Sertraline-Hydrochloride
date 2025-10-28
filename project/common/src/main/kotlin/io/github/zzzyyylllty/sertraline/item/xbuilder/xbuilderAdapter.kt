package io.github.zzzyyylllty.sertraline.item.xbuilder

import io.github.zzzyyylllty.sertraline.config.AdapterUtil
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XItemStack

fun xbuilderAdapter(item: ItemStack,arg: ModernSItem): ItemStack {
    val i = item
    val map = (arg.config["xbuilder"] as Map<String, Any?>?)?.toMutableMap()
    map?.let { it["material"] = i.type.name }
    val xItem = map?.let { XItemStack.deserialize(it) } ?: i
    val c = AdapterUtil(arg.data)
    val prefix = "xbuilder"
    val meta = xItem.itemMeta
    val name = c.getTextComponent("$prefix:name")
    if (name != null) meta.displayName(name)
    val lore = c.getTextComponentList("$prefix:lore")
    if (lore != null) meta.lore(lore)
    xItem.itemMeta = meta
    xItem.type = i.type
    return xItem
}
