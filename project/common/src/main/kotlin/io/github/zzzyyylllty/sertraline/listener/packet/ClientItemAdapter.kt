package io.github.zzzyyylllty.sertraline.listener.packet

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.config.asListEnhanded
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.item.itemSerializer
import io.github.zzzyyylllty.sertraline.impl.setComponent
import io.github.zzzyyylllty.sertraline.impl.setComponentNMS
import io.github.zzzyyylllty.sertraline.util.VersionHelper
import io.github.zzzyyylllty.sertraline.util.loreformat.handleLoreFormat
import io.github.zzzyyylllty.sertraline.util.minimessage.toComponent
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.module.lang.asLangText
import taboolib.module.nms.NMSItemTag.Companion.asBukkitCopy
import taboolib.module.nms.NMSItemTag.Companion.asNMSCopy
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag
import taboolib.platform.util.deserializeToItemStack
import taboolib.platform.util.serializeToByteArray
import io.github.zzzyyylllty.sertraline.util.toUpperCase

private val carriedItemFieldInContainerClick by lazy { if (VersionHelper().isUniversal) "carriedItem" else "item" }
private val packetLore by lazy { config.getBoolean("packet.packet-lore", true) }
private val packetComponent by lazy { config.getBoolean("packet.packet-component", true) }
val suffix by lazy { console.asLangText("Editor_Item_Suffix").asListEnhanded()?.toComponent() ?: emptyList() }

/**
 * 从传入的虚拟物品中的Tag获取SERTRALINE_OITEM以恢复原物品
 * */
fun ItemStack.c2s(): ItemStack {
    val tag = this.getItemTag()
    val deserialized = (tag["sertraline_oitem"]?.asByteArray())?.deserializeToItemStack()
        ?: run {
            devLog("OItem not found, skipping c2s.")
            return this
        }
    return deserialized
}
/**
 * 将服务端侧物品转化为虚拟物品
 * */
fun ItemStack.s2c(player: Player?): ItemStack {
    val oItem = this.clone()
    if (type.isAir) return oItem
    val tag = this.getItemTag(true)
    val id = tag["sertraline_id"]?.asString() ?: return oItem
    val sItem = itemSerializer(id, player) ?: return oItem
    if (packetLore) handleLoreFormat(sItem, player, this.lore(), true)?.let {
        this.lore(it)
    }
    val nmsItem = asNMSCopy(this)
    val modifiedItem = visualComponentSetterNMS(nmsItem, sItem, oItem.serializeToByteArray())
    if (tag["sertraline_browse_item"] != null && oItem.lore() != modifiedItem.lore()) { // 如果是展示物品且lore被修改过
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
        val filtered = (sItem.data["visual"] as? Map<*,*>)?.toMutableMap() ?: return asBukkitCopy(item)
        if (!filtered.isEmpty()) {

            filtered["material"]?.let {
                visualMaterial = XMaterial.valueOf(it.toString().toUpperCase()).get() ?: Material.STONE
                filtered.remove("material")
            }

            filtered.forEach { (key, value) ->
                resultItem.setComponentNMS("minecraft:$key", value!!)?.let { resultItem = it }
            }
        }
    }

    var resultBItem = asBukkitCopy(resultItem)
    val tag = resultBItem.getItemTag()
    tag["sertraline_oitem"] = serialized
    resultBItem = resultBItem.setItemTag(tag)
    visualMaterial?.let { resultBItem.type = it }
    return resultBItem
}

@Deprecated("Performance Issue")
fun visualComponentSetter(item: ItemStack, sItem: ModernSItem): ItemStack {
    val filtered = (sItem.data["visual"] as? Map<*,*>)?.toMutableMap() ?: return item
    if (filtered.isEmpty()) return item

    var resultItem = item
    filtered["material"]?.let {
        resultItem.type = XMaterial.valueOf(it.toString()).get() ?: Material.STONE
        filtered.remove("material")
    }

    if (!filtered.isEmpty()) {
        var nmsItem = asNMSCopy(resultItem)
        filtered.forEach { (key, value) ->
            nmsItem.setComponentNMS("minecraft:$key", value!!)?.let { nmsItem = it }
        }
        resultItem = asBukkitCopy(nmsItem)
    }

    return resultItem
}