package io.github.zzzyyylllty.sertraline.listener.bukkit

import io.github.zzzyyylllty.sertraline.function.action.throttleApplyAction
import io.lumine.mythic.lib.api.event.PlayerAttackEvent
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.event.player.PlayerLoginEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync

@SubscribeEvent
fun onInteract(e: PlayerInteractEvent) {
    submitAsync {
    throttleApplyAction(e.player, "onInteract")
}
}

@SubscribeEvent
fun onLogin(e: PlayerLoginEvent) {
    submitAsync {
    throttleApplyAction(e.player, "onLogin")
}
}

@SubscribeEvent
fun onPreAttack(e: PrePlayerAttackEntityEvent) {
    submitAsync {
    throttleApplyAction(e.player, "onPreAttack")
}
}

@SubscribeEvent
fun onConsume(e: PlayerItemConsumeEvent) {
    submitAsync {
    throttleApplyAction(e.player, "onConsume")
    }
}

@SubscribeEvent
fun onConsume(e: PlayerAttackEvent) {
    submitAsync {
        throttleApplyAction(e.player, "onAttack")
    }
}
