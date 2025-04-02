package io.github.zzzyyylllty.sertraline.listener.bukkit

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import io.github.zzzyyylllty.sertraline.function.stats.debounceRefreshStat
import io.github.zzzyyylllty.sertraline.function.stats.refreshStat
import org.bukkit.event.player.PlayerItemHeldEvent
import taboolib.common.platform.event.SubscribeEvent

@SubscribeEvent
fun hotBarChangeEvent(e: PlayerItemHeldEvent) {
    debounceRefreshStat(e.player, listOf("UNIVERSAL"))
}

@SubscribeEvent
fun armorEquipEvent(e: PlayerArmorChangeEvent) {
    debounceRefreshStat(e.player, listOf("UNIVERSAL"))
}

