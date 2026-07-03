package io.github.zzzyyylllty.sertraline.listener.sertraline

import taboolib.common5.compileJS
import io.github.zzzyyylllty.sertraline.item.sertralineItemBuilder
import io.github.zzzyyylllty.sertraline.logger.severeS
import io.github.zzzyyylllty.sertraline.logger.warningS
import io.github.zzzyyylllty.sertraline.data.RecipeFunction
import io.github.zzzyyylllty.sertraline.util.SertralineRecipeManager
import org.bukkit.Keyed
import org.bukkit.entity.Player
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import org.bukkit.Bukkit
import taboolib.common.platform.function.adaptCommandSender
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptOptions

/**
 * 配方合成事件监听器。
 * 负责：
 * 1. PrepareItemCraftEvent — 即时重建 Sertraline 物品结果（含玩家上下文）
 * 2. CraftItemEvent — 合成后执行结果函数（kether / JS / command）
 */

private const val NAMESPACE = "sertraline"

@SubscribeEvent(priority = EventPriority.HIGHEST)
fun onPrepareCraft(event: PrepareItemCraftEvent) {
    val recipe = event.recipe ?: return
    if (recipe !is Keyed) return
    if (recipe.key.namespace != NAMESPACE) return
    val recipeData = SertralineRecipeManager.getRecipeData(recipe.key) ?: return

    val player = event.view.player as? Player ?: return

    // 只重建 Sertraline 物品结果（含玩家变量）
    val (namespace, key) = parseId(recipeData.result.itemId)
    if (namespace != NAMESPACE) return

    val rebuilt = sertralineItemBuilder(key, player)
        ?.apply { amount = recipeData.result.count }
        ?: return

    event.inventory.result = rebuilt
}

@SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
fun onCraftItem(event: CraftItemEvent) {
    val recipe = event.recipe ?: return
    if (recipe !is Keyed) return
    if (recipe.key.namespace != NAMESPACE) return
    val recipeData = SertralineRecipeManager.getRecipeData(recipe.key) ?: return

    if (recipeData.result.functions.isEmpty()) return
    val player = event.whoClicked as? Player ?: return

    recipeData.result.functions.forEach { func ->
        runCatching {
            executeFunction(func, player)
        }.onFailure { e ->
            severeS("Failed to execute recipe function for ${recipeData.id}: ${e.message}")
        }
    }
}

private fun executeFunction(func: RecipeFunction, player: Player) {
    when (func) {
        is RecipeFunction.Kether -> {
            val script = func.script.lines()
            if (script.isNotEmpty()) {
                KetherShell.eval(script, ScriptOptions(sender = adaptCommandSender(player)))
            }
        }
        is RecipeFunction.JavaScript -> {
            func.script.compileJS()?.eval()
                ?: warningS("Failed to compile JS for recipe")
        }
        is RecipeFunction.Command -> {
            Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                func.command.replace("%player%", player.name)
            )
        }
    }
}

private fun parseId(id: String): Pair<String, String> {
    val idx = id.indexOf(':')
    return if (idx == -1) "minecraft" to id.lowercase()
    else id.substring(0, idx).lowercase() to id.substring(idx + 1)
}
