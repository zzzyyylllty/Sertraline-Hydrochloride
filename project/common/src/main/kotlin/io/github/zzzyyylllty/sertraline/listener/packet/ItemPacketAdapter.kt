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
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag
import taboolib.platform.util.isAir

private val carriedItemFieldInContainerClick by lazy { if (VersionHelper().isUniversal) "carriedItem" else "item" }

/**
 * 从传入的虚拟物品中的Tag获取SERTRALINE_OITEM以恢复原物品
 * */
fun resumeItem(itemStack: ItemStack): ItemStack{
    return ItemStack.deserializeBytes(itemStack.getItemTag(true)["SERTRALINE_OITEM"] as? ByteArray ?: return itemStack)
}


@SubscribeEvent
fun onPacketReceive(event: taboolib.module.nms.PacketReceiveEvent) {

    /*
    when (event.packet.name) {
        "ServerboundContainerClickPacket" -> {
            devLog("Handling Packet: ${event.packet.name}")
            val itemStack = event.packet.read<Any?>(carriedItemFieldInContainerClick, remap = true) ?: return
            devLog("Packet ItemStack: $itemStack")
            val item = handleItemStack(event.player, asBukkitCopy(itemStack))
            devLog("Packet item: $item")
            val nmsItem = item?.let { asNMSCopy(it) }
            devLog("Packet NMSitem: $nmsItem")
            nmsItem?.let { event.packet.write(carriedItemFieldInContainerClick, it) }
        }
    }
    */

    /*
    val items = Int2ObjectOpenHashMap<Any>()
    items[114514] = event.packet.read(carriedItemFieldInContainerClick) ?: return
    if (VersionHelper().isUniversal) {
        event.packet.read<Map<Int, Any>>("changedSlots")?.let { items.putAll(it) }
    }
    devLog("Handling Packet: ${event.packet.name}")
    if (VersionHelper().isOrAbove12105()) {
        throw IllegalStateException("Unsupported minecraft version") // TODO 1.21.5
    } else {
        // 1.21.4-
        items.forEach { (i, item) ->
            // 恢复物品
            devLog("rewriting items $i - $item")
            val bItem = asBukkitCopy(item)
            if (!(bItem.isAir || bItem.isEmpty)) {
                items[i] = asNMSCopy(resumeItem(bItem))
                devLog("rewritted items $i - ${items[i]}")
            } else {
                devLog("Item is null or air,skipping rewriting.")
            }
        }
    }
    event.packet.write(carriedItemFieldInContainerClick, items[114514])
    if (VersionHelper().isUniversal) {
        items.remove(114514)
        event.packet.write("changedSlots", items)
    }

     */
}

