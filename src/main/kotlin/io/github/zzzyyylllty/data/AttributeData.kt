package io.github.zzzyyylllty.data


data class SingleAttribute(
    val type: AttributeSources = AttributeSources.MYTHIC_LIB,
    val attr: String,
    val idef: String?,
    val override: Boolean = true,
    val chance: Double = 100.0,
    val amount: String = "1",
    val saveInNbt: Boolean = true
)
enum class AttributeSources {
    MYTHIC_LIB,
    ATTRIBUTE_PLUS,
    MYTHIC_MOBS,
    SX_ATTRIBUTE_2,
    SX_ATTRIBUTE_3
}
