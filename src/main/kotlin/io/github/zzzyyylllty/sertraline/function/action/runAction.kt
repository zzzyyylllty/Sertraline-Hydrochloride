package io.github.zzzyyylllty.sertraline.function.action

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.data.Action
import io.github.zzzyyylllty.sertraline.data.ActionType
import io.github.zzzyyylllty.sertraline.data.DepazItemInst
import io.github.zzzyyylllty.sertraline.data.DepazItems
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.function.item.getData
import io.github.zzzyyylllty.sertraline.logger.warningS
import io.github.zzzyyylllty.sertraline.function.item.getDepazItem
import io.github.zzzyyylllty.sertraline.function.item.getDepazItemInst
import io.github.zzzyyylllty.sertraline.function.item.getSlots
import io.github.zzzyyylllty.sertraline.function.item.solvePlaceholders
import io.github.zzzyyylllty.sertraline.function.kether.directInvokeItemEvent
import io.github.zzzyyylllty.sertraline.function.kether.evalKether
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.submitAsync
import taboolib.module.lang.asLangText

fun Player.applyActions(trigger: String, e: Event) {
    val player = this
    submitAsync {
    val inv = player.inventory
        devLog(console.asLangText("DEBUG_ACTION_APPLY", player.player?.name ?:"Unknown"))
        val slotList = getSlots(config.getStringList("action.require-enabled-slot"))
        for (slot in slotList) {
            val data = inv.getItem(slot).getData()
            var i = inv.getItem(slot)?.getDepazItemInst() ?: continue
                for (action in i.getDepazItem()?.actions ?: continue) {
                    if (action.trigger == trigger && player.getSlots(action.require).contains(slot)) {
                        val returni = player.applyAction(action, i, data, e)
                        if (returni != null) i = returni
                    }
                }
        }
    }
}

fun Player.applyAction(action: Action, i: DepazItemInst,data : LinkedHashMap<String, Any>,e: Event): DepazItemInst? {
    val player = this
    var returnItem: DepazItemInst? = i
    submit(async = action.async) {
        when (action.actionType) {
            ActionType.KETHER ->
                returnItem = i.directInvokeItemEvent(
                    player = player,
                    event = e,
                    data = data,
                    script = action.actions
                ).get()
            ActionType.SKILL_MYTHIC -> player.warningS("In Dev!")
            ActionType.JAVASCRIPT -> player.warningS("In Dev!")
        }
    }
    return returnItem
}