package io.github.zzzyyylllty.sertraline.config

import io.github.zzzyyylllty.sertraline.logger.severeL
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

        // 检查是否支持的格式
        if (!isSupportedFormat(format)) {
            severeL("Item_Load_Error_Extension", file.extension)
            return null
        }

        val content = file.readText()
        if (content.isBlank()) {
            severeL("Item_Load_Error_Empty", file.name)
            return null
        }

        parseToMap(content, format)
    } catch (e: Exception) {
        severeL("Item_Load_Error_Parse", file.name, e.message ?: "Unknown error")
        null
    }
}

// 辅助方法：检查是否支持的格式
private fun isSupportedFormat(format: String): Boolean {
    return format in listOf("yaml", "toml", "json") // 根据你的实际支持格式修改
}