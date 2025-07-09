package io.github.zzzyyylllty.sertraline.data

import io.github.zzzyyylllty.sertraline.function.sertralize.AnySerializer
import kotlinx.serialization.Serializable

@Serializable
data class SertralineItem(
    val minecraftItem: SertralineMaterial,
    val sertralineMeta: SertralineMeta,
    val customMeta: LinkedHashMap<String, @Serializable(AnySerializer::class) Any?>,
) {
}

@Serializable
data class SertralineMeta(
    val key: Key,
    val parent: Key?,
    val data: LinkedHashMap<String, @Serializable(with = AnySerializer::class) Any?>,
)

@Serializable
data class SertralineMaterial(
    val material: String?,
    val displayName: String?,
    val lore: List<String>,
    val nbt: LinkedHashMap<String, @Serializable(AnySerializer::class) Any?>
)

@Serializable
data class Key(
    val namespace: String,
    val name: String,
)
