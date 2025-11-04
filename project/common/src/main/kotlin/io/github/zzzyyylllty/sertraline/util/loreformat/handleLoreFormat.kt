package io.github.zzzyyylllty.sertraline.util.loreformat

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.configUtil
import io.github.zzzyyylllty.sertraline.Sertraline.loreFormats
import io.github.zzzyyylllty.sertraline.config.asListEnhanded
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import net.kyori.adventure.text.Component
import io.github.zzzyyylllty.sertraline.util.minimessage.toComponent
import io.github.zzzyyylllty.sertraline.util.minimessage.toComponentJson
import org.bukkit.entity.Player
import taboolib.module.kether.KetherFunction
import taboolib.module.kether.ScriptOptions
import taboolib.platform.compat.replacePlaceholder

fun handleLoreFormat(item: ModernSItem, player: Player?): List<Component>? {
    val loreFormat = loreFormats[item.data["sertraline:lore-format"]] ?: return null

    return loreFormat.elements.flatMap { element ->
        val key = element.key
        if (key != null) {
            val keyValueList = if (key.startsWith("*")) {
                configUtil.getDeep(item.config, key.removePrefix("*")).asListEnhanded()
            } else {
                item.data[key].asListEnhanded()
            } ?: emptyList()

            keyValueList.mapNotNull { value ->
                value.performNormalPlaceholders(element.content, player, item)?.toComponent()
            }
        } else {
            element.content.performPlaceholders(item, player)?.toComponent()?.let { listOf(it) } ?: emptyList()
        }
    }.also { devLog("Handled lore format $it") }
}

fun Any?.performNormalPlaceholders(content: String,player: Player?,sItem: ModernSItem): String {
    val numeral = this.toString().toDoubleOrNull()
    val string = this.toString()
    devLog("Numeral: $numeral | string: $string")
    var content = content
    if (numeral != null) {
        if (numeral > 0.0) config.getString("placeholders.plus", "+")?.let { content = content.replace("{plus}", it) }
        if (numeral < 0.0) config.getString("placeholders.minus", "-")?.let { content = content.replace("{minus}", it) }
    }
    content = content.replace("{value}", string).performPlaceholders(sItem, player).toString()
    return content
}
fun String?.performPlaceholders(sItem: ModernSItem,player: Player?): String? {
    var content = this ?: run {
        devLog("content is null, returning null.")
        return null
    }

    devLog("Ready to Perform content: $content")
    player?.let { content = content.replacePlaceholder(it) }

    // inline kether
    content = KetherFunction.parse(
        content,
        ScriptOptions.new {
            player?.let { sender(it) }
        }
    )
    devLog("Performed content: $content")
    return content
}