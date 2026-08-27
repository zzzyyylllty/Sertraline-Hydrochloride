package io.github.zzzyyylllty.sertraline.listener.action

import io.github.zzzyyylllty.sertraline.util.ActionHelper.throttleAction
import org.bukkit.event.player.PlayerRiptideEvent
import taboolib.common.platform.event.SubscribeEvent

// PlayerRiptideEvent is a 1.13+ class, absent from the legacy12 (v11200) compile surface; this standalone file is excluded entirely in legacy12 builds
@SubscribeEvent
fun onShootTrident(e: PlayerRiptideEvent) {
    val player = e.player
    val uuid = player.uniqueId.toString()
    val param = ThrottleActionParam(player, e, null, e.item)
    throttleAction(ThrottleActionLink(uuid, "onShootTrident"), param)
}
