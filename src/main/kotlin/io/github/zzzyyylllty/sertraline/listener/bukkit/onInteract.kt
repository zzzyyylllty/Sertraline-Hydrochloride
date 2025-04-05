package io.github.zzzyyylllty.sertraline.listener.bukkit

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.consoleSender
import io.github.zzzyyylllty.sertraline.function.action.applyActions
import io.github.zzzyyylllty.sertraline.function.stats.refreshStat
import io.github.zzzyyylllty.sertraline.logger.infoS
import io.lumine.mythic.lib.api.event.PlayerAttackEvent
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.event.player.PlayerLoginEvent
import taboolib.common.env.RuntimeDependency
import taboolib.common.function.debounce
import taboolib.common.function.throttle
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync


val throttleAction = throttle<Player, ThrottleActionParam>(config["action.debounce-time"] as Long? ?: 500) { player, data ->
    player.applyActions(data.str, data.e)
}


data class ThrottleActionParam(
    val str: String,
    val e: Event,
)

@SubscribeEvent
fun onInteract(e: PlayerInteractEvent) {
    submitAsync {
    throttleAction(e.player, ThrottleActionParam("onInteract", e))
}
}

@SubscribeEvent
fun onLogin(e: PlayerLoginEvent) {
    submitAsync {
        throttleAction(e.player, ThrottleActionParam("onLogin", e))
}
}

@SubscribeEvent
fun onPreAttack(e: PrePlayerAttackEntityEvent) {
    submitAsync {
        throttleAction(e.player, ThrottleActionParam("onPreAttack", e))
}
}

@SubscribeEvent
fun onConsume(e: PlayerItemConsumeEvent) {
    submitAsync {
        throttleAction(e.player, ThrottleActionParam("onConsume", e))
        if(config.getBoolean("item.no-replacement-consume",false)) e.replacement = null
    }
}
@SubscribeEvent
fun onAttack(e: PlayerAttackEvent) {
    submitAsync {
        throttleAction(e.player, ThrottleActionParam("onAttack", e))
    }
}
