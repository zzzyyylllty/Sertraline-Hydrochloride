package io.github.zzzyyylllty.sertraline.util.dependencies

import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.util.DependencyHelper
import io.lumine.mythic.lib.api.player.MMOPlayerData
import io.lumine.mythic.lib.api.stat.StatMap
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent

val mmoDataCacheMap = LinkedHashMap<String, MMOPlayerData>() // UUID, MMOPlayerData
val mmoStatCacheMap = LinkedHashMap<String, StatMap>() // UUID, StatMap

@SubscribeEvent
fun mmoAttributeInit(e: PlayerJoinEvent) {
    if (!DependencyHelper().isPluginInstalled("MythicLib")) return
    val playerData = MMOPlayerData(e.player.uniqueId)
    mmoDataCacheMap[e.player.uniqueId.toString()] = playerData
    mmoStatCacheMap[e.player.uniqueId.toString()] = playerData.statMap
}

@SubscribeEvent
fun mmoAttributeRelease(e: PlayerQuitEvent) {
    if (!DependencyHelper().isPluginInstalled("MythicLib")) return
    mmoDataCacheMap.remove(e.player.uniqueId.toString())
    mmoStatCacheMap.remove(e.player.uniqueId.toString())
}
@SubscribeEvent
fun mmoAttributeReleaseKick(e: PlayerKickEvent) {
    if (!DependencyHelper().isPluginInstalled("MythicLib")) return
    mmoDataCacheMap.remove(e.player.uniqueId.toString())
    mmoStatCacheMap.remove(e.player.uniqueId.toString())
}

fun mmoAttributeCalcuate(item: ModernSItem, player: Player) {
    if (!DependencyHelper().isPluginInstalled("MythicLib")) return

}
