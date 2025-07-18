package io.github.zzzyyylllty.sertraline.data

import io.github.zzzyyylllty.sertraline.function.sertralize.AnySerializer
import kotlinx.serialization.Serializable

@Serializable
data class SertralineItem(
    val minecraftItem: SertralineMaterial,
    val sertralineMeta: SertralineMeta,
    val customMeta: HashMap<String, @Serializable(AnySerializer::class) Any?> = hashMapOf(),
) {
}

@Serializable
data class SertralineMeta(
    val key: Key,
    val parent: Key? = null,
    val data: HashMap<String, @Serializable(with = AnySerializer::class) Any?> = hashMapOf(),
)

@Serializable
data class SertralineMaterial(
    val material: String? = "STONE",
    val displayName: String? = null,
    val lore: List<String>? = null,
    val model: Int? = null,
    val nbt: HashMap<String, @Serializable(with = AnySerializer::class) Any?>? = hashMapOf(),
    val extra: HashMap<String, @Serializable(with = AnySerializer::class) Any?> = hashMapOf()
)

@Serializable
data class Key(
    val namespace: String,
    val name: String,
) {
    fun serialize(): String {
        return "$namespace:$name"
    }
}
