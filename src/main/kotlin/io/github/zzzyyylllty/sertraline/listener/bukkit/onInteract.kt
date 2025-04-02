package io.github.zzzyyylllty.sertraline.listener.bukkit

import io.github.zzzyyylllty.sertraline.Sertraline.consoleSender
import io.github.zzzyyylllty.sertraline.function.action.applyActions
import io.github.zzzyyylllty.sertraline.logger.infoS
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.event.player.PlayerLoginEvent
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.event.SubscribeEvent

@SubscribeEvent
fun onInteract(e: PlayerInteractEvent) {
    e.player.applyActions("onInteract")
}

@SubscribeEvent
fun onLogin(e: PlayerLoginEvent) {
    e.player.applyActions("onLogin")
}

@SubscribeEvent
fun onPreAttack(e: PrePlayerAttackEntityEvent) {
    e.player.applyActions("onPreAttack")
}

@SubscribeEvent
fun onConsume(e: PlayerItemConsumeEvent) {
    e.player.applyActions("onConsume")
}

@SubscribeEvent
fun onConsume(e: Event) {
    consoleSender.infoS("event ${e.eventName}")
}
