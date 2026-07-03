package io.github.zzzyyylllty.sertraline.config

import io.github.zzzyyylllty.sertraline.logger.severeL
import io.github.zzzyyylllty.sertraline.util.serialize.isSupportedFormat
import io.github.zzzyyylllty.sertraline.util.serialize.parseToMap
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
        if (content.isBlank()) {
            severeL("Config_Load_Error_Empty", file.name)
            return null
        }

        parseToMap(content, format)
    } catch (e: Exception) {
        severeL("Config_Load_Error_Parse", file.name, e.message ?: "Unknown error")
        null
    }
}