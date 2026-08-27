package io.github.zzzyyylllty.sertraline.listener.sertraline

import org.bukkit.entity.Player
import org.bukkit.event.inventory.TradeSelectEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent

// Standalone file: TradeSelectEvent is a 1.14+ API and does not exist on the legacy12 (v11200) build surface,
// excluded via sourceSets in common/build.gradle.kts (1.12.2 Bukkit has no villager trade events, feature missing)

// ── villager trading ─────────────────────────────────────────────────
// disable-village-trade: does not distinguish plugin/vanilla trades; both VANILLA and TRUE block all trades

@SubscribeEvent(priority = EventPriority.HIGHEST)
fun onTradeSelect(event: TradeSelectEvent) {
    val player = event.view.player as? Player ?: return
    val recipe = try {
        event.merchant.getRecipe(event.index)
    } catch (_: IndexOutOfBoundsException) {
        return
    }

    if (recipe.ingredients.any {
            checkItemForBlock(it, "disable-village-trade", isVanilla = true)
        }
    ) {
        event.isCancelled = true
        sendBlockMessage(player, "village-trade-blocked")
    }
}
