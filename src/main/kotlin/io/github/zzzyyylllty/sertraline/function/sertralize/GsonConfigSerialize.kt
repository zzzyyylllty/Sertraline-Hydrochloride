package io.github.zzzyyylllty.sertraline.function.sertralize

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.ConfigurationSerialization
import java.lang.reflect.Type


class ConfigurationSerializableAdapter : JsonSerializer<ConfigurationSerializable?>,
    JsonDeserializer<ConfigurationSerializable?> {
    val objectStringMapType: Type? = object : TypeToken<MutableMap<String?, Any?>?>() {}.getType()

    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext,
    ): ConfigurationSerializable? {
        val map: MutableMap<String?, Any?> = LinkedHashMap<String?, Any?>()

        for (entry in json.getAsJsonObject().entrySet()) {
            val value = entry.value
            val name = entry.key

            if (value.isJsonObject() && value.getAsJsonObject().has(ConfigurationSerialization.SERIALIZED_TYPE_KEY)) {
                map.put(name, this.deserialize(value, value.javaClass, context))
            } else {
                map.put(name, context.deserialize<Any?>(value, Any::class.java))
            }
        }

        return ConfigurationSerialization.deserializeObject(map)
    }

    override fun serialize(
        src: ConfigurationSerializable?,
        typeOfSrc: Type,
        context: JsonSerializationContext,
    ): JsonElement {
        val map: MutableMap<String?, Any?> = LinkedHashMap<String?, Any?>()
        src?.let { map.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(it.javaClass)) }
        src?.let { map.putAll(it.serialize()) }
        return context.serialize(map, objectStringMapType)
    }
}