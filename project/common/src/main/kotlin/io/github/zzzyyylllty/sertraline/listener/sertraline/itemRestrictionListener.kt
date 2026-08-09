package io.github.zzzyyylllty.sertraline.listener.sertraline

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Keyed
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.FurnaceSmeltEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.inventory.SmithItemEvent
import org.bukkit.event.inventory.TradeSelectEvent
import org.bukkit.inventory.ItemStack
import io.papermc.paper.event.player.PlayerTradeEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.nms.getItemTag

/**
 * RestrictMode for common adapter lock options.
 *
 * FALSE   — do not block
 * VANILLA — block only vanilla (minecraft namespace) recipes
 * TRUE    — block all recipes/operations
 */
enum class RestrictMode {
    FALSE,
    VANILLA,
    TRUE;

    companion object {
        fun fromString(s: String): RestrictMode? {
            return when (s.trim().lowercase()) {
                "true", "yes" -> TRUE
                "false", "no" -> FALSE
                "vanilla" -> VANILLA
                else -> try {
                    valueOf(s.uppercase())
                } catch (_: IllegalArgumentException) {
                    null
                }
            }
        }
    }
}

// ── helpers ──────────────────────────────────────────────────────────

private fun getRestrictMode(item: ItemStack, key: String): RestrictMode? {
    if (item.type == Material.AIR) return null
    val tag = item.getItemTag(true)
    val sId = tag["sertraline_id"]?.asString() ?: return null
    val sItem = itemMap[sId] ?: return null
    val raw = sItem.getDeepData("common:$key")?.toString() ?: return null
    return RestrictMode.fromString(raw)
}

private fun isVanillaRecipe(recipe: Any?): Boolean {
    if (recipe == null) return true // conservative: treat unknown as vanilla
    if (recipe !is Keyed) return true
    return recipe.key.namespace == "minecraft"
}

private fun shouldBlock(mode: RestrictMode, isVanilla: Boolean): Boolean {
    return when (mode) {
        RestrictMode.TRUE -> true
        RestrictMode.VANILLA -> isVanilla
        RestrictMode.FALSE -> false
    }
}

private fun sendBlockMessage(player: Player, key: String) {
    val msg = config.getString("messages.common.$key", "")
    if (!msg.isNullOrEmpty()) {
        player.sendMessage(MiniMessage.miniMessage().deserialize(msg))
    }
}

private fun checkItemsForBlock(
    items: Array<out ItemStack?>,
    checkKey: String,
    isVanilla: Boolean,
): Boolean {
    for (item in items) {
        if (item == null || item.type == Material.AIR) continue
        val mode = getRestrictMode(item, checkKey) ?: continue
        if (shouldBlock(mode, isVanilla)) return true
    }
    return false
}

private fun checkItemForBlock(
    item: ItemStack?,
    checkKey: String,
    isVanilla: Boolean,
): Boolean {
    if (item == null || item.type == Material.AIR) return false
    val mode = getRestrictMode(item, checkKey) ?: return false
    return shouldBlock(mode, isVanilla)
}

// ── crafting table ───────────────────────────────────────────────────

@SubscribeEvent(priority = EventPriority.HIGHEST)
fun onCommonPrepareCraft(event: PrepareItemCraftEvent) {
    val player = event.view.player as? Player ?: return
    val isVanilla = isVanillaRecipe(event.recipe)

    if (checkItemsForBlock(event.inventory.matrix, "disable-crafting", isVanilla)) {
        event.inventory.result = null
        sendBlockMessage(player, "crafting-blocked")
    }
}

@SubscribeEvent(priority = EventPriority.HIGHEST)
fun onCommonCraftItem(event: CraftItemEvent) {
    val player = event.whoClicked as? Player ?: return
    val isVanilla = isVanillaRecipe(event.recipe)

    if (checkItemsForBlock(event.inventory.matrix, "disable-crafting", isVanilla)) {
        event.isCancelled = true
        sendBlockMessage(player, "crafting-blocked")
    }
}

// ── furnace smelting ─────────────────────────────────────────────────

@SubscribeEvent(priority = EventPriority.HIGHEST)
fun onFurnaceSmelt(event: FurnaceSmeltEvent) {
    // FurnaceSmeltEvent does not expose recipe API in 1.21.4,
    // so VANILLA and TRUE both block smelting.
    val mode = getRestrictMode(event.source, "disable-smelting") ?: return
    if (mode != RestrictMode.FALSE) {
        devLog("Blocked furnace smelting for ${event.source.type}")
        event.isCancelled = true
    }
}

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

@SubscribeEvent(priority = EventPriority.HIGHEST)
fun onPlayerTrade(event: PlayerTradeEvent) {
    // 安全网：交易完成瞬间再次拦截（选择已被阻止，正常情况下不会触发）
    if (event.trade.ingredients.any {
            checkItemForBlock(it, "disable-village-trade", isVanilla = true)
        }
    ) {
        event.isCancelled = true
    }
}

// ── smithing table ───────────────────────────────────────────────────

@SubscribeEvent(priority = EventPriority.HIGHEST)
fun onSmithItem(event: SmithItemEvent) {
    val player = event.whoClicked as? Player ?: return

    val inv = event.inventory
    // SmithItemEvent recipe API varies across versions; we conservatively
    // pass isVanilla=true so TRUE and VANILLA both block.
    if (checkItemForBlock(inv.inputEquipment, "disable-smithing", isVanilla = true) ||
        checkItemForBlock(inv.inputMineral, "disable-smithing", isVanilla = true)
    ) {
        event.isCancelled = true
        sendBlockMessage(player, "smithing-blocked")
    }
}
