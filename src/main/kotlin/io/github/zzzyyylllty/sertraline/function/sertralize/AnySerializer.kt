package io.github.zzzyyylllty.sertraline.function.sertralize

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*

object AnySerializer : KSerializer<Any> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Any", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Any) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw IllegalStateException("仅支持 JSON 编码")
        jsonEncoder.encodeJsonElement(Json.encodeToJsonElement(value))
    }

    override fun deserialize(decoder: Decoder): Any {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw IllegalStateException("仅支持 JSON 解码")
        return jsonDecoder.decodeJsonElement()
    }
}