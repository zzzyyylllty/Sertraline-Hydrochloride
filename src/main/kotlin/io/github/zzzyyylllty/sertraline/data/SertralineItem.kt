package io.github.zzzyyylllty.sertraline.data

import io.github.zzzyyylllty.sertraline.function.sertralize.AnySerializer
import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
data class SertralineItem(
    val key: Key,
    val minecraftItem: Material,
    val sertralineMeta: SertralineMeta,
    val customMeta: LinkedHashMap<Key, @Serializable(AnySerializer::class) Any>,
) {
}

@Serializable
data class SertralineMeta(
    val parent: Key
)

@Serializable
data class Key(
    val namespace: String,
    val name: String,
)
