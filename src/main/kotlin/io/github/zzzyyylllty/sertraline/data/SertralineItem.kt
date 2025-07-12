package io.github.zzzyyylllty.sertraline.data

import io.github.zzzyyylllty.sertraline.function.sertralize.AnySerializer
import kotlinx.serialization.Serializable

@Serializable
data class SertralineItem(
    val minecraftItem: SertralineMaterial,
    val sertralineMeta: SertralineMeta,
    val customMeta: LinkedHashMap<String, @Serializable(AnySerializer::class) Any?> = linkedMapOf(),
) {
}

@Serializable
data class SertralineMeta(
    val key: Key,
    val parent: Key? = null,
    val data: LinkedHashMap<String, @Serializable(with = AnySerializer::class) Any?>,
)

@Serializable
data class SertralineMaterial(
    val material: String? = "STONE",
    val displayName: String? = null,
    val lore: List<String>? = null,
    val model: Int? = null,
    val nbt: List<java.util.LinkedHashMap<String, @Serializable(with = AnySerializer::class) Any>>? = listOf(),
    val extra: LinkedHashMap<String, @Serializable(with = AnySerializer::class) Any?> = linkedMapOf()
)

@Serializable
data class Key(
    val namespace: String,
    val name: String,
)
