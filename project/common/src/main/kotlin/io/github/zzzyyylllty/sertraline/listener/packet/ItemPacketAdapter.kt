package io.github.zzzyyylllty.sertraline.listener.packet

import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.component.ComponentType
import com.github.retrooper.packetevents.protocol.component.ComponentTypes
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemLore
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.protocol.player.User
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatMessage
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUseItem
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerHeldItemChange
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetCursorItem
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPlayerInventory
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.util.loreformat.handleLoreFormat
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.nms.getItemTag
import kotlin.jvm.optionals.getOrDefault


class PacketEventsPacketListener : PacketListener {


    override fun onPacketSend(event: PacketSendEvent) {
        val player = event.getPlayer<Player>()

        when (event.packetType) {
            PacketType.Play.Server.WINDOW_ITEMS -> {
                val packet = WrapperPlayServerWindowItems(event)

                val items = packet.items
                if (items == null || items.isEmpty()) return
                items.forEach { handleItemStack(player, it) }

                event.lastUsedWrapper = packet
                event.markForReEncode(true)
            }

            PacketType.Play.Server.SET_PLAYER_INVENTORY -> {
                val packet = WrapperPlayServerSetPlayerInventory(event)

                handleItemStack(player, packet.stack)

                event.lastUsedWrapper = packet
                event.markForReEncode(true)
            }

            PacketType.Play.Server.SET_CURSOR_ITEM -> {
                val packet = WrapperPlayServerSetCursorItem(event)

                handleItemStack(player, packet.stack)

                event.lastUsedWrapper = packet
                event.markForReEncode(true)
            }

            PacketType.Play.Server.SET_SLOT -> {
                val packet = WrapperPlayServerSetSlot(event)

                handleItemStack(player, packet.item)

                event.lastUsedWrapper = packet
                event.markForReEncode(true)
            }
        }
    }
}



fun handleItemStack(
    player: Player,
    itemStack: com.github.retrooper.packetevents.protocol.item.ItemStack?
): com.github.retrooper.packetevents.protocol.item.ItemStack? {
    if (itemStack == null || itemStack.type == ItemTypes.AIR) return itemStack
    val item = SpigotConversionUtil.toBukkitItemStack(itemStack)
    val id = item.getItemTag()["SERTRALINE_ID"]?.asString() ?: return itemStack
    devLog("Handling Item Stack for item $id.")
    val sItem: ModernSItem = itemMap[id] ?: return itemStack
    handleLoreFormat(sItem, player)?.let { itemStack.setComponent(ComponentTypes.LORE, ItemLore(it)) }
    return itemStack
}