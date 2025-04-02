package io.github.zzzyyylllty.sertraline.compat.mythicmobs

import ink.ptms.um.Mythic
import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.logger.severeS
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import taboolib.module.lang.asLangText

fun castSkill(
    skillLine: String,
    triggerName: String,
    player: Player,
    et: Set<Entity> = emptySet(),
    lt: Set<Location> = emptySet(),
    power: Float = 0f,
) {
    val mechanic = Mythic.API.getSkillMechanic(skillLine) ?: run{
        severeS(
            console.asLangText(
                "ERROR_UNKNOWN_SKILL",
                skillLine
            )
        )
        throw NullPointerException()
    }
    val trigger = try {
        Mythic.API.getSkillTrigger(triggerName)
    } catch (ex: Throwable) {
        severeS(console.asLangText("ERROR_UNKNOWN_SKILL_TRIGGER", triggerName))
        Mythic.API.getSkillTrigger("DEFAULT")
    }
    submit {
        mechanic.execute(trigger, player, player, et, lt, power, emptyMap())
    }

}
