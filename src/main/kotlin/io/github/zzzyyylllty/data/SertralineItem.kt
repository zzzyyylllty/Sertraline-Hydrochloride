package io.github.zzzyyylllty.data

data class SertralineItem(
    val compatibilityData: CompatibilityData?,
    val sertralineData: SertralineItemData?,
    val attribute: ArrayList<SingleAttribute>,
    val actionsData: ArrayList<SingleActionsData>,
)
