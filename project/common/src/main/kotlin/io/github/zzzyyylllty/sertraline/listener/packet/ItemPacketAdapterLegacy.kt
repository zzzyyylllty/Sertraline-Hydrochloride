package io.github.zzzyyylllty.sertraline.listener.packet

import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.component.ComponentTypes
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemLore
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCreativeInventoryAction
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientHeldItemChange
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPickItem
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetCursorItem
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPlayerInventory
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.reflect.setComponent
import io.github.zzzyyylllty.sertraline.util.loreformat.handleLoreFormat
import org.bukkit.Material
import org.bukkit.entity.Player
import taboolib.platform.util.isAir

class PacketEventsPacketListener : PacketListener {


    override fun onPacketReceive(event: com.github.retrooper.packetevents.event.PacketReceiveEvent) {
        val player = event.getPlayer<Player>()

        when (event.packetType) {
            PacketType.Play.Client.CLICK_WINDOW -> {
                val packet = WrapperPlayClientClickWindow(event)

                val rewrite = packet.carriedItemStack.let {
                    val bItem = SpigotConversionUtil.toBukkitItemStack(it)
                    if (bItem.isAir || bItem.isEmpty) return
                    bItem.c2s()
                }
                packet.carriedItemStack = SpigotConversionUtil.fromBukkitItemStack(rewrite)

                event.lastUsedWrapper = packet
                event.markForReEncode(true)
            }
            PacketType.Play.Client.CREATIVE_INVENTORY_ACTION -> {
                val packet = WrapperPlayClientCreativeInventoryAction(event)

                val rewrite = packet.itemStack.let {
                    val bItem = SpigotConversionUtil.toBukkitItemStack(it)
                    if (bItem.isAir || bItem.isEmpty) return
                    bItem.c2s()
                }
                packet.itemStack = SpigotConversionUtil.fromBukkitItemStack(rewrite)

                event.lastUsedWrapper = packet
                event.markForReEncode(true)
            }
        }
    }
    override fun onPacketSend(event: PacketSendEvent) {
        val player = event.getPlayer<Player>()

        when (event.packetType) {
            PacketType.Play.Server.WINDOW_ITEMS -> {
                val packet = WrapperPlayServerWindowItems(event)

                val items = packet.items
                val rewrite = mutableListOf<com.github.retrooper.packetevents.protocol.item.ItemStack>()
                if (items == null || items.isEmpty()) return
                items.forEach { rewrite.add(handleItemStack(player, it)) }
                packet.items = rewrite

                event.lastUsedWrapper = packet
                event.markForReEncode(true)
            }

            PacketType.Play.Server.SET_PLAYER_INVENTORY -> {
                val packet = WrapperPlayServerSetPlayerInventory(event)

                packet.stack = handleItemStack(player, packet.stack)

                event.lastUsedWrapper = packet
                event.markForReEncode(true)
            }

            PacketType.Play.Server.SET_CURSOR_ITEM -> {
                val packet = WrapperPlayServerSetCursorItem(event)

                packet.stack = handleItemStack(player, packet.stack)

                event.lastUsedWrapper = packet
                event.markForReEncode(true)
            }

            PacketType.Play.Server.SET_SLOT -> {
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
