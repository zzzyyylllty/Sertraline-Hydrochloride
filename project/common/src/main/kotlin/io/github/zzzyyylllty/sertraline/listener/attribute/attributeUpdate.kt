package io.github.zzzyyylllty.sertraline.listener.bukkit

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.util.DependencyHelper
import io.github.zzzyyylllty.sertraline.util.dependencies.AttributeUtil
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerLoginEvent
import taboolib.common.function.debounce
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync


val debounceRefreshStat = debounce<Player>(config.getLong("attribute.debounce-time",250)) { player ->
    AttributeUtil.refreshAttributes(player)
}

@SubscribeEvent
fun hotBarChangeEvent(e: PlayerItemHeldEvent) {
    submitAsync {
        debounceRefreshStat(e.player)
    }
}

@SubscribeEvent
fun armorEquipEvent(e: PlayerArmorChangeEvent) {
    submitAsync {
        debounceRefreshStat(e.player)
    }
}


@SubscribeEvent(EventPriority.MONITOR)
fun onLoginUpdate(e: PlayerLoginEvent) {
    submitAsync {
        AttributeUtil.refreshAttributes(e.player)
    }
}