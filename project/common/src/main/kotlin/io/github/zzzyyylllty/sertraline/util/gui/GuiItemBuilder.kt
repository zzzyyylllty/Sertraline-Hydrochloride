package io.github.zzzyyylllty.sertraline.util.gui

import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.compat.PlatformCompat
import io.github.zzzyyylllty.sertraline.config.asListEnhanced
import io.github.zzzyyylllty.sertraline.util.minimessage.toComponent
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import com.cryptomorin.xseries.XMaterial
import taboolib.module.lang.asLangText
import io.github.zzzyyylllty.sertraline.util.toUpperCase

data class GuiItem(
    val nodeName: String,
    val material: String,
    val name: String = console.asLangText("Editor_${nodeName}_Name"),
    val lore: List<String> = console.asLangText("Editor_${nodeName}_Lore").asListEnhanced() ?: emptyList(),
)



fun GuiItem.build(): ItemStack {
    val item = ItemStack(XMaterial.valueOf(material.toUpperCase()).get() ?: Material.STONE)
    item.itemMeta?.let { itemMeta ->
        val lore = lore.asListEnhanced()?.toComponent()
        PlatformCompat.setDisplayName(itemMeta, name.toComponent())
        PlatformCompat.setLore(itemMeta, lore ?: emptyList())
        item.setItemMeta(itemMeta)
    }
    return item
}