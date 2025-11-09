package io.github.zzzyyylllty.sertraline.listener.packet

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.Sertraline.consoleSender
import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.config.asListEnhanded
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.item.sertralineItemBuilder
import io.github.zzzyyylllty.sertraline.logger.sendStringAsComponent
import io.github.zzzyyylllty.sertraline.logger.warningS
import io.github.zzzyyylllty.sertraline.reflect.getComponent
import io.github.zzzyyylllty.sertraline.reflect.getComponentNMS
import io.github.zzzyyylllty.sertraline.reflect.setComponent
import io.github.zzzyyylllty.sertraline.reflect.setComponentNMS
import io.github.zzzyyylllty.sertraline.util.VersionHelper
import io.github.zzzyyylllty.sertraline.util.loreformat.handleLoreFormat
import io.github.zzzyyylllty.sertraline.util.minimessage.toComponent
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.module.configuration.util.asMap
import taboolib.module.lang.asLangText
import taboolib.module.nms.NMSItemTag.Companion.asBukkitCopy
import taboolib.module.nms.NMSItemTag.Companion.asNMSCopy
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag
import taboolib.platform.util.deserializeToItemStack
import taboolib.platform.util.serializeToByteArray

private val carriedItemFieldInContainerClick by lazy { if (VersionHelper().isUniversal) "carriedItem" else "item" }
private val packetLore by lazy { config.getBoolean("packet.packet-lore", true) }
private val packetComponent by lazy { config.getBoolean("packet.packet-component", true) }
val suffix by lazy { console.asLangText("Editor_Item_Suffix").asListEnhanded()?.toComponent() ?: emptyList() }

/**
 * 从传入的虚拟物品中的Tag获取SERTRALINE_OITEM以恢复原物品
 * */
fun ItemStack.c2s(): ItemStack {
    val deserialized = (this.getItemTag()["sertraline_oitem"]?.asCompound()?.get("itemstack")?.asByteArray())?.deserializeToItemStack() ?: return this
    return deserialized
}
/**
 * 将服务端侧物品转化为虚拟物品
 * */
fun ItemStack.s2c(player: Player?): ItemStack {
    val oItem = this.clone()
    if (type == Material.AIR) return oItem
    val tag = this.getItemTag()
    val id = tag["sertraline_id"]?.asString() ?: return oItem
    val sItem = itemMap[id] ?: return oItem
    var loreModified = false
    if (packetLore) handleLoreFormat(sItem, player)?.let {
        this.lore(it)
        loreModified = true
    }
    val nmsItem = asNMSCopy(this)
    val modifiedItem = visualComponentSetterNMS(nmsItem, sItem, oItem.serializeToByteArray())
    if (tag["sertraline_browse_item"] != null && loreModified) {
        val lore = modifiedItem.lore()?.toMutableList() ?: mutableListOf()
        lore.addAll(suffix)
        modifiedItem.lore(lore)
    }
    return modifiedItem
}
fun visualComponentSetterNMS(item: Any, sItem: ModernSItem,serialized: ByteArray): ItemStack {

    var resultItem = item
    var visualMaterial: Material? = null

    if (packetComponent) {
        val filtered = sItem.data.filter { it.key.startsWith("visual:") && it.value != null }.toMutableMap()
        if (filtered.isEmpty()) return asBukkitCopy(item)

        filtered["visual:material"]?.let {
            visualMaterial = XMaterial.valueOf(it.toString().toUpperCase()).get() ?: Material.STONE
            filtered.remove("visual:material")
        }

        filtered.forEach { (key, value) ->
            resultItem = resultItem.setComponentNMS(key.replace("visual", "minecraft"), value!!)
        }
    }

    var resultBItem = asBukkitCopy(resultItem)
    val tag = resultBItem.getItemTag()
    tag["sertraline_oitem.itemstack"] = serialized
    resultBItem = resultBItem.setItemTag(tag)
    visualMaterial?.let { resultBItem.type = it }
    return resultBItem
}

@Deprecated("Performance Issue")
fun visualComponentSetter(item: ItemStack, sItem: ModernSItem): ItemStack {
    val filtered = sItem.data.filter { it.key.startsWith("visual:") && it.value != null }.toMutableMap()
    if (filtered.isEmpty()) return item

    var resultItem = item
    filtered["visual:material"]?.let {
        resultItem.type = XMaterial.valueOf(it.toString()).get() ?: Material.STONE
        filtered.remove("visual:material")
    }

    filtered.forEach { (key, value) ->
        resultItem = resultItem.setComponent(key.replace("visual", "minecraft"), value!!)
    }

    return resultItem
}