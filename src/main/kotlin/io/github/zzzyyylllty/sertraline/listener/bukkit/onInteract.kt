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
import org.bukkit.inventory.ItemStack
import taboolib.common.env.RuntimeDependency
import taboolib.common.function.debounce
import taboolib.common.function.throttle
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync


val throttleAction = throttle<ThrottleActionLink, ThrottleActionParam>(config.getLong("action.throttle-time", 500)){ link, data ->
    link.p.applyActions(link.str, data.e)
}


data class ThrottleActionLink(
    val p: Player,
    val str: String,
)
data class ThrottleActionParam(
    val e: Event,
    val i2 : ItemStack? = null,
    val islot : Int? = null
)

@SubscribeEvent
fun onInteract(e: PlayerInteractEvent) {
    submitAsync {
    throttleAction(ThrottleActionLink(e.player, "onInteract"), ThrottleActionParam(e, e.item, e.hand?.ordinal))
}
}

@SubscribeEvent
fun onLogin(e: PlayerLoginEvent) {
    submitAsync {
        throttleAction(ThrottleActionLink(e.player, "onLogin"), ThrottleActionParam(e))
}
}

@SubscribeEvent
fun onPreAttack(e: PrePlayerAttackEntityEvent) {
    submitAsync {
        throttleAction(ThrottleActionLink(e.player, "onPreAttack"), ThrottleActionParam(e, e.player.activeItem, e.player.activeItemHand.ordinal))
}
}

@SubscribeEvent
fun onConsume(e: PlayerItemConsumeEvent) {
    submitAsync {
        throttleAction(ThrottleActionLink(e.player, "onConsume"), ThrottleActionParam(e, e.item, e.hand.ordinal))
        if(config.getBoolean("item.no-replacement-consume",false)) e.replacement = null
    }
}
@SubscribeEvent
fun onAttack(e: PlayerAttackEvent) {
    submitAsync {
        throttleAction(ThrottleActionLink(e.player, "onAttack"), ThrottleActionParam(e, e.player.activeItem, e.player.activeItemHand.ordinal))
    }
}
