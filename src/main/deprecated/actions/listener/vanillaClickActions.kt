package io.github.zzzyyylllty.actions.listener

import io.github.zzzyyylllty.functions.item.getStlItem
import io.github.zzzyyylllty.functions.item.isStlItem
import org.bukkit.GameMode
import org.bukkit.event.player.PlayerInteractEvent
import taboolib.common.platform.event.SubscribeEvent

@SubscribeEvent
fun vanillaRightClickActions(e: PlayerInteractEvent) {
    if (isStlItem(e.player.inventory.itemInMainHand)) {
        getStlItem(e.player.inventory.itemInMainHand)
    }
}
