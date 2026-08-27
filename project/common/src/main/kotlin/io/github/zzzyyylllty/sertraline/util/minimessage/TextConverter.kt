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

    // 1. Handle hex color codes first - legacy &x&6&6&c&c&f&f
    // Matches the form: &x&6&6&c&c&f&f (fixed six hex characters)
    val legacyHexPattern = Regex("&x((&[0-9a-fA-F]){6})")
    text = legacyHexPattern.replace(text) { matchResult ->
        if (isInsidePlaceholder(text, matchResult.range)) {
            matchResult.value
        } else {
            val hexChars = matchResult.groupValues[1].chunked(2).joinToString("") { it.substring(1) }
            "<#$hexChars>"
        }
    }

    // 2. Handle modern hex color codes: &#66ccff or &#ABCDEF
    val modernHexPattern = Regex("&#([0-9a-fA-F]{6})")
    text = modernHexPattern.replace(text) { matchResult ->
        if (isInsidePlaceholder(text, matchResult.range)) {
            matchResult.value
        } else {
            "<#${matchResult.groupValues[1].lowercase()}>"
        }
    }

    // 3. Handle color codes (legacy, case-insensitive)
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
        text = pattern.replace(text) { matchResult ->
            if (isInsidePlaceholder(text, matchResult.range)) {
                matchResult.value
            } else {
                tag
            }
        }
    }

    // 4. Handle format codes
    val formatMap = mapOf(
        'k' to "<obfuscated>",
        'l' to "<bold>",
        'm' to "<strikethrough>",
        'n' to "<underlined>",
        'o' to "<italic>"
    )

    for ((code, tag) in formatMap) {
        val pattern = Regex("[&§]$code", RegexOption.IGNORE_CASE)
        text = pattern.replace(text) { matchResult ->
            if (isInsidePlaceholder(text, matchResult.range)) {
                matchResult.value
            } else {
                tag
            }
        }
    }

    // 5. Handle reset code
    text = Regex("[&§]r", RegexOption.IGNORE_CASE).replace(text) { matchResult ->
        if (isInsidePlaceholder(text, matchResult.range)) {
            matchResult.value
        } else {
            "<reset>"
        }
    }

    return text
}

fun isInsidePlaceholder(text: String, range: IntRange): Boolean {
    val start = text.lastIndexOf("\${", range.first)
    val end = text.indexOf("}$", range.last)

    return start != -1 && end != -1 && start < range.first && end > range.last
}
