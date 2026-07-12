package io.github.zzzyyylllty.sertraline.listener.update

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.function.update.checkAndUpdateItem
import io.github.zzzyyylllty.sertraline.function.update.updatePlayerItems
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.isAir

@SubscribeEvent
fun onPlayerJoin(e: PlayerJoinEvent) {
    if (!config.getBoolean("item-update.triggers.on-login", true)) return
    if (!config.getBoolean("item-update.enabled", true)) return
    updatePlayerItems(e.player)
}

@SubscribeEvent
fun onPlayerHeldItem(e: PlayerItemHeldEvent) {
    if (!config.getBoolean("item-update.triggers.on-hold", true)) return
    if (!config.getBoolean("item-update.enabled", true)) return
    val item = e.player.inventory.getItem(e.newSlot)
    if (item == null || item.isAir) return
    checkAndUpdateItem(e.player, item, e.newSlot)
}

@SubscribeEvent
fun onPlayerInteract(e: PlayerInteractEvent) {
    if (!config.getBoolean("item-update.triggers.on-interact", true)) return
    if (!config.getBoolean("item-update.enabled", true)) return
    val item = e.item
    if (item == null || item.isAir) return
    // 获取手持物品的槽位
    val slot = e.player.inventory.heldItemSlot
    checkAndUpdateItem(e.player, item, slot)
}
