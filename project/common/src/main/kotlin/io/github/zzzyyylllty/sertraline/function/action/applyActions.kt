package io.github.zzzyyylllty.sertraline.function.action

import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.data.Action
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.function.data.getSavedData
import io.github.zzzyyylllty.sertraline.item.itemSerializer
import io.github.zzzyyylllty.sertraline.util.ComplexTypeHelper
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submitAsync
import taboolib.module.configuration.util.asMap
import taboolib.module.lang.asLangText
import taboolib.module.nms.getItemTag

fun Player.applyActions(trigger: String, e: Event, i: ItemStack,islot: Int? = null) {
    submitAsync {
        val player = this@applyActions
        val inv = player.inventory
        val item = itemSerializer(i, player) ?: return@submitAsync
        val allActions = ComplexTypeHelper(item.data["sertraline:actions"]).getAsActions()
        val actions = allActions?.get(trigger) ?: return@submitAsync
        actions.forEach { it.runAction(player, getSavedData(item, i, true, player).collect(), i, e, item) }
    }
}