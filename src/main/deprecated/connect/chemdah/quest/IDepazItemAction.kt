package io.github.zzzyyylllty.connect.chemdah.quest

import DepazItemActionEvent
import ink.ptms.chemdah.core.PlayerProfile
import ink.ptms.chemdah.core.quest.Task
import ink.ptms.chemdah.core.quest.objective.ObjectiveCountableI
import ink.ptms.chemdah.core.quest.objective.other.IPlayerInventory.handler
import org.bukkit.entity.Player
/*
object IItemPick : ObjectiveCountableI<DepazItemActionEvent>() {

    override val name = "pickup item"
    override val event = DepazItemActionEvent::class.java

    init {
        handler {
            it.entity as? Player
        }
        addSimpleCondition("position") { data, e ->
            data.toPosition().inside(e.entity.location)
        }
        addSimpleCondition("item") { data, e ->
            data.toInferItem().isItem(e.item.itemStack)
        }
        addSimpleCondition("amount") { data, e ->
            data.toInt() <= e.item.itemStack.amount
        }
        addConditionVariable("amount") {
            it.item.itemStack.amount
        }
    }

    override fun getCount(profile: PlayerProfile, task: Task, event: DepazItemActionEvent): Int {
        return event.item.itemStack.amount
    }
}*/