@SubscribeEvent
fun onPacketSend(event: taboolib.module.nms.PacketSendEvent) {
    if (event.packet.name.contains("Container")) devLog("Finded Container Packet: ${event.packet.name}")
    when (event.packet.name) {
        "ClientboundContainerSetSlotPacket" -> {
            devLog("Handling Packet: ${event.packet.name}")
            val itemStack = event.packet.read<Any?>("itemStack", remap = true) ?: return
            devLog("Packet ItemStack: $itemStack")
            val item = handleItemStack(event.player, asBukkitCopy(itemStack))
            devLog("Packet item: $item")
            val nmsItem = item?.let { asNMSCopy(it) }
            devLog("Packet NMSitem: $nmsItem")
            nmsItem?.let { event.packet.write("itemStack", it) }
        }
        "ClientboundContainerSetContentPacket" -> {
            devLog("Handling Packet: ${event.packet.name}")
            val itemStack = event.packet.read<Any?>("carriedItem", remap = true) ?: return
            val itemList = event.packet.read<List<Any?>>("items", remap = true) ?: return
            val list = mutableListOf<Any>()
            for (item in itemList) {
                item?.let { (handleItemStack(event.player, asBukkitCopy(it)) ?: ItemStack(Material.AIR)).let { it1 -> list.add(asNMSCopy(it1)) } }
            }
            devLog("Packet ItemStack: $itemStack")
            val item = handleItemStack(event.player, asBukkitCopy(itemStack))
            devLog("Packet item: $item")
            val nmsItem = item?.let { asNMSCopy(it) }
            devLog("Packet NMSitem: $nmsItem")
            nmsItem?.let { event.packet.write("carriedItem", it) }
            event.packet.write("items", list)
        }
        "PacketPlayOutSetPlayerInventory","PacketPlayServerSetPlayerInventory","ClientboundSetPlayerInventoryPacket" -> {
            devLog("Handling Packet: ${event.packet.name}")
            val itemStack = event.packet.read<Pair<*, Any?>>("contents", remap = true) ?: return
            devLog("Packet ItemStack: $itemStack")
            val item = handleItemStack(event.player, asBukkitCopy(itemStack.second ?: return))
            devLog("Packet item: $item")
            val nmsItem = item?.let { asNMSCopy(it) }
            devLog("Packet NMSitem: $nmsItem")
            nmsItem?.let { event.packet.write("contents", it) }
        }
        "PacketPlayOutWindowItems","PacketPlayServerWindowItems" -> {
            devLog("Handling Packet: ${event.packet.name}")
            val itemStack = event.packet.read<Pair<*, Any?>>("carriedItem", remap = true) ?: return
            devLog("Packet ItemStack: $itemStack")
            val item = handleItemStack(event.player, asBukkitCopy(itemStack.second ?: return))
            devLog("Packet item: $item")
            val nmsItem = item?.let { asNMSCopy(it) }
            devLog("Packet NMSitem: $nmsItem")
            nmsItem?.let { event.packet.write("carriedItem", it) }
        }
        "PacketPlayInWindowClick" -> {
            devLog("Handling Packet: ${event.packet.name}")
            val itemStack = event.packet.read<Pair<*, Any?>>("changedSlots", remap = true) ?: return
            devLog("Packet ItemStack: $itemStack")
            val item = handleItemStack(event.player, asBukkitCopy(itemStack.second ?: return))
            devLog("Packet item: $item")
            val nmsItem = item?.let { asNMSCopy(it) }
            devLog("Packet NMSitem: $nmsItem")
            nmsItem?.let { event.packet.write("changedSlots", it) }
        }
        "PacketPlayOutSetSlot","PacketPlayServerSetSlot" -> {
            devLog("Handling Packet: ${event.packet.name}")
            val itemStack = event.packet.read<Pair<*, Any?>>("itemStack", remap = true) ?: return
            devLog("Packet ItemStack: $itemStack")
            val item = handleItemStack(event.player, asBukkitCopy(itemStack.second ?: return))
            devLog("Packet item: $item")
            val nmsItem = item?.let { asNMSCopy(it) }
            devLog("Packet NMSitem: $nmsItem")
            nmsItem?.let { event.packet.write("itemStack", it) }
        }
        "PacketPlayInSetCreativeSlot" -> {
            devLog("Handling Packet: ${event.packet.name}")
            val itemStack = event.packet.read<Pair<*, Any?>>("itemStack", remap = true) ?: return
            devLog("Packet ItemStack: $itemStack")
            val item = handleItemStack(event.player, asBukkitCopy(itemStack.second ?: return))
            devLog("Packet item: $item")
            val nmsItem = item?.let { asNMSCopy(it) }
            devLog("Packet NMSitem: $nmsItem")
            nmsItem?.let { event.packet.write("itemStack", it) }
        }
        "ClientboundSetCursorItemPacket" -> {
            devLog("Handling Packet: ${event.packet.name}")
            val itemStack = event.packet.read<Any?>("contents", remap = true) ?: return
            devLog("Packet ItemStack: $itemStack")
            val item = handleItemStack(event.player, asBukkitCopy(itemStack))
            devLog("Packet item: $item")
            val nmsItem = item?.let { asNMSCopy(it) }
            devLog("Packet NMSitem: $nmsItem")
            nmsItem?.let { event.packet.write("contents", it) }
        }
    }
}
fun handleItemStack(
    player: Player,
    input: ItemStack?
): ItemStack? {
    var item = input
    if (item == null || item.type == Material.AIR) return null
    val id = item.getItemTag()["SERTRALINE_ID"]?.asString() ?: return null
    val sItem: ModernSItem = itemMap[id] ?: return null
    handleLoreFormat(sItem, player)?.let { item.lore(it) }
    item = visualComponentSetter(item, sItem)
    val tag = item.getItemTag(true)
    tag.put("SERTRALINE_OITEM", input.serializeAsBytes())
    item.setItemTag(tag, true)
    return item
}

fun visualComponentSetter(item: ItemStack,sItem: ModernSItem): ItemStack {
    var item = item
    val filtered = sItem.data.filter {
        it.key.startsWith("visual:") && (it.value != null)
    }.toMutableMap()
    if (filtered.isEmpty()) return item
    if (filtered.contains("visual:material")) {
        item.type = XMaterial.valueOf((filtered["visual:material"] ?: "STONE").toString()).get() ?: Material.STONE
        filtered.remove("visual:material")
    }
    filtered.forEach {
        item = item.setComponent(it.key.replace("visual","minecraft"), it.value!!)
    }
    devLog("Visual Item: $item")
    return item

}