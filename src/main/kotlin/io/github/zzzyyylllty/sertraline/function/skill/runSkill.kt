package io.github.zzzyyylllty.sertraline.function.skill

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.data.ActionType
import io.github.zzzyyylllty.sertraline.data.DSkill
import io.github.zzzyyylllty.sertraline.data.DepazItemInst
import io.github.zzzyyylllty.sertraline.data.SkillSource
import io.github.zzzyyylllty.sertraline.data.depazCast
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.logger.warningS
import io.github.zzzyyylllty.sertraline.function.item.getDepazItem
import io.github.zzzyyylllty.sertraline.function.item.getDepazItemInst
import io.github.zzzyyylllty.sertraline.function.item.getSlots
import io.github.zzzyyylllty.sertraline.function.kether.directInvokeItemEvent
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.submitAsync
import taboolib.module.chat.impl.PropertyValue
import taboolib.module.lang.asLangText

fun Player.applySkills(trigger: String, e: Event, i2 : ItemStack? = null,islot: Int? = null) {
    val player = this
    submitAsync {
    val inv = player.inventory
        devLog(console.asLangText("DEBUG_SKILL_APPLY", player.player?.name ?:"Unknown"))
        val slotList = getSlots(config.getStringList("skill.require-enabled-slot"))
        for (slot in slotList) {
            var i = if (islot == slot) i2?.getDepazItemInst() ?: continue else inv.getItem(slot)?.getDepazItemInst() ?: continue
            val data = i.data
                for (action in i.getDepazItem()?.actions ?: continue) {
                    if (action.trigger == trigger && player.getSlots(action.require).contains(slot)) {
                        player.applySkill(action, i, data, e)
                    }
                }
        }
    }
}

fun Player.applySkill(skill: DSkill, i: DepazItemInst, data : LinkedHashMap<String, Any>, e: Event, params: LinkedHashMap<String, Any>) {
    val player = this
    var returnItem: DepazItemInst? = i
    submit(async = skill.async) {
        when (skill.engine) {
            SkillSource.MYTHIC -> skill.depazCast(this@applySkill, this@applySkill, params)
        }
    }
}