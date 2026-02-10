package io.github.zzzyyylllty.sertraline.util.dependencies

import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCreativeInventoryAction
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetCursorItem
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPlayerInventory
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import io.github.zzzyyylllty.sertraline.Sertraline.consoleSender
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.debugMode.devLogSync
import io.github.zzzyyylllty.sertraline.listener.packet.c2s
import io.github.zzzyyylllty.sertraline.listener.packet.s2c
import io.github.zzzyyylllty.sertraline.logger.sendStringAsComponent
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.coroutineScope
import org.bukkit.entity.Player
import taboolib.expansion.DispatcherType
import taboolib.expansion.DurationType
import taboolib.expansion.submitChain
import taboolib.platform.util.isAir
import kotlin.jvm.optionals.getOrNull

class PacketEventsReceiveListener : PacketListener {
    override fun onPacketReceive(event: PacketReceiveEvent) {

        when (event.packetType) {
            PacketType.Play.Client.CLICK_WINDOW -> {
                val player = event.getPlayer<Player>()
                val packet = WrapperPlayClientClickWindow(event)

                packet.carriedItemStack = packet.carriedItemStack.let {
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
//            PacketType.Play.Server.WINDOW_ITEMS -> {
//                val player = event.getPlayer<Player>()
//                val packet = WrapperPlayServerWindowItems(event)
//                val items = packet.items ?: return
//                if (items.isEmpty()) return
//
//                val carriedItem = packet.carriedItem.getOrNull()
//
////                devLog("=== WINDOW_ITEMS 数据包处理开始 ===")
////                devLog("玩家: ${player.name}")
////                devLog("物品总数: ${items.size}")
////                devLog("携带物品: ${carriedItem != null}")
//
//                items.forEach { item -> handleItemStack(player, item) }
//
////                submitChain(DispatcherType.SYNC) {
////                    val batchSize = 5
////                    devLog("开始分批处理物品，批次大小: $batchSize")
////
////                    val deferredBatches: List<ItemStack> = items.chunked(batchSize)
////                        .mapIndexed { batchIndex, subList ->
////                            devLog("处理第 ${batchIndex + 1} 批，物品数量: ${subList.size}")
////                            async {
////                                subList.mapIndexed { index, item ->
////                                    devLog("批次 $batchIndex - 物品 $index: ${item.type}")
////
////                                }
////                            }
////                        }
////                        .flatMap { deferredList ->
////                            deferredList
////                        }
////
////                    devLog("所有批次异步任务已提交，等待结果...")
//
//                    val rewriteCarriedItem: ItemStack? = carriedItem?.let { item ->
//                        devLog("开始处理携带物品: ${item.type}")
//                        handleItemStack(player, item)
//                    }
//                    packet.items = deferredBatches
//                        packet.setCarriedItem(it)
//                    event.lastUsedWrapper = packet
//                    event.markForReEncode(true)
//            }
//            PacketType.Play.Server.WINDOW_ITEMS -> {
//                val player = event.getPlayer<Player>()
//                val packet = WrapperPlayServerWindowItems(event)
//                val items = packet.items ?: return
//                if (items.isEmpty()) return
//
//                val carriedItem = packet.carriedItem.getOrNull()
//
//                submitChain(DispatcherType.SYNC) {
//                    val batchSize = 5
//                    val deferredBatches: List<ItemStack> = items.chunked(batchSize)
//                        .map { subList ->
//                            async {
//                                subList.map { handleItemStack(player, it) }
//                            }
//                        }
//
//                        .flatMap { deferredList ->
//                            deferredList
//                        }
//
//                    // 处理 carriedItem (单独异步任务)
//                    val rewriteCarriedItem: ItemStack? = carriedItem?.let { item ->
//                        handleItemStack(player, item)
//                    }
//                    while (deferredBatches.size >= items.size) {
//                        wait(50, DurationType.MILLIS)
//                    }
//
//                    packet.items = deferredBatches
//                    rewriteCarriedItem?.let { packet.setCarriedItem(it) }
//                    event.lastUsedWrapper = packet
//                    event.markForReEncode(true)
//                }
//
//            }

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
