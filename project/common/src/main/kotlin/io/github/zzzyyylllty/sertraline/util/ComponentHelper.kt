package io.github.zzzyyylllty.sertraline.util

import io.github.zzzyyylllty.sertraline.Sertraline.consoleSender
import io.github.zzzyyylllty.sertraline.util.minimessage.mmJsonUtil
import io.github.zzzyyylllty.sertraline.util.minimessage.mmUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import taboolib.platform.util.asLangText


object ComponentFormatter {

    // Color definitions (hex)
    private const val BYTE_BOOLEAN_COLOR = "#FF6666"   // red
    private const val FLOAT_COLOR = "#FFFF99"        // light yellow
    private const val DOUBLE_COLOR = "#99FF99"       // light green
    private const val INT_COLOR = "#3366FF"          // dark blue
    private const val LONG_COLOR = "#66CCFF"         // lake blue
    private const val SHORT_COLOR = "#FF99CC"                       // pink
    private const val COMPONENT_COLOR = "gradient:#EECCFF:#AA66FF"          // purple
    private const val KEY_COLOR = "gradient:#FFEECC:#EEEE33"          // golden yellow
    private const val STRING_COLOR = "#99CCAA"  // light green
    private const val NULL_COLOR = "gradient:#FF0000:#BB0044"  // dark red
    private const val BRACKET_COLOR = "#BBBBBB"       // slightly darker gray (brackets)
    private const val EMPTY_BRACKET_COLOR = "#996666"            // dark red (empty list/map brackets)

    /**
     * Convert a component Map into a readable MiniMessage-format Component.
     *
     * @param componentMap LinkedHashMap<String, Any> component Map
     * @return Component MiniMessage-format Component
     */
    fun formatComponentMap(componentMap: Map<String, Any?>): Component {
        val sb = StringBuilder()
        sb.append("<").append(BRACKET_COLOR).append(">{").append("</").append(BRACKET_COLOR).append(">\n") // root bracket

        componentMap.forEach { (key, value) ->
            value?.let { formatEntry(key, it, sb, 1) }
        }

        sb.append("<").append(BRACKET_COLOR).append(">}").append("</").append(BRACKET_COLOR).append(">") // root bracket

        return mmUtil.deserialize(sb.toString()).replaceText(TextReplacementConfig.builder().match("SINGLE QUOTATION MARK PLACEHOLDER").replacement("'").build())
    }

    /**
     * Recursively format each entry in a Map.
     *
     * @param key String the key of the entry
     * @param value Any the value of the entry
     * @param sb StringBuilder used to build the MiniMessage string
     * @param indentLevel Int indentation level
     */
    private fun formatEntry(key: String, value: Any, sb: StringBuilder, indentLevel: Int) {
        val indent = repeatString("  ", indentLevel)
        if (indentLevel == 1) {
            sb.append(indent).append("<").append(COMPONENT_COLOR).append(">").append(key).append("</").append(COMPONENT_COLOR).append(">: ")
        } else {
            sb.append(indent).append("<").append(KEY_COLOR).append(">").append(key).append("</").append(KEY_COLOR).append(">: ")
        }
        val value = unwrapJson(value)

        when (value) {
            is Byte, is Boolean -> {
                sb.append("<").append(BYTE_BOOLEAN_COLOR).append(">").append(value.toString()).append("</").append(BYTE_BOOLEAN_COLOR).append(">\n")
            }
            is Float -> {
                sb.append("<").append(FLOAT_COLOR).append(">").append(value.toString()).append("</").append(FLOAT_COLOR).append(">\n")
            }
            is Double -> {
                sb.append("<").append(DOUBLE_COLOR).append(">").append(value.toString()).append("</").append(DOUBLE_COLOR).append(">\n")
            }
            is Int -> {
                sb.append("<").append(INT_COLOR).append(">").append(value.toString()).append("</").append(INT_COLOR).append(">\n")
            }
            is Long -> {
                sb.append("<").append(LONG_COLOR).append(">").append(value.toString()).append("</").append(LONG_COLOR).append(">\n")
            }
            is Short -> {
                sb.append("<").append(SHORT_COLOR).append(">").append(value.toString()).append("</").append(SHORT_COLOR).append(">\n")
            }
            is ByteArray -> {
                sb.append("<").append(BYTE_BOOLEAN_COLOR).append(">[ByteArray size=").append(value.size).append("]</").append(BYTE_BOOLEAN_COLOR).append(">\n")
            }
            is List<*> -> {
                if (value.isEmpty()) {
                    sb.append("<").append(EMPTY_BRACKET_COLOR).append(">[EMPTY]</").append(EMPTY_BRACKET_COLOR).append(">\n")
                } else {
                    sb.append("<").append(BRACKET_COLOR).append(">[</").append(BRACKET_COLOR).append(">\n")
                    value.forEach { item ->
                        formatListItem(item, sb, indentLevel + 1)
                    }
                    sb.append(indent).append("<").append(BRACKET_COLOR).append(">]</").append(BRACKET_COLOR)
                        .append(">\n")
                }
            }
            is Map<*, *> -> {
                if (value.isEmpty()) {
                    sb.append("<").append(EMPTY_BRACKET_COLOR).append(">{EMPTY}</").append(EMPTY_BRACKET_COLOR).append(">\n")
                } else {
                    sb.append("<").append(BRACKET_COLOR).append(">{</").append(BRACKET_COLOR).append(">\n")
                    value.forEach { (k, v) ->
                        if (k != null) {
                            if (v != null) {
                                formatEntry(k.toString(), v, sb, indentLevel + 1)
                            }
                        }
                    }
                    sb.append(indent).append("<").append(BRACKET_COLOR).append(">}</").append(BRACKET_COLOR)
                        .append(">\n")
                }
            }
            else -> {
                if (value != null) {
                    sb.append("<").append(STRING_COLOR).append(">").append(handleStringComponent(value.toString())).append("</").append(STRING_COLOR).append(">\n")
                } else {
                    sb.append("<").append(NULL_COLOR).append(">null</").append(NULL_COLOR).append(">\n")
                }
            }
        }
    }

