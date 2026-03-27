package io.github.zzzyyylllty.sertraline.data

data class CraftingStation(
    val option: Map<String, Any?>,
    val display: DisplayConfig,
    val recipes: Map<String, StationRecipe>
)

data class DisplayConfig(
    val title: String,
    val layout: List<String>,
    val elements: Map<String, ElementConfig>,
    val key: String
)

data class ElementConfig(
    val char: String? = null,
    val slot: Int? = null,
    val material: String,
    val name: String,
    val lore: String? = null,
    val roll: Boolean? = null,
    val agents: Map<String, String>? = null
)

data class StationRecipe(
    val displayName: String,
    val displayTime: String? = null,
    val time: String,
    val options: Map<String, Any?>,
    val conditions: List<ConditionConfig>? = null,
    val messages: Map<String, String>? = null,
    val display: OmniItem? = null,
    val inputs: List<StationRecipeInput>,
    val outputs: List<OmniItem>,
    val agents: Map<String, String>? = null
)

data class ConditionConfig(
    val name: String,
    val type: String,
    val amount: String,
    val required: String,
    val condition: String,
    val agents: Map<String, String>? = null
)

data class StationRecipeInput(
    val displayName: String,
    val input: String,
    val plural: String?,
)