package io.github.zzzyyylllty.sertraline.compat

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * Paper 专属事件回调（由 common 提供业务逻辑，适配层负责事件注册/模拟）。
 * 此接口为双平台公共契约，仅使用 Bukkit API 类型。
 */
interface PaperEventHooks {

    /** Paper: PlayerArmorChangeEvent 触发；Spigot: 由 armor-change-polling 轮询触发 */
    fun onArmorChange(player: Player) {}

    /** Paper: PlayerTradeEvent 触发；返回 true 表示拦截本次交易。Spigot: 不触发 */
    fun onPlayerTrade(ingredients: List<ItemStack>): Boolean = false
}
