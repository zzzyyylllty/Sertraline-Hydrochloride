package io.github.zzzyyylllty.sertraline.data

/**
 * Item type data class
 * @property id the type ID, used to reference it in config and items
 * @property name the type display name
 * @property parent the parent type ID, for inheritance (optional)
 * @property description the type description
 * @property extra extra attributes
 * @property meta metadata, retrievable in scripts via TypeUtil.getMeta(id, key)
 */
data class Type(
    val id: String,
    val name: String,
    val parent: String? = null,
    val description: String = "",
    val extra: Map<String, Any?> = mapOf(),
    val meta: Map<String, Any?> = mapOf()
)