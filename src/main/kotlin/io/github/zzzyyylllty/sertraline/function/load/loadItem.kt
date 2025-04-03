package io.github.zzzyyylllty.sertraline.function.load

import io.github.zzzyyylllty.sertraline.Sertraline.consoleSender
import io.github.zzzyyylllty.sertraline.data.Action
import io.github.zzzyyylllty.sertraline.data.ActionType
import io.github.zzzyyylllty.sertraline.data.AttributePart
import io.github.zzzyyylllty.sertraline.data.AttributeSources
import io.github.zzzyyylllty.sertraline.data.DepazItems
import io.github.zzzyyylllty.sertraline.data.VanillaItemInst
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.function.sertralize.serializeStringList
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.platform.util.asLangText
import java.util.LinkedHashMap

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

    if (config.get("minecraft.extend") != null) {
        val list = config.get("minecraft.extend") as List<LinkedHashMap<String, Any>>
        for (template in list) {
            config = applyTemplate(config, template)
        }
    }


    val nbts = config.get("minecraft.nbt") as LinkedHashMap<String, Any>?

    val item = VanillaItemInst(
        material = config.get("minecraft.material") as String? ?:"STONE",
        name = config.get("minecraft.name") as String?,
        lore = serializeStringList(config.get("minecraft.lore")),
        model = config.get("minecraft.model") as Int? ?:0,
        nbt = nbts ?: LinkedHashMap<String, Any>(),
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
        devLog("async ${section["async"] as Boolean}")
        devLog("type: $type")
        devLog("require: $requireList")
        devLog("actions $actionList ")
        val ac =
            Action(
                trigger = trigger,
                async = (section["async"] as Boolean),
                actions = actionList,
                actionType = type,
                require = requireList,
            )
        devLog("Adding $ac.")
        actions.add(ac)
    }

    var attributeParts : MutableList<AttributePart> = mutableListOf()
    val atbsections = config.getList("attribute") as List<LinkedHashMap<String, Any>>?

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
                uuid = uuid,
                chance = chance,
                source = source,
                mythicLibEquipSlot = mythicLibEquipSlot,
                requireSlot = requireSlot.ifEmpty { mutableListOf("UNIVERSAL") },
                conditionOnBuild = conditionOnBuild,
                conditionOnEffect = conditionOnEffect
            )
        )
    }

    val data = config.get("data") as LinkedHashMap<String, Any>? ?: LinkedHashMap()

    return DepazItems(root, item, actions, attributeParts, data)
}