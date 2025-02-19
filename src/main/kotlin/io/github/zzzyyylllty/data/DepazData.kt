package io.github.zzzyyylllty.data

data class DepazData(
    val fixedData: LinkedHashMap<String, SingleData>, // String = idef
    val valuesData: LinkedHashMap<String, SingleData>,
    val variablesData: LinkedHashMap<String, SingleData>
)

data class SingleData(
    val type: SingleDataTypes,
    val vals: Any?
)
/*
*
      - idef: attack
        type: value
        val: 1
        * */
enum class SingleDataTypes {
    VALUE,
    RANDOM,
    WEIGHT
}