package io.github.zzzyyylllty.sertraline.config

import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.logger.severeL
import io.github.zzzyyylllty.sertraline.util.serialize.isSupportedFormat
import io.github.zzzyyylllty.sertraline.util.serialize.parseToMap
import io.github.zzzyyylllty.sertraline.util.textIsBlank
import java.io.File
import io.github.zzzyyylllty.sertraline.util.toLowerCase

fun multiExtensionLoader(file: File): Map<String, Any?>? {
    return try {
        val extension = file.extension.lowercase()
        val format = when (extension) {
            "yml" -> "yaml"
            "tml" -> "toml"
            else -> extension
        }

        if (!isSupportedFormat(format)) {
            severeL("Config_Load_Error_Extension", file.extension)
            return null
        }

        val content = file.readText()
        // textIsBlank：Java 8 安全实现（String.isBlank 是 Java 11 成员方法，见 StringUtil.kt）
        if (content.textIsBlank()) {
            severeL("Config_Load_Error_Empty", file.name)
            return null
        }
        // Skip files with only comments — not real YAML content
        if (content.lineSequence().none { line ->
                val trimmed = line.trimStart { it == ' ' || it == '\t' }
                trimmed.isNotEmpty() && !trimmed.startsWith("#")
            }) {
            devLog("Skipping ${file.name}: no YAML content (comments only)")
            return null
        }

        parseToMap(content, format)
    } catch (e: Exception) {
        severeL("Config_Load_Error_Parse", file.name, e.message ?: "Unknown error")
        null
    }
}