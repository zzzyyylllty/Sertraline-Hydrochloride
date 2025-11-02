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
import taboolib.module.nms.PacketSendEvent
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

    when (event.packet.name) {
        "ServerboundContainerClickPacket" -> {
            val player = event.player
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
                        items[i] = asNMSCopy(bItem.s2c(player))
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
        }
    }
}

@SubscribeEvent
fun onPacketSend(event: PacketSendEvent) {
    if (event.packet.name.contains("Container"))
        devLog("Finded Container Packet: ${event.packet.name}")

    when (event.packet.name) {
        "ClientboundContainerSetSlotPacket" -> {
            devLog("Handling Packet: ${event.packet.name}")
            handlePacketItem(event, "itemStack")
        }

        "ClientboundContainerSetContentPacket" -> {
            devLog("Handling Packet: ${event.packet.name}")
            handlePacketItem(event, "carriedItem", "items")
        }

        "PacketPlayOutSetPlayerInventory","PacketPlayServerSetPlayerInventory","ClientboundSetPlayerInventoryPacket" -> {
            devLog("Handling Packet: ${event.packet.name}")
            handlePacketItem(event, "contents")
        }

        "PacketPlayOutWindowItems","PacketPlayServerWindowItems" -> {
            devLog("Handling Packet: ${event.packet.name}")
            handlePacketItem(event, "carriedItem")
        }

        "PacketPlayInWindowClick" -> {
            devLog("Handling Packet: ${event.packet.name}")
            handlePacketItem(event, "changedSlots")
        }

        "PacketPlayOutSetSlot","PacketPlayServerSetSlot" -> {
            devLog("Handling Packet: ${event.packet.name}")
            handlePacketItem(event, "itemStack")
        }

        "PacketPlayInSetCreativeSlot" -> {
            devLog("Handling Packet: ${event.packet.name}")
            handlePacketItem(event, "itemStack")
        }

        "ClientboundSetCursorItemPacket" -> {
            devLog("Handling Packet: ${event.packet.name}")
            handlePacketItem(event, "contents")
        }
    }
}

private fun handlePacketItem(
    event: PacketSendEvent,
    readKey: String,
    readListKey: String? = null
) {

    val packet = event.packet
    val itemStack = packet.read<Any?>(readKey, remap = true) ?: return
    val itemList = readListKey?.let { packet.read<List<Any?>>(it, remap = true) ?: return } ?: emptyList()
    val newList = mutableListOf<Any>()
    for (item in itemList) {
        val processedItem = item?.let { asBukkitCopy(it).s2c(event.player) }
        newList.add(asNMSCopy(processedItem ?: ItemStack(Material.AIR)))
    }
    devLog("Packet ItemStack: $itemStack")
    val item = asBukkitCopy(itemStack).s2c(event.player)
    devLog("Packet item: $item")
    val nmsItem = asNMSCopy(item)
    devLog("Packet NMSitem: $nmsItem")
    nmsItem.let { packet.write(readKey, it) }
}


