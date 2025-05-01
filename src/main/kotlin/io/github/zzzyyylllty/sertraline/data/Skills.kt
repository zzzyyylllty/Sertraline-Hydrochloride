package io.github.zzzyyylllty.sertraline.data

import ink.ptms.um.Mythic
import ink.ptms.um.Skill
import ink.ptms.zaphkiel.um.impl5.Mythic5
import io.github.zzzyyylllty.sertraline.function.sertralize.AnySerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

@Serializable
data class DSkill(
    var engine: SkillSource,
    var depazTrigger: String,
    var async: Boolean,
    var skillName: String,
    var skillTrigger: String,
    var power: Float,
    var require: MutableList<String>,
    var param: LinkedHashMap<String, @Serializable(AnySerializer::class) Any>,
    var dataForParam: Boolean = false
)
@Serializable
enum class SkillSource {
    MYTHIC,
}

fun DSkill.depazCast(p: Player,target: Entity,param: LinkedHashMap<String, Any>) {
    if (!Mythic.isLoaded()) throw IllegalStateException("Mythic is not loaded")
    val trigger = Mythic.API.getSkillTrigger(skillTrigger)
    val mechanic = Mythic.API.getSkillMechanic(skillName) ?: throw IllegalStateException("unknown skill $skillName")
    mechanic.execute(trigger, p, target, emptySet(), emptySet(), power, param)
}