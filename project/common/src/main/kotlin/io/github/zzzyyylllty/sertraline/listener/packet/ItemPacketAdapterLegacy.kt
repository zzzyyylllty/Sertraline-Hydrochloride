package io.github.zzzyyylllty.sertraline.listener.packet

import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCreativeInventoryAction
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetCursorItem
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPlayerInventory
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems
import com.google.common.base.Optional
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import io.github.zzzyyylllty.sertraline.Sertraline.consoleSender
import io.github.zzzyyylllty.sertraline.logger.sendStringAsComponent
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.isAir
import kotlin.jvm.optionals.getOrElse
import kotlin.jvm.optionals.getOrNull

class PacketEventsReceiveListener : PacketListener {
    override fun onPacketReceive(event: PacketReceiveEvent) {

        when (event.packetType) {
            PacketType.Play.Client.CLICK_WINDOW -> {
                val player = event.getPlayer<Player>()
                val packet = WrapperPlayClientClickWindow(event)

                packet.carriedItemStack = packet.carriedItemStack.let {
                    consoleSender.sendStringAsComponent("<rainbow>itemStack: $it")
                    val bItem = SpigotConversionUtil.toBukkitItemStack(it ?: return)
                    if (bItem.isAir || bItem.isEmpty) return
                    SpigotConversionUtil.fromBukkitItemStack(bItem.c2s())
                }

                event.lastUsedWrapper = packet
                event.markForReEncode(true)
            }
            PacketType.Play.Client.CREATIVE_INVENTORY_ACTION -> {
                val player = event.getPlayer<Player>()
                val packet = WrapperPlayClientCreativeInventoryAction(event)

                packet.itemStack = packet.itemStack.let {
                    consoleSender.sendStringAsComponent("<rainbow>itemStack: $it")
                    val bItem = SpigotConversionUtil.toBukkitItemStack(it ?: return)
                    if (bItem.isAir || bItem.isEmpty) return
                    SpigotConversionUtil.fromBukkitItemStack(bItem.c2s())
                }

                event.lastUsedWrapper = packet
                event.markForReEncode(true)
            }
        }
    }
}


// TODO BUFFER REWRITE
class PacketEventsSendListener : PacketListener {

    override fun onPacketSend(event: PacketSendEvent) {

        when (event.packetType) {
            PacketType.Play.Server.WINDOW_ITEMS -> {
                val player = event.getPlayer<Player>()
                val packet = WrapperPlayServerWindowItems(event)

                val item = packet.carriedItem.getOrNull()
                val items = packet.items
                val rewriteItem = item?.let { handleItemStack(player, it) }
                val rewriteItems = mutableListOf<com.github.retrooper.packetevents.protocol.item.ItemStack>()
                if (items == null || items.isEmpty()) return
                items.forEach { rewriteItems.add(handleItemStack(player, it)) }
                packet.items = rewriteItems
                rewriteItem?.let {
                    packet.setCarriedItem(it)
                }

                event.lastUsedWrapper = packet
                event.markForReEncode(true)
            }

            PacketType.Play.Server.SET_PLAYER_INVENTORY -> {
                val player = event.getPlayer<Player>()
                val packet = WrapperPlayServerSetPlayerInventory(event)

                packet.stack = handleItemStack(player, packet.stack)

                event.lastUsedWrapper = packet
                event.markForReEncode(true)
            }

            PacketType.Play.Server.SET_CURSOR_ITEM -> {
                val player = event.getPlayer<Player>()
                val packet = WrapperPlayServerSetCursorItem(event)

                packet.stack = handleItemStack(player, packet.stack)

                event.lastUsedWrapper = packet
                event.markForReEncode(true)
            }

            PacketType.Play.Server.SET_SLOT -> {
                val player = event.getPlayer<Player>()
                val packet = WrapperPlayServerSetSlot(event)

                packet.item = handleItemStack(player, packet.item)

                event.lastUsedWrapper = packet
                event.markForReEncode(true)
            }
        }
    }
}
fun handleItemStack(
    player: Player,
    itemStack: com.github.retrooper.packetevents.protocol.item.ItemStack
): com.github.retrooper.packetevents.protocol.item.ItemStack {
    if (itemStack.type == ItemTypes.AIR) return itemStack
    return SpigotConversionUtil.fromBukkitItemStack(SpigotConversionUtil.toBukkitItemStack(itemStack).s2c(player))
}
