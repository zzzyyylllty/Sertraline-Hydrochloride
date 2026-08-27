package io.github.zzzyyylllty.sertraline.listener

import io.github.zzzyyylllty.sertraline.util.SertralineRecipeManager
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerJoinEvent
import taboolib.common.platform.event.SubscribeEvent

/**
 * Player recipe book unlock.
 * The vanilla recipe book only shows recipes the player has unlocked (by progress); Sertraline recipes have no
 * progress source, so players cannot see them without crafting at least once. On join, proactively unlock all
 * Sertraline recipes via NMS RecipeBookServer.add (Player.discoverRecipe was removed in spigot-api 1.21.4).
 */
@SubscribeEvent
fun onPlayerJoinUnlockRecipes(e: PlayerJoinEvent) {
    SertralineRecipeManager.unlockAllForPlayer(e.player)
}

/**
 * Refreshes the recipe book of all online players (call after reloading recipes, see loadRecipeFiles).
 */
fun unlockOnlinePlayers() {
    for (player in Bukkit.getOnlinePlayers()) {
        SertralineRecipeManager.unlockAllForPlayer(player)
    }
}
