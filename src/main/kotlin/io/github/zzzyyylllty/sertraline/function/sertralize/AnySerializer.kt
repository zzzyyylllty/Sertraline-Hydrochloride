package io.github.zzzyyylllty.sertraline.function.sertralize
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.SerializersModule

object AnySerializer : KSerializer<Any> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Any", PrimitiveKind.STRING)

    @OptIn(InternalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: Any) {
        val jsonEncoder = encoder as? JsonEncoder ?: throw SerializationException("需要 JSON 编码器")
        jsonEncoder.encodeJsonElement(Json.encodeToJsonElement(value))
    }

    override fun deserialize(decoder: Decoder): Any {
        val jsonDecoder = decoder as? JsonDecoder ?: throw SerializationException("需要 JSON 解码器")
        val element = jsonDecoder.decodeJsonElement()
        return parseJsonElement(element)
    }

    private fun parseJsonElement(element: JsonElement): Any {
        return when (element) {
            is JsonObject -> element.mapValues { parseJsonElement(it.value) }
            is JsonArray -> element.map { parseJsonElement(it) }
            is JsonPrimitive -> parseJsonPrimitive(element)
        }
    }

    private fun parseJsonPrimitive(element: JsonPrimitive): Any {
        return when {
            element.isString -> element.content
            element.booleanOrNull != null -> element.boolean
            element.longOrNull != null -> element.long
            element.doubleOrNull != null -> element.double
            else -> element.content // Fallback to string if unknown
        }
    }
}