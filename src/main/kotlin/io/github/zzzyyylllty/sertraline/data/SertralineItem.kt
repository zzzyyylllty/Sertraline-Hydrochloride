package io.github.zzzyyylllty.sertraline.data

import io.github.zzzyyylllty.sertraline.function.sertralize.AnySerializer
import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
data class SertralineItem(
    val minecraftItem: SertralineMaterial,
    val sertralineMeta: SertralineMeta,
) {
}

@Serializable
data class SertralineMeta(
    val key: Key,
    val parent: Key,
    val data: LinkedHashMap<String, @Serializable(AnySerializer::class) Any>,
    val customMeta: LinkedHashMap<Key, @Serializable(AnySerializer::class) Any>,
)

@Serializable
data class SertralineMaterial(
    val material: String,
    val displayName: String,
    val lore: List<String>,
    val nbt: LinkedHashMap<String, @Serializable(AnySerializer::class) Any>
)

@Serializable
data class Key(
    val namespace: String,
    val name: String,
)
