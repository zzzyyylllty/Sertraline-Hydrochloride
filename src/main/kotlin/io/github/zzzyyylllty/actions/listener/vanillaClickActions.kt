package io.github.zzzyyylllty.actions.listener

import org.bukkit.GameMode
import org.bukkit.event.player.PlayerInteractEvent
import taboolib.common.platform.event.SubscribeEvent

@SubscribeEvent
fun vanillaRightClickActions(e: PlayerInteractEvent) {
    if (e.player.gameMode == GameMode.ADVENTURE) {}
}
