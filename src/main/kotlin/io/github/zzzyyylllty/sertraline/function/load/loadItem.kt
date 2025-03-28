package io.github.zzzyyylllty.sertraline.function.load

import io.github.zzzyyylllty.sertraline.data.Action
import io.github.zzzyyylllty.sertraline.data.AttributePart
import io.github.zzzyyylllty.sertraline.data.AttributeSources
import io.github.zzzyyylllty.sertraline.data.DepazItems
import io.github.zzzyyylllty.sertraline.function.error.throwNPEWithMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.common.platform.function.info
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem
import java.util.LinkedHashMap

fun loadItem(config: YamlConfiguration, root: String) : DepazItems {
    val legacyApi = LegacyComponentSerializer.legacyAmpersand()
    val mm = MiniMessage.miniMessage()

    val item = buildItem(XMaterial.valueOf(config["$root.minecraft.material"].toString())) {
        customModelData = 0
    }
    val meta = item.itemMeta
    var name = mm.deserialize("<white>${config["$root.minecraft.name"].toString()}").decorationIfAbsent(TextDecoration.ITALIC,TextDecoration.State.FALSE)
    meta.displayName(name)
    item.setItemMeta(meta)
    val strings = config.getString("$root.minecraft.lore")
    var lore : MutableList<Component> = mutableListOf()
    val legacy = LegacyComponentSerializer.legacyAmpersand()
    if (strings != null) {
        for (string in strings.split("\n")) {
            val comp = mm.deserialize(legacy.serialize(legacy.deserialize(string.replace("§", "&"))))
            lore.add(comp.decorationIfAbsent(TextDecoration.ITALIC,TextDecoration.State.FALSE))
        }
    }
    item.lore(lore)

    var actions : MutableList<Action> = mutableListOf()
    val sections = config.getList("$root.action") as List<LinkedHashMap<String, Any>>?

    if (sections != null && !sections.isEmpty()) for (section in sections) {
        val actionList = if (section["content"] is ArrayList<*>) section["content"] as ArrayList<String> else (section["content"] as String).split("\n")
        actions.add(
            Action(
                trigger = (section["trigger"] ?: throwNPEWithMessage("ACTION_TRIGGER_NOT_FOUND", root)).toString(),
                async = (section["async"] as Boolean),
                actions = actionList
            )
        )
    }

    var attributeParts : MutableList<AttributePart> = mutableListOf()
    val atbsections = config.getList("$root.attribute") as List<LinkedHashMap<String, Any>>?

    info("atbsections: $atbsections")

    if (atbsections != null && !atbsections.isEmpty()) for (section in atbsections) {
        info("section: $section")

        var attributeNames: MutableList<MutableMap.MutableEntry<String, Any>> = mutableListOf()
        for (entry in section) {
            if (!entry.key.startsWith("meta")) attributeNames.add(entry)
        }

        val type = AttributeSources.valueOf(section["meta_type"] as String? ?: "MYTHIC_LIB")
        val definer = section["meta_definer"] as String? ?: "sertraline"
        val metaUUID = section["meta_uuid"] as String?
        val uuid = metaUUID
        val amount = section["meta_engine"] as String? ?: "100"
        val source = section["meta_source"] as String? ?: "MELEE_WEAPON"
        val mythicLibEquipSlot = section["meta_equip_slot"] as String? ?: "MAIN_HAND"
        val requireSlot = section["meta_require"] as List<String>?
        val conditionOnBuild = section["meta_condition_onbuild"] as String?
        val conditionOnEffect = section["meta_condition"] as String?
        val chance = section["chance"] as String? ?: "100.0"

        val attrList = kotlin.collections.LinkedHashMap<String, String>()
        for (attr in attributeNames) {
            attrList.put(attr.key, attr.value.toString())
        }
        attributeParts.add(
            AttributePart(
                type = type,
                attr = attrList,
                definer = definer,
                uuid = uuid,
                chance = chance,
                source = source,
                mythicLibEquipSlot = mythicLibEquipSlot,
                requireSlot = requireSlot ?: listOf<String>("36","37","38","39","ANY_HAND"),
                conditionOnBuild = conditionOnBuild,
                conditionOnEffect = conditionOnEffect
            )
        )
    }




    return DepazItems(root, item, actions, attributeParts)
}