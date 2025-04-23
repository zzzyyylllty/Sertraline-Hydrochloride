package io.github.zzzyyylllty.sertraline.function.sertralize
/*
object AnySerializer : KSerializer<Any> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Any", PrimitiveKind.STRING)

    @OptIn(InternalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: Any) {
        val jsonEncoder = encoder as? JsonEncoder ?: throw SerializationException("需要 JSON 编码器")
        devLog("Serializeing Any")
        jsonEncoder.encodeJsonElement(Json.encodeToJsonElement(value))
    }

    override fun deserialize(decoder: Decoder): Any {
        val jsonDecoder = decoder as? JsonDecoder ?: throw SerializationException("需要 JSON 解码器")
        devLog("DE-Serializeing Any")
        val element = jsonDecoder.decodeJsonElement()
        return Json.decodeFromJsonElement(String.serializer(), element)
    }
}
*/
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.encoding.*
import kotlinx.serialization.encoding.encodeCollection
import kotlinx.serialization.descriptors.*

object AnySerializer : KSerializer<Any> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Any", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Any) {
        devLog("Serializeing Any")
        when (value) {
            is Boolean -> encoder.encodeBoolean(value)
            is Int -> encoder.encodeInt(value)
            is Long -> encoder.encodeLong(value)
            is Float -> encoder.encodeFloat(value)
            is Double -> encoder.encodeDouble(value)
            is String -> encoder.encodeString(value)
            is List<*> -> {
                val listSerializer = ListSerializer(AnySerializer)
                encoder.encodeSerializableValue(listSerializer, value as List<Any>)
            }
            is Map<* ,*> -> {
                val listSerializer = MapSerializer(AnySerializer, AnySerializer)
                encoder.encodeSerializableValue(listSerializer, value as Map<Any, Any>)
            }
            else -> throw SerializationException("Unsupported type: ${value::class}")
        }
    }

    override fun deserialize(decoder: Decoder): Any {
        devLog("DE-Serializeing Any")
        return decoder.decodeString()
    }
}