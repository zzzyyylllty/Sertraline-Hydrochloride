package io.github.zzzyyylllty.actions.listener

import io.github.zzzyyylllty.functions.item.isStlItem
import io.github.zzzyyylllty.functions.item.stlItem
import org.bukkit.GameMode
import org.bukkit.event.player.PlayerInteractEvent
import taboolib.common.platform.event.SubscribeEvent

@SubscribeEvent
fun vanillaRightClickActions(e: PlayerInteractEvent) {
    if (isStlItem(e.player.inventory.itemInMainHand)) {
        stlItem(e.player.inventory.itemInMainHand)
    }
}
