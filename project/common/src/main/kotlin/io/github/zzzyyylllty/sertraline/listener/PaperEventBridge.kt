package io.github.zzzyyylllty.sertraline.listener

import io.github.zzzyyylllty.sertraline.compat.PaperEventHooks
import io.github.zzzyyylllty.sertraline.listener.attribute.debounceRefreshStat
import io.github.zzzyyylllty.sertraline.listener.sertraline.checkItemForBlock
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submitAsync

/**
 * Paper 专属事件桥接。
 * Paper 上由 PlatformCompat 注册真实监听器调用 hooks；Spigot 上不触发（或由轮询模拟）。
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
