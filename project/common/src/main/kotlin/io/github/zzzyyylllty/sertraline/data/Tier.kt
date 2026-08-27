package io.github.zzzyyylllty.sertraline.data

/**
 * Item quality data class
 * @property id the quality ID, used to reference it in config and items
 * @property name the quality display name
 * @property description the quality description
 * @property color the quality color (MiniMessage format)
 * @property weight the quality weight, used for random selection etc.
 * @property extra extra attributes
 * @property meta metadata, retrievable in scripts via TierUtil.getMeta(id, key)
 */
data class Tier(
    val id: String,
    val name: String,
    val description: String = "",
    val color: String = "<white>",
    val weight: Int = 1,
    val extra: Map<String, Any?> = mapOf(),
    val meta: Map<String, Any?> = mapOf()
)