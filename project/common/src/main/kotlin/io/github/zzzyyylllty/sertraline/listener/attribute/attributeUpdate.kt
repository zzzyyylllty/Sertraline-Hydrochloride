package io.github.zzzyyylllty.sertraline.listener.attribute

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.attribute.AttributeManager
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerLoginEvent
import taboolib.common.function.debounce
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync


val debounceRefreshStat = debounce<Player>(config.getLong("attribute.debounce-time",1000)) { player ->
    AttributeManager.refreshAttributes(player)
}

@SubscribeEvent
fun hotBarChangeEvent(e: PlayerItemHeldEvent) {
    submitAsync {
        debounceRefreshStat(e.player)
    }
}

// The Paper-only PlayerArmorChangeEvent has been moved to PaperEventBridge (hooks);
// on Spigot it is simulated via attribute.armor-change-polling polling.

@SubscribeEvent(EventPriority.MONITOR)
fun onLoginUpdate(e: PlayerLoginEvent) {
    submitAsync {
        AttributeManager.refreshAttributes(e.player)
    }
}