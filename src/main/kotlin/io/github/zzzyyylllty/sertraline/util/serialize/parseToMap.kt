package io.github.zzzyyylllty.sertraline.util.serialize

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.toml.TomlFactory
// import com.fasterxml.jackson.dataformat.hocon.HoconFactory
import com.fasterxml.jackson.module.kotlin.*
import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.type.TypeReference

fun parseToMap(data: String, format: String): Map<String, Any?>? {
    val factory: JsonFactory = when (format.lowercase()) {
        "json" -> null // Use default JsonFactory
        "yaml" -> YAMLFactory()
        "toml" -> TomlFactory()
        // "hocon" -> HoconFactory()
        else -> throw IllegalArgumentException("Unsupported format: $format")
    } ?: JsonFactory() // Default to JSON if format is not specified

    return try {
        val mapper = ObjectMapper(factory).registerKotlinModule()
        mapper.readValue(data, object : TypeReference<Map<String, Any?>>() {})
    } catch (e: Exception) {
        println("Error parsing $format: ${e.message}")
        null
    }
}