package io.github.zzzyyylllty.sertraline.util.minimessage

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

fun List<Any?>.legacyToMiniMessage(): List<Any?> {
    if ((this as? List<String>) == null) return this
    val list = mutableListOf<String>()
    this.forEach { list.add(it.legacyToMiniMessage()) }
    return list
}

fun String.legacyToMiniMessage(): String {
    var text = this

    // 1. 先处理 Hex 颜色代码 - 旧版 &x&6&6&c&c&f&f
    // 匹配形式：&x&6&6&c&c&f&f （固定6个16进制字符）
    val legacyHexPattern = Regex("&x((&[0-9a-fA-F]){6})")
    text = legacyHexPattern.replace(text) { matchResult ->
        val hexChars = matchResult.groupValues[1].chunked(2).joinToString("") { it.substring(1) }
        "<#$hexChars>"
    }

    // 2. 处理现代形式的 Hex Hex代码：&#66ccff 或 &#ABCDEF
    val modernHexPattern = Regex("&#([0-9a-fA-F]{6})")
    text = modernHexPattern.replace(text) { matchResult ->
        "<#${matchResult.groupValues[1].lowercase()}>"
    }

    // 3. 处理颜色代码（旧版，无大小写敏感）
    val colorMap = mapOf(
        '0' to "<black>",
        '1' to "<dark_blue>",
        '2' to "<dark_green>",
        '3' to "<dark_aqua>",
        '4' to "<dark_red>",
        '5' to "<dark_purple>",
        '6' to "<gold>",
        '7' to "<gray>",
        '8' to "<dark_gray>",
        '9' to "<blue>",
        'a' to "<green>",
        'b' to "<aqua>",
        'c' to "<red>",
        'd' to "<light_purple>",
        'e' to "<yellow>",
        'f' to "<white>"
    )

    for ((code, tag) in colorMap) {
        val pattern = Regex("[&§]$code", RegexOption.IGNORE_CASE)
        text = pattern.replace(text, tag)
    }

    // 4. 处理格式代码
    val formatMap = mapOf(
        'k' to "<obfuscated>",
        'l' to "<bold>",
        'm' to "<strikethrough>",
        'n' to "<underlined>",
        'o' to "<italic>"
    )

    for ((code, tag) in formatMap) {
        val pattern = Regex("[&§]$code", RegexOption.IGNORE_CASE)
        text = pattern.replace(text, tag)
    }

    // 5. 处理重置代码
    text = Regex("[&§]r", RegexOption.IGNORE_CASE).replace(text, "<reset>")

    return text
}
