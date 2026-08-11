package io.github.zzzyyylllty.sertraline.listener

import io.github.zzzyyylllty.sertraline.util.SertralineRecipeManager
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerJoinEvent
import taboolib.common.platform.event.SubscribeEvent

/**
 * 玩家配方书解锁。
 * 原版配方书只显示玩家已解锁的配方（进度来源）；Sertraline 配方无进度来源，
 * 玩家不合成一次就看不到。在 join 时通过 NMS RecipeBookServer.add 主动解锁全部
 * Sertraline 配方（Player.discoverRecipe 已在 spigot-api 1.21.4 移除）。
 */
@SubscribeEvent
fun onPlayerJoinUnlockRecipes(e: PlayerJoinEvent) {
    SertralineRecipeManager.unlockAllForPlayer(e.player)
}

/**
 * 刷新所有在线玩家的配方书（reload 配方后调用，见 loadRecipeFiles）。
 */
fun unlockOnlinePlayers() {
    for (player in Bukkit.getOnlinePlayers()) {
        SertralineRecipeManager.unlockAllForPlayer(player)
    }
}
