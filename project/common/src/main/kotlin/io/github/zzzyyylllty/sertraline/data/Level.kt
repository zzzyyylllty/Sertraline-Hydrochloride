package io.github.zzzyyylllty.sertraline.data

/**
 * Item level data class
 * @property id the level ID, used to reference it in config and items
 * @property name the level display name
 * @property description the level description
 * @property color the level color (MiniMessage format)
 * @property weight the level weight, used for random selection etc.
 * @property extra extra attributes
 */
data class Level(
    val id: String,
    val name: String,
    val description: String = "",
    val color: String = "<white>",
    val weight: Int = 1,
    val extra: Map<String, Any?> = mapOf()
)