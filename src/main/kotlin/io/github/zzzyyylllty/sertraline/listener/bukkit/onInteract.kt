package io.github.zzzyyylllty.sertraline.listener.bukkit

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.consoleSender
import io.github.zzzyyylllty.sertraline.function.action.applyActions
import io.github.zzzyyylllty.sertraline.logger.infoS
import io.lumine.mythic.lib.api.event.PlayerAttackEvent
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.event.player.PlayerLoginEvent
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync

@SubscribeEvent
fun onInteract(e: PlayerInteractEvent) {
    submitAsync {
    e.player.applyActions("onInteract", e)
}
}

@SubscribeEvent
fun onLogin(e: PlayerLoginEvent) {
    submitAsync {
    e.player.applyActions("onLogin", e)
}
}

@SubscribeEvent
fun onPreAttack(e: PrePlayerAttackEntityEvent) {
    submitAsync {
    e.player.applyActions("onPreAttack", e)
}
}

@SubscribeEvent
fun onConsume(e: PlayerItemConsumeEvent) {
    submitAsync {
        e.player.applyActions("onConsume", e)
        if(config.getBoolean("item.no-replacement-consume",false)) e.replacement = null
    }
}
@SubscribeEvent
fun onAttack(e: PlayerAttackEvent) {
    submitAsync {
        e.player.applyActions("onAttack", e)
    }
}
