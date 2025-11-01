package io.github.zzzyyylllty.sertraline.listener.packet
/*
import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.component.ComponentTypes
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemLore
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import com.github.retrooper.packetevents.protocol.packettype.PacketType
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
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.module.nms.getItemTag


@Deprecated("Packetevents nmsl")
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

           
fun handleItemStack(
    player: Player,
    itemStack: com.github.retrooper.packetevents.protocol.item.ItemStack?
): com.github.retrooper.packetevents.protocol.item.ItemStack? {
    if (itemStack == null || itemStack.type == ItemTypes.AIR) return itemStack
    var item = SpigotConversionUtil.toBukkitItemStack(itemStack)
    val id = item.getItemTag()["SERTRALINE_ID"]?.asString() ?: return itemStack
    devLog("Handling Item Stack for item $id.")
    val sItem: ModernSItem = itemMap[id] ?: return itemStack
    handleLoreFormat(sItem, player)?.let { itemStack.setComponent(ComponentTypes.LORE, ItemLore(it)) }
    item = SpigotConversionUtil.toBukkitItemStack(itemStack)
    item = visualComponentSetter(item, sItem)
    return SpigotConversionUtil.fromBukkitItemStack(item)
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

}*/