package io.github.zzzyyylllty.sertraline.config

import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.Sertraline.mappings
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.logger.severeS
import io.github.zzzyyylllty.sertraline.logger.warningS
import io.github.zzzyyylllty.sertraline.util.minimessage.legacyToMiniMessage
import io.github.zzzyyylllty.sertraline.util.minimessage.mmLegacyUtil
import io.github.zzzyyylllty.sertraline.util.minimessage.mmUtil
import io.github.zzzyyylllty.sertraline.util.serialize.isListOfType
import taboolib.module.lang.asLangText

public class ConfigUtil {
    fun getString(input: Any?): String? {
        return input?.toString()
    }
    fun getInt(input: Any?): Int? {
        return input?.toString()?.toInt()
    }

    fun getLong(input: Any?): Long? {
        return input?.toString()?.toLong()
    }
    fun getDeep(input: Any?, location: String): Any? {
        if (input == null || location.isEmpty()) return null

        val keys = location.split(".")
        var current: Any? = input

        for (key in keys) {
            if (current !is Map<*, *>) return null
            current = current[key]
        }
        return current
    }
    fun existDeep(input: Any?, location: String): Boolean {
        if (input == null || location.isEmpty()) return false

        val keys = location.split(".")
        var current: Any? = input

        for (key in keys) {
            if (current !is Map<*, *>) return (current != null)
            current = current[key]
        }
        return (current != null)
    }

    fun getFeature(input: Map<*, *>?, feature: String): Any? {
        if (input == null) {
            return null
        }

        val mapping = mappings[feature]
        if (mapping == null) {
            warningS(console.asLangText("Warning_No_Mapping", feature))
            return null
        }

        for (entry in mapping) {
            val (namespace, id) = entry.split(":", limit = 2).let { it[0] to it.getOrElse(1) { "" } }
            val section = input[namespace] as? Map<*, *>
            if (section?.contains(id) == true) {
                return section[id]
            }
        }
        return null
    }

    fun getFeatures(
        input: Map<*, *>?,
        features: List<String>,
        final: Map<String, Any?>? = null
    ): Map<String, Any?> {
        val baseMap = final?.toMutableMap() ?: linkedMapOf()
        features.forEach { feature ->
            val unparsed = getFeature(input, feature)
            baseMap[feature] = unparsed?.let { transformValue(it) }
        }
        return baseMap
    }

    private fun transformValue(value: Any): Any = when {
        value is String -> value.legacyToMiniMessage()
        value is List<*> && isListOfType<String>(value) -> value.legacyToMiniMessage()
        else -> value
    }


}
