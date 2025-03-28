package io.github.zzzyyylllty.sertraline.function.load

import io.github.zzzyyylllty.sertraline.data.Action
import io.github.zzzyyylllty.sertraline.data.Attribute
import io.github.zzzyyylllty.sertraline.data.DepazItems
import io.github.zzzyyylllty.sertraline.function.error.throwNPEWithMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.command.CommandSender
import org.bukkit.configuration.MemorySection
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.XMaterial
import taboolib.module.nms.itemTagReader
import taboolib.platform.util.buildItem
import java.util.LinkedHashMap

fun loadMaterialPart(config: YamlConfiguration, root: String) : DepazItems {
    val legacyApi = LegacyComponentSerializer.legacyAmpersand()
    val mm = MiniMessage.miniMessage()

    val item = buildItem(XMaterial.valueOf(config["$root.minecraft.material"].toString()))

    val itemTag = item.itemTagReader {
        //val value = getString("SERTRALINE_ID", "$root")
        set("SERTRALINE_ID", "$root")
        // 收尾方法 写了才算写入物品 不然不会写入 减少操作可能出现的失误
        write(item)
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

    var attributes : MutableList<Attribute> = mutableListOf()
    val atbsections = config.getList("$root.attribute") as List<LinkedHashMap<String, Any>>?

    if (sections != null && !sections.isEmpty()) for (section in sections) {
        val actionList = if (section["content"] is ArrayList<*>) section["content"] as ArrayList<String> else (section["content"] as String).split("\n")
        attributes.add(
           Attribute(
               attr = TODO(),
               definer = TODO(),
               uuid = TODO(),
               requireSlot = TODO()
           )
        )
    }




    return DepazItems(root, item, actions, attributes)
}