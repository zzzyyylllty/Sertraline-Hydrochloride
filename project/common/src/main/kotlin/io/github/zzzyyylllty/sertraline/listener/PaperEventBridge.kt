package io.github.zzzyyylllty.sertraline.listener

import io.github.zzzyyylllty.sertraline.compat.PaperEventHooks
import io.github.zzzyyylllty.sertraline.listener.attribute.debounceRefreshStat
import io.github.zzzyyylllty.sertraline.listener.sertraline.checkItemForBlock
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submitAsync

/**
 * Paper-only event bridge.
 * On Paper, real listeners registered by PlatformCompat call these hooks; on Spigot they do not fire (or are simulated by polling).
 */
object PaperEventBridge {

    val hooks: PaperEventHooks = object : PaperEventHooks {

        override fun onArmorChange(player: Player) {
            submitAsync { debounceRefreshStat(player) }
        }

        override fun onPlayerTrade(ingredients: List<ItemStack>): Boolean {
            return ingredients.any { checkItemForBlock(it, "disable-village-trade", isVanilla = true) }
        }
    }
}
