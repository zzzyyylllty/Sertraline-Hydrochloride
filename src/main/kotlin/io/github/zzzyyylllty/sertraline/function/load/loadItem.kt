package io.github.zzzyyylllty.sertraline.function.load

import io.github.zzzyyylllty.sertraline.Sertraline.consoleSender
import io.github.zzzyyylllty.sertraline.data.Action
import io.github.zzzyyylllty.sertraline.data.ActionType
import io.github.zzzyyylllty.sertraline.data.AttributePart
import io.github.zzzyyylllty.sertraline.data.AttributeSources
import io.github.zzzyyylllty.sertraline.data.DSkill
import io.github.zzzyyylllty.sertraline.data.DepazItems
import io.github.zzzyyylllty.sertraline.data.SkillSource
import io.github.zzzyyylllty.sertraline.data.VanillaItemInst
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.function.sertralize.serializeStringList
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.platform.util.asLangText
import java.util.LinkedHashMap
import java.util.UUID

@OptIn(ExperimentalStdlibApi::class)
fun loadItem(iconfig: YamlConfiguration, root: String) : DepazItems {
    val mm = MiniMessage.miniMessage()
    var config = iconfig.getConfigurationSection(root)!!

    /*
  extend:
    - template: FOOD_TEMPLATE
      data:
        effect: instant_damage
    */

    if (config.get("extend") != null) {
        val list = config.get("extend") as List<LinkedHashMap<String, Any>>
        for (template in list) {
            config = applyTemplate(config, template)
        }
    }

    val nbts = config.get("minecraft.nbt") as List<LinkedHashMap<String, Any>>?

    val item = VanillaItemInst(
        material = config.get("minecraft.material") as String? ?:"STONE",
        name = config.get("minecraft.name") as String?,
        lore = serializeStringList(config.get("minecraft.lore")),
        model = config.get("minecraft.model") as Int?,
        nbt = nbts ?: listOf(),
        materialLoreEnabled = config.get("minecraft.material-lore") as Boolean? ?: true
    )

    val actions : MutableList<Action> = mutableListOf()
    val sections = config.getList("action") as List<LinkedHashMap<String, Any?>>?

    devLog("actionsections: $sections")

    if (sections != null && !sections.isEmpty()) for (section in sections) {
        val actionList = serializeStringList(section["content"])
        var requireList = serializeStringList(section["require"])
        var trigger = section["trigger"] as String?
        val type: ActionType = ActionType.valueOf(section["type"] as String? ?: "KETHER")
        if (requireList.isEmpty()) {
            requireList = listOf("UNIVERSAL")
            devLog(consoleSender.asLangText("DEBUG_NO_PARAM_USE_DEFAULT","require","listOf(\"UNIVERSAL\")"))
        }
        if (trigger == null) {
            trigger = "onInteract"
            devLog(consoleSender.asLangText("DEBUG_NO_PARAM_USE_DEFAULT","trigger","onInteract"))
        }
        devLog("requireList is not empty.")

        devLog("Trigger $trigger ")
        devLog("async ${section["async"] as Boolean? ?: true}")
        devLog("type: $type")
        devLog("require: $requireList")
        devLog("actions $actionList ")
        val ac =
            Action(
                trigger = trigger,
                async = (section["async"] as Boolean? ?: true),
                actions = actionList,
                actionType = type,
                require = requireList,
            )
        devLog("Adding $ac.")
        actions.add(ac)
    }

    var attributeParts : MutableList<AttributePart> = mutableListOf()
    var skillParts : MutableList<DSkill> = mutableListOf()
    val atbsections = config.getList("attribute") as List<LinkedHashMap<String, Any>>?
    val skillsections = config.getList("skill") as List<LinkedHashMap<String, Any>>?

    devLog("atbsections: $atbsections")

    if (atbsections != null && !atbsections.isEmpty()) for (section in atbsections) {
        devLog("section: $section")

        var attributeNames: MutableList<MutableMap.MutableEntry<String, Any>> = mutableListOf()
        for (entry in section) {
            if (!entry.key.startsWith("meta")) attributeNames.add(entry)
        }

        val type: AttributeSources = AttributeSources.valueOf(section["meta_type"] as String? ?: config["attribute.default"] as String? ?: "MYTHIC_LIB")
        val definer = section["meta_definer"] as String? ?: config["attribute.default-definer"] as String? ?: "sertraline_<slot>"
        val metaUUID = section["meta_uuid"] as String?
        val uuid = metaUUID
        val source = section["meta_source"] as String? ?: "MELEE_WEAPON"
        val mythicLibEquipSlot = section["meta_equip_slot"] as String? ?: "MAIN_HAND"
        val requireSlot = serializeStringList(section["meta_require"])
        val conditionOnBuild = section["meta_condition_onbuild"] as String?
        val conditionOnEffect = section["meta_condition"] as String?
        val chance = section["chance"] as String? ?: "100.0"

        val attrList = LinkedHashMap<String, String>()
        for (attr in attributeNames) {
            attrList.put(attr.key, attr.value.toString())
        }
        attributeParts.add(
            AttributePart(
                attributeSources = type,
                attr = attrList,
                definer = definer,
                uuid = uuid ?: UUID.randomUUID().toString(),
                chance = chance,
                source = source,
                mythicLibEquipSlot = mythicLibEquipSlot,
                requireSlot = requireSlot.ifEmpty { mutableListOf("UNIVERSAL") }.toMutableList(),
                conditionOnBuild = conditionOnBuild,
                conditionOnEffect = conditionOnEffect
            )
        )
    }

    if (skillsections != null && !skillsections.isEmpty()) for (section in skillsections) {
        devLog("section: $section")

        val engine = section["engine"] as String? ?: config["skill.default-engine"] as String? ?: "MYTHIC"

        skillParts.add(DSkill(
            engine = SkillSource.valueOf(engine),
            depazTrigger = section["trigger"] as String? ?: throw NullPointerException("skill DEPAZ trigger not found"),
            async = section["async"] as Boolean? ?: true,
            skillName = section["skill"] as String? ?: throw NullPointerException("Skill name not found"),
            skillTrigger = section["skill_trigger"] as String? ?: "DEFAULT",
            power = section["power"].toString().toFloatOrNull() ?: 0f,
            require = serializeStringList(section["require"]).toMutableList(),
            param = config.getConfigurationSection("skill.params")?.getValues(false) as LinkedHashMap<String, Any>? ?: linkedMapOf(),
            dataForParam = section["data_param"] as Boolean? ?: false,
        ))
    }

    val data = config.getConfigurationSection("meta.data")?.getValues(false) as LinkedHashMap<String, Any>? ?: linkedMapOf()

    //val data = meta["data"] as LinkedHashMap<String, Any> ?: linkedMapOf()

    return DepazItems(root, item, actions, attributeParts, data, skillParts)
}