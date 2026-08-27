package io.github.zzzyyylllty.sertraline.compat

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * Paper-specific event callbacks (common provides the business logic; this adapter layer handles event
 * registration/simulation). This interface is the public contract for both platforms, using only Bukkit API types.
 */
interface PaperEventHooks {

    /** Paper: fired by PlayerArmorChangeEvent; Spigot: fired by armor-change-polling */
    fun onArmorChange(player: Player) {}

    /** Paper: fired by PlayerTradeEvent; returning true intercepts the trade. Spigot: not fired */
    fun onPlayerTrade(ingredients: List<ItemStack>): Boolean = false
}
