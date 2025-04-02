package io.github.zzzyyylllty.sertraline.function.action

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.data.Action
import io.github.zzzyyylllty.sertraline.data.ActionType
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.logger.warningS
import io.github.zzzyyylllty.sertraline.function.item.getDepazItem
import io.github.zzzyyylllty.sertraline.function.item.getSlots
import io.github.zzzyyylllty.sertraline.function.kether.evalKether
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.submitAsync
import taboolib.module.lang.asLangText

fun Player.applyActions(trigger: String) {
    val player = this
    submitAsync {
    val inv = player.inventory
        devLog(console.asLangText("DEBUG_ACTION_APPLY", player.player?.name ?:"Unknown"))
        val slotList = getSlots(config.getStringList("action.require-enabled-slot"))
        for (slot in slotList) {
            val i = inv.getItem(slot).getDepazItem() ?: continue
                for (action in i.actions) {
                    if (action.trigger == trigger && player.getSlots(action.require).contains(slot)) player.applyAction(action)
                }
        }
    }
}

fun Player.applyAction(action: Action) {
    val player = this
    submit(async = action.async) {
        when (action.actionType) {
            ActionType.KETHER -> action.actions.evalKether(player)
            ActionType.SKILL_MYTHIC -> player.warningS("In Dev!")
            ActionType.JAVASCRIPT -> player.warningS("In Dev!")
        }
    }
}