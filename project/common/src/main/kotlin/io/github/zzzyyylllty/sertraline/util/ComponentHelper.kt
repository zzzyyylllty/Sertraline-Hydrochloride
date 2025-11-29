package io.github.zzzyyylllty.sertraline.util

import io.github.zzzyyylllty.sertraline.util.minimessage.mmUtil
import net.kyori.adventure.text.Component


object ComponentFormatter {

    // 颜色定义 (十六进制)
    private const val BYTE_BOOLEAN_COLOR = "#FF6666"   // 红
    private const val FLOAT_COLOR = "#FFFF99"        // 浅黄
    private const val DOUBLE_COLOR = "#99FF99"       // 浅绿
    private const val INT_COLOR = "#3366FF"          // 深蓝
    private const val LONG_COLOR = "#66CCFF"         // 湖蓝
    private const val SHORT_COLOR = "#FF99CC"        // 粉色
    private const val KEY_COLOR = "#CCCCCC"           // 浅灰 (用于 Map 的 key)
    private const val COMPONENT_NAME_COLOR = "#FFFFFF"  // 白色 (组件名称)
    private const val BRACKET_COLOR = "#BBBBBB"       // 稍暗的灰色 (括号)

    /**
     * 将组件 Map 转换为易于阅读的 MiniMessage 格式的 Component.
     *
     * @param componentMap LinkedHashMap<String, Any> 组件 Map
     * @return Component MiniMessage 格式的 Component
     */
    fun formatComponentMap(componentMap: Map<String, Any?>): Component {
        val sb = StringBuilder()
        sb.append("<").append(BRACKET_COLOR).append(">{").append("</").append(BRACKET_COLOR).append(">\n") // 根部的括号

        componentMap.forEach { (key, value) ->
            value?.let { formatEntry(key, it, sb, 1) }
        }

        sb.append("<").append(BRACKET_COLOR).append(">}").append("</").append(BRACKET_COLOR).append(">") // 根部的括号

        return mmUtil.deserialize(sb.toString())
    }

    /**
     * 递归格式化 Map 中的每个条目.
     *
     * @param key String 条目的键
     * @param value Any 条目的值
     * @param sb StringBuilder 用于构建 MiniMessage 字符串
     * @param indentLevel Int 缩进级别
     */
    private fun formatEntry(key: String, value: Any, sb: StringBuilder, indentLevel: Int) {
        val indent = "  ".repeat(indentLevel)
        sb.append(indent).append("<").append(KEY_COLOR).append(">").append(key).append("</").append(KEY_COLOR).append(">: ")

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
                sb.append("<").append(BRACKET_COLOR).append(">[</").append(BRACKET_COLOR).append(">\n")
                value.forEach { item ->
                    formatListItem(item, sb, indentLevel + 1)
                }
                sb.append(indent).append("<").append(BRACKET_COLOR).append(">]</").append("</").append(BRACKET_COLOR).append(">\n")
            }
            is LinkedHashMap<*, *> -> {
                sb.append("<").append(BRACKET_COLOR).append(">{</").append(BRACKET_COLOR).append(">\n")
                value.forEach { (k, v) ->
                    if (k != null) {
                        if (v != null) {
                            formatEntry(k.toString(), v, sb, indentLevel + 1)
                        }
                    }
                }
                sb.append(indent).append("<").append(BRACKET_COLOR).append(">}</").append("</").append(BRACKET_COLOR).append(">\n")
            }
            else -> {
                sb.append("<").append(COMPONENT_NAME_COLOR).append(">").append(value.toString()).append("</").append(COMPONENT_NAME_COLOR).append(">\n")
            }
        }
    }

    /**
     * 格式化 List 中的每个条目.
     *
     * @param item Any List 中的条目
     * @param sb StringBuilder 用于构建 MiniMessage 字符串
     * @param indentLevel Int 缩进级别
     */
    private fun formatListItem(item: Any?, sb: StringBuilder, indentLevel: Int) {
        val indent = "  ".repeat(indentLevel)
        sb.append(indent).append("- ")
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
                sb.append("<").append(BRACKET_COLOR).append(">[</").append(BRACKET_COLOR).append(">\n")
                item.forEach { subItem ->
                    formatListItem(subItem, sb, indentLevel + 1)
                }
                sb.append(indent).append("<").append(BRACKET_COLOR).append(">]</").append("</").append(BRACKET_COLOR).append(">\n")
            }
            is LinkedHashMap<*, *> -> {
                sb.append("<").append(BRACKET_COLOR).append(">{</").append(BRACKET_COLOR).append(">\n")
                item.forEach { (k, v) ->
                    if (k != null) {
                        if (v != null) {
                            formatEntry(k.toString(), v, sb, indentLevel + 1)
                        }
                    }
                }
                sb.append(indent).append("<").append(BRACKET_COLOR).append(">}</").append("</").append(BRACKET_COLOR).append(">\n")
            }
            else -> {
                if (item != null) {
                    sb.append("<").append(COMPONENT_NAME_COLOR).append(">").append(item.toString()).append("</").append(COMPONENT_NAME_COLOR).append(">\n")
                } else {
                    sb.append("<").append(COMPONENT_NAME_COLOR).append(">null</").append(COMPONENT_NAME_COLOR).append(">\n")
                }
            }
        }
    }

}
