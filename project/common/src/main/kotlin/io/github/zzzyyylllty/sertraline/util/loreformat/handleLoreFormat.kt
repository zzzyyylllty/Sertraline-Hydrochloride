package io.github.zzzyyylllty.sertraline.util.loreformat

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.loreFormats
import io.github.zzzyyylllty.sertraline.config.asListEnhanded
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import net.kyori.adventure.text.Component
import io.github.zzzyyylllty.sertraline.util.minimessage.toComponent
import org.bukkit.entity.Player
import taboolib.module.kether.KetherFunction
import taboolib.module.kether.ScriptOptions
import taboolib.platform.compat.replacePlaceholder

fun handleLoreFormat(item: ModernSItem,player: Player): List<Component>? {
    val loreFormat = loreFormats[item.data["sertraline:lore-format"]] ?: return null
    val list = mutableListOf<Component>()
    for (element in loreFormat.elements) {
        if (element.key != null) {
            val keyValue = item.data[element.key].asListEnhanded() ?: continue
            keyValue.forEach {
                list.add(it.performNormalPlaceholders(element.content, player, item).toComponent())
            }
        } else {
            element.content.performPlaceholders(item, player)?.let { list.add(it.toComponent()) }
        }
    }
    return list
}
fun Any?.performNormalPlaceholders(content: String,player: Player,sItem: ModernSItem): String {
    val numeral = this as? Double
    val string = this.toString()
    var content = content
    if (numeral != null) {
        if (numeral > 0.0) config.getString("placeholders.plus", "+")?.let { content = content.replace("{plus}", it) }
        if (numeral < 0.0) config.getString("placeholders.minus", "-")?.let { content = content.replace("{minus}", it) }
    }
    content = content.replace("{value}", string)
    content.performPlaceholders(sItem, player)
    return content
}
fun String?.performPlaceholders(sItem: ModernSItem,player: Player): String? {
    var content = this ?: return null
    content = content.replacePlaceholder(player)

    // inline kether
    content = KetherFunction.parse(
        content,
        ScriptOptions.new {
            sender(player)
        }
    )

    return content
}