    /**
     * Format each entry in a List.
     *
     * @param item Any entry in the List
     * @param sb StringBuilder used to build the MiniMessage string
     * @param indentLevel Int indentation level
     */
    private fun formatListItem(item: Any?, sb: StringBuilder, indentLevel: Int) {
        val indent = repeatString("  ", indentLevel)
        sb.append(indent).append("- ")
        val item = unwrapJson(item)
        when (item) {
            is Byte, is Boolean -> {
                sb.append("<").append(BYTE_BOOLEAN_COLOR).append(">").append(item.toString()).append("</").append(BYTE_BOOLEAN_COLOR).append(">\n")
            }
            is Float -> {
                sb.append("<").append(FLOAT_COLOR).append(">").append(item.toString()).append("</").append(FLOAT_COLOR).append(">\n")
            }
            is Double -> {
                sb.append("<").append(DOUBLE_COLOR).append(">").append(item.toString()).append("</").append(DOUBLE_COLOR).append(">\n")
            }
            is Int -> {
                sb.append("<").append(INT_COLOR).append(">").append(item.toString()).append("</").append(INT_COLOR).append(">\n")
            }
            is Long -> {
                sb.append("<").append(LONG_COLOR).append(">").append(item.toString()).append("</").append(LONG_COLOR).append(">\n")
            }
            is Short -> {
                sb.append("<").append(SHORT_COLOR).append(">").append(item.toString()).append("</").append(SHORT_COLOR).append(">\n")
            }
            is ByteArray -> {
                sb.append("<").append(BYTE_BOOLEAN_COLOR).append(">[ByteArray size=").append(item.size).append("]</").append(BYTE_BOOLEAN_COLOR).append(">\n")
            }
            is List<*> -> {
                if (item.isEmpty()) {
                    sb.append("<").append(EMPTY_BRACKET_COLOR).append(">[EMPTY]</").append(EMPTY_BRACKET_COLOR).append(">\n")
                } else {
                    sb.append("<").append(BRACKET_COLOR).append(">[</").append(BRACKET_COLOR).append(">\n")
                    item.forEach { subItem ->
                        formatListItem(subItem, sb, indentLevel + 1)
                    }
                    sb.append(indent).append("<").append(BRACKET_COLOR).append(">]</").append(BRACKET_COLOR)
                        .append(">\n")
                }
            }
            is Map<*, *> -> {
                if (item.isEmpty()) {
                    sb.append("<").append(EMPTY_BRACKET_COLOR).append(">{EMPTY}</").append(EMPTY_BRACKET_COLOR).append(">\n")
                } else {
                    sb.append("<").append(BRACKET_COLOR).append(">{</").append(BRACKET_COLOR).append(">\n")
                    item.forEach { (k, v) ->
                        if (k != null) {
                            if (v != null) {
                                formatEntry(k.toString(), v, sb, indentLevel + 1)
                            }
                        }
                    }
                    sb.append(indent).append("<").append(BRACKET_COLOR).append(">}</").append(BRACKET_COLOR)
                        .append(">\n")
                }
            }
            else -> {
                if (item != null) {
                    sb.append("<").append(STRING_COLOR).append(">").append(handleStringComponent(item.toString())).append("</").append(STRING_COLOR).append(">\n")
                } else {
                    sb.append("<").append(NULL_COLOR).append(">null</").append(NULL_COLOR).append(">\n")
                }
            }
        }
    }

}

// Java 8 compatible: String.repeat is a Java 11 member method (Kotlin 2.2's kotlin.text has no repeat extension), so implemented manually
private fun repeatString(s: String, n: Int): String {
    if (n <= 0) return ""
    val sb = StringBuilder(s.length * n)
    for (i in 1..n) sb.append(s)
    return sb.toString()
}

fun handleStringComponent(input: String): String {
    val result = StringBuilder()
    var i = 0
    while (i < input.length) {
        if (input[i] == '\\') {
            if (i + 1 < input.length && input[i + 1] == '\\') {
                result.append('\\')
                i += 2 // skip two backslashes
            } else {
                // single backslash, skip
                i++
            }
        } else {
            result.append(input[i])
            i++
        }
    }
    return try {
        if (mmJsonUtil.deserialize(result.toString()) == Component.text(result.toString()))
            result.toString()
            else "<hover:show_text:'${result.replace("'".toRegex(),"SINGLE QUOTATION MARK PLACEHOLDER")}'>${mmUtil.serialize(mmJsonUtil.deserialize(result.toString()))} <dark_gray>${consoleSender.asLangText("Collapsed")}</dark_gray></hover>"
    } catch (_: Exception) {
        result.toString()
    }

}