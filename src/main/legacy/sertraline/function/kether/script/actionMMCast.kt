package io.github.zzzyyylllty.sertraline.function.kether.script

import ink.ptms.um.Mythic
import ink.ptms.um.Skill
import io.github.zzzyyylllty.sertraline.function.kether.getBukkitPlayer
import taboolib.module.kether.KetherParser
import taboolib.module.kether.scriptParser
import taboolib.common.platform.function.submit
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.expects
import java.util.concurrent.CompletableFuture


// Some code from Chemdah
class MythicMobsCast(val mechanic: Skill, val trigger: Skill.Trigger, power: Float) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val player = frame.getBukkitPlayer()
        submit { mechanic.execute(trigger, player, player, emptySet(), emptySet(), 0f, emptyMap()) }
        return CompletableFuture.completedFuture(null);
    }
}

@KetherParser(["smythicmobs", "smm"], shared = true)
fun parser() = scriptParser {
    when (it.expects("castskill")) {
        "castskill" -> {
            val skill = it.nextToken()
            val mechanic = Mythic.API.getSkillMechanic(skill) ?: error("unknown skill $skill")
            val trigger = try {
                it.mark()
                it.expects("trigger")
                Mythic.API.getSkillTrigger(it.nextToken())

            } catch (ex: Throwable) {
                it.reset()
                Mythic.API.getSkillTrigger("DEFAULT")
            }
            val power = try {
                it.mark()
                it.expects("power")
                it.nextToken().toFloat()
            } catch (ex: Throwable) {
                it.reset()
                0f
            }
            MythicMobsCast(mechanic, trigger, power)
        }
        else -> error("out of case")
    }
}