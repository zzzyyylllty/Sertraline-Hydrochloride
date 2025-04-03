package io.github.zzzyyylllty.sertraline.function.sertralize

import io.github.zzzyyylllty.sertraline.debugMode.devLog
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*
import org.bukkit.inventory.ItemStack

object AnySerializer : KSerializer<Any> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Any", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Any) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw IllegalStateException("仅支持 JSON 编码")
        devLog("Serializing Any")
        jsonEncoder.encodeJsonElement(Json.encodeToJsonElement(value))
    }

    override fun deserialize(decoder: Decoder): Any {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw IllegalStateException("仅支持 JSON 解码")
        devLog("Deserializing Any")
        return jsonDecoder.decodeJsonElement()
    }
}