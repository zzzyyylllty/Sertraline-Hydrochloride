package io.github.zzzyyylllty.sertraline.listener.sertraline

import org.bukkit.entity.Player
import org.bukkit.event.inventory.TradeSelectEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent

// 独立文件：TradeSelectEvent 是 1.14+ API，legacy12（v11200）编译面不存在，
// 由 common/build.gradle.kts 的 sourceSets exclude 排除（1.12.2 Bukkit 无任何村民交易事件，功能缺失）

// ── villager trading ─────────────────────────────────────────────────
// disable-village-trade: 不区分插件/原版交易，VANILLA 与 TRUE 均拦截全部交易

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
