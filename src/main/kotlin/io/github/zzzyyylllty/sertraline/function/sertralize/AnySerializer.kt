package io.github.zzzyyylllty.sertraline.function.sertralize
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.encodeToJsonElement

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