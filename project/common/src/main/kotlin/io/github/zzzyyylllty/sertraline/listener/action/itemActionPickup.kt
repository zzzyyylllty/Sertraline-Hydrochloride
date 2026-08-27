package io.github.zzzyyylllty.sertraline.listener.action

import io.github.zzzyyylllty.sertraline.util.ActionHelper.throttleAction
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityPickupItemEvent
import taboolib.common.platform.event.SubscribeEvent

// EntityPickupItemEvent is a 1.13+ class, absent from the legacy12 (v11200) compile surface; this standalone file is excluded entirely in legacy12 builds
@SubscribeEvent
fun onPickup(e: EntityPickupItemEvent) {
    val player = e.entity as? Player ?: return
    val uuid = player.uniqueId.toString()
    throttleAction(ThrottleActionLink(uuid, "onPickUp"), ThrottleActionParam(player, e, e, e.item.itemStack))
}
