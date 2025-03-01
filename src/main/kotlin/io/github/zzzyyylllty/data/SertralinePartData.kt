package io.github.zzzyyylllty.data

data class SertralinePartData(
    val id: String,
    val name: String?,
    val material: String?,
    val nbts: LinkedHashMap<String, Any?>,
    val lore: List<String>?,
    val model: Double?,
    val updateId: Double?,
    val fixedData: LinkedHashMap<String, SingleData>?, // String = idef
    val valuesData: LinkedHashMap<String, SingleData>?,
    val variablesData: LinkedHashMap<String, SingleData>?
)

data class SingleData(
    val type: SingleDataTypes,
    val values: Any?
)
/*
*
      - idef: attack
        type: value
        val: 1
        * */
enum class SingleDataTypes {
    VALUE,
    MATH,
    RANDOM,
    WEIGHT
}