package io.github.zzzyyylllty.sertraline.function.load

import io.github.zzzyyylllty.sertraline.data.Action
import io.github.zzzyyylllty.sertraline.data.Attribute
import io.github.zzzyyylllty.sertraline.data.AttributeSources
import io.github.zzzyyylllty.sertraline.data.DepazItems
import io.github.zzzyyylllty.sertraline.function.error.throwNPEWithMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.library.xseries.XMaterial
import taboolib.module.nms.itemTagReader
import taboolib.platform.util.buildItem
import java.util.LinkedHashMap
import java.util.UUID

fun loadItem(config: YamlConfiguration, root: String) : DepazItems {
    val legacyApi = LegacyComponentSerializer.legacyAmpersand()
    val mm = MiniMessage.miniMessage()

    val item = buildItem(XMaterial.valueOf(config["$root.minecraft.material"].toString()))
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

    var attributes : MutableList<Attribute> = mutableListOf()
    val atbsections = config.getList("$root.attribute") as List<LinkedHashMap<String, Any>>?

    if (sections != null && !sections.isEmpty()) for (section in sections) {

        var attributeNames: MutableList<String> = mutableListOf()
        for (string in section.keys) {
            if (!string.startsWith("meta")) attributeNames.add(string)
        }

        val type = AttributeSources.valueOf(section["meta_engine"] as String? ?: "MYTHIC_LIB")
        val definer = section["meta_engine"] as String? ?: "sertraline"
        val uuid = UUID.fromString(section["meta_engine"] as String?) ?: UUID.randomUUID()
        val chance = section["meta_engine"] as String?
        val amount = TODO()
        val source = TODO()
        val mythicLibEquipSlot = TODO()
        val requireSlot = TODO()

        for (attr in attributeNames)
        attributes.add(
           Attribute(
               type = type,
               attr = attr,
               definer = definer,
               uuid = uuid,
               chance = TODO(),
               amount = TODO(),
               source = TODO(),
               mythicLibEquipSlot = TODO(),
               requireSlot = TODO()
           )
        )
    }




    return DepazItems(root, item, actions, attributes)
}