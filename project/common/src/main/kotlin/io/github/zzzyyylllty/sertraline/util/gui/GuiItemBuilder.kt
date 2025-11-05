package io.github.zzzyyylllty.sertraline.util.gui

import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.config.asListEnhanded
import io.github.zzzyyylllty.sertraline.util.minimessage.toComponent
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.module.lang.asLangText

fun GuiItemBuilder(nodeName: String,material: String): ItemStack {
    val item = ItemStack(XMaterial.valueOf(material).get() ?: Material.STONE)
    val itemMeta = item.itemMeta
    itemMeta.displayName(console.asLangText("Editor_${nodeName}_Name").toComponent())
    itemMeta.lore(console.asLangText("Editor_${nodeName}_Lore").asListEnhanded()?.toComponent())
    item.setItemMeta(itemMeta)
    return item
}