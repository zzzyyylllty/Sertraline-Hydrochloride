package io.github.zzzyyylllty.actions

import io.github.zzzyyylllty.SertralineHydrochloride.console
import io.github.zzzyyylllty.data.ActionsType.*
import io.github.zzzyyylllty.data.SertralineItem
import io.github.zzzyyylllty.data.SingleActionsData
import io.github.zzzyyylllty.functions.kether.evalKether
import io.lumine.mythic.bukkit.MythicBukkit
import org.bukkit.entity.Player
import pers.neige.neigeitems.utils.ItemUtils.cast
import taboolib.expansion.dispatchCommandAsOp
/**
 * 触发action，不管是否满足条件
 * 满足条件由 prepareCallActions 判断后传递给该函数
 *
 * @param p 玩家
 * @param sItem 物品
 */
fun SingleActionsData.directCallAction(p: Player,sItem: SertralineItem) {
    when (this.type) {
        KETHER -> this.value.evalKether(p)
        COMMAND_PLAYER -> this.value.forEach { p.performCommand(it) }
        COMMAND_CONSOLE -> this.value.forEach { console.performCommand(it) }
        COMMAND_OP -> this.value.forEach { p.dispatchCommandAsOp(it) }
        MYTHICMOBS_SKILL -> this.value.forEach { MythicBukkit.inst().skillManager.getSkill(it).cast() }
        REFRESH -> TODO()
        REGENERATE -> TODO()
        JAVASCRIPT -> TODO()
    }

}
