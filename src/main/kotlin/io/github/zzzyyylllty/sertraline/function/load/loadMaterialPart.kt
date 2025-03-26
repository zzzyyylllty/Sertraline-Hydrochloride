package io.github.zzzyyylllty.sertraline.function.load

import io.github.zzzyyylllty.sertraline.data.Action
import io.github.zzzyyylllty.sertraline.data.DepazItems
import io.github.zzzyyylllty.sertraline.function.error.throwNPEWithMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem

fun loadMaterialPart(config: YamlConfiguration, root: String) : DepazItems {
    val legacyApi = LegacyComponentSerializer.legacyAmpersand()
    val mm = MiniMessage.miniMessage()

    val item = buildItem(XMaterial.valueOf(config["$root.minecraft.material"].toString()))
        var meta = item.itemMeta
    var name = mm.deserialize("<white>${config["$root.minecraft.name"].toString()}")
    name.decorationIfAbsent(TextDecoration.ITALIC,TextDecoration.State.FALSE)
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
    val sections = config.get("$root.actions") as List<ConfigurationSection>

    if (!sections.isEmpty()) for (section in sections) {
        actions.add(
            Action(
                trigger = (section.getString("trigger") ?: throwNPEWithMessage("ACTION_TRIGGER_NOT_FOUND", root)).toString(),
                async = section.getBoolean("async"),
                actions = section.getString("content")?.split("\n") ?: emptyList()
            )
        )
    }

    return DepazItems(root, item, actions)
}