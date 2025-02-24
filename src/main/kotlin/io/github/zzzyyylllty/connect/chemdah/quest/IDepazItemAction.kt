package io.github.zzzyyylllty.connect.chemdah.quest

import ink.ptms.chemdah.core.quest.objective.ObjectiveCountableI

object IDepazItemAction : ObjectiveCountableI() {

    /**
     * 统一识别名称
     */
    override val name = "depaz action"

    /**
     * Bukkit 事件
     */
    override val event = EntityPickupItemEvent::class

    init {
        handler {
            // 返回 EntityPickupItemEvent 事件下的玩家对象
            entity as? Player
        }
        // 注册一个条件，判断玩家所在位置
        addCondition("position") { e ->
            toPosition().inside(e.entity.location)
        }
        // 注册一个条件，判断玩家捡起的物品
        addCondition("item") { e ->
            toInferItem().isItem(e.item.itemStack)
        }
        // 注册一个条件，判断玩家捡起的物品的数量
        addCondition("amount") { e ->
            toInt() <= e.item.itemStack.amount
        }
        // 将捡起的物品的数量作为变量传入脚本代理，提高操作上限
        addConditionVariable("amount") {
            it.item.itemStack.amount
        }
    }

    /**
     * 以捡起的物品数量为计数单位，并非捡起的次数。
     */
    override fun getCount(profile: PlayerProfile, task: Task, event: EntityPickupItemEvent): Int {
        return event.item.itemStack.amount
    }