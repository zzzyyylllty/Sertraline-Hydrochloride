package io.github.zzzyyylllty.data

data class SertralineItem(
    val compatibilityData: CompatibilityData?,
    val sertralineData: SertralinePartData?,
    val attribute: ArrayList<SingleAttribute>,
    val actionsData: LinkedHashMap<String, SingleActionsData>,
)
