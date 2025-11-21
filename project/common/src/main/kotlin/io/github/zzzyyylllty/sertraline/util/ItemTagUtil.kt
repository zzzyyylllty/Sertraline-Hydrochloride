package io.github.zzzyyylllty.sertraline.util

import taboolib.module.nms.ItemTagData
import taboolib.module.nms.ItemTagSerializer.serializeList
import taboolib.module.nms.ItemTagType

fun ItemTagData.parseNBT(): Any {
    val tagData = this
    return when (tagData.type) {
        ItemTagType.COMPOUND -> (tagData).parseMapNBT()
        ItemTagType.LIST -> serializeList(tagData.asList())
        ItemTagType.BYTE -> tagData.asByte()
        // ItemTagType.BOOLEAN -> tagData.asBoolean()
//        ItemTagType.BOOLEAN -> tagData.asByte()
        ItemTagType.SHORT -> tagData.asShort()
        ItemTagType.INT -> tagData.asInt()
        ItemTagType.LONG -> tagData.asLong()
        ItemTagType.FLOAT -> tagData.asFloat()
        ItemTagType.DOUBLE -> tagData.asDouble()
        ItemTagType.STRING, ItemTagType.END -> tagData.asString()
        ItemTagType.INT_ARRAY -> tagData.asIntArray()
        ItemTagType.BYTE_ARRAY -> tagData.asByteArray()
        ItemTagType.LONG_ARRAY -> tagData.asLongArray()
    }
}
fun ItemTagData.parseMapNBT(): Map<String, Any> {
    val tagData = this.asCompound().entries

    val map = mutableMapOf<String, Any>()
    tagData.forEach {
        map[it.key] = it.value.parseNBT()
    }
    return map
}