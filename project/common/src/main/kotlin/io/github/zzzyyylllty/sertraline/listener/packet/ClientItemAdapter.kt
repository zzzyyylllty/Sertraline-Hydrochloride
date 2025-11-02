package io.github.zzzyyylllty.sertraline.listener.packet

import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.logger.warningS
import io.github.zzzyyylllty.sertraline.reflect.setComponent
import io.github.zzzyyylllty.sertraline.util.VersionHelper
import io.github.zzzyyylllty.sertraline.util.loreformat.handleLoreFormat
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.SubscribeEvent
import taboolib.library.xseries.XMaterial
import taboolib.module.nms.NMSItemTag.Companion.asBukkitCopy
import taboolib.module.nms.NMSItemTag.Companion.asNMSCopy
import taboolib.module.nms.PacketReceiveEvent
import taboolib.module.nms.PacketSendEvent
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag
import taboolib.platform.util.isAir

private val carriedItemFieldInContainerClick by lazy { if (VersionHelper().isUniversal) "carriedItem" else "item" }

/**
 * 从传入的虚拟物品中的Tag获取SERTRALINE_OITEM以恢复原物品
 * */
fun ItemStack.c2s(): ItemStack {
    return ItemStack.deserializeBytes(this.getItemTag(true)["SERTRALINE_OITEM"]?.asByteArray() ?: return this)
}
/**
 * 将服务端侧物品转化为虚拟物品
 * */
fun ItemStack.s2c(player: Player?): ItemStack {
    val item = this
    if (item.type == Material.AIR) return item
    val id = item.getItemTag()["SERTRALINE_ID"]?.asString() ?: return item
    val sItem = itemMap[id] ?: return item
    handleLoreFormat(sItem, player)?.let { item.lore(it) }
    val modifiedItem = visualComponentSetter(item, sItem)
    val tag = modifiedItem.getItemTag(true)
    tag.put("SERTRALINE_OITEM", this.serializeAsBytes())
    modifiedItem.setItemTag(tag, true)
    return modifiedItem
}

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

    devLog("Visual Item: $resultItem")
    return resultItem
}