package io.github.zzzyyylllty.actions

import io.github.zzzyyylllty.data.SertralineItem
import io.github.zzzyyylllty.data.SingleActionsData
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit

/**
 * 触发action，不管是否满足条件
 * 满足条件由 prepareCallActions 判断后传递给该函数
 *
 * @param p 玩家
 * @param sItem 物品
 * @param actionKey 行动关键字，如 right-click @ all
 */
fun SingleActionsData.prepareCallActions(p: Player, sItem: SertralineItem, actionKey: String) {
    if (sItem.actionsData[actionKey] == null) return
    submit(async = (sItem.actionsData[actionKey]?.async ?: true)) {
        this@prepareCallActions.directCallAction(p, sItem)
    }
}
