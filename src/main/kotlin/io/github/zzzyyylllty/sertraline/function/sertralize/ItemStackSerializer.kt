package io.github.zzzyyylllty.sertraline.function.sertralize

import io.github.zzzyyylllty.sertraline.function.sertralize.ItemStackSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.encodeToJsonElement
import org.bukkit.inventory.ItemStack


object ItemStackSerializer : KSerializer<ItemStack> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ItemStack", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ItemStack) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw IllegalStateException("仅支持 JSON 编码")
        jsonEncoder.encodeJsonElement(Json.encodeToJsonElement(value.serialize()))
    }

    override fun deserialize(decoder: Decoder): ItemStack {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw IllegalStateException("仅支持 JSON 解码")
        return ItemStack.deserialize(jsonDecoder.decodeJsonElement() as Map<String, @Serializable(with = AnySerializer::class) Any>)
    }
}