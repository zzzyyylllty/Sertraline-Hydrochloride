package io.github.zzzyyylllty.sertraline.listener.bukkit

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import io.github.zzzyyylllty.sertraline.command.subCommands.DepazDebugCommand.refreshStat
import io.github.zzzyyylllty.sertraline.function.stats.debounceRefreshStat
import io.github.zzzyyylllty.sertraline.function.stats.refreshStat
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerLoginEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync

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


@SubscribeEvent
fun onLoginUpdate(e: PlayerLoginEvent) {
    submitAsync {
        e.player.refreshStat()
    }
}