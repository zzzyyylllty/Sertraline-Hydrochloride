package io.github.zzzyyylllty.sertraline.function.item

fun relativeOrFlat(s: String) : RelativeOrFlatValue {
    return if (s.endsWith("%")) return RelativeOrFlatValue(
        type = ValueType.RELATIVE,
        value = s.substring(0,s.length-2).toDouble()
    ) else return RelativeOrFlatValue(
        ValueType.FLAT,
        s.toDouble()
    )
}

data class RelativeOrFlatValue(
    val type: ValueType,
    val value: Double
)

enum class ValueType{
    RELATIVE,
    FLAT
}