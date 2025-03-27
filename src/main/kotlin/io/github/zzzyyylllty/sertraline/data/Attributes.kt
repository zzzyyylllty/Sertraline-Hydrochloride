package io.github.zzzyyylllty.sertraline.data

data class Attribute(
    val type: AttributeSources = AttributeSources.MYTHIC_LIB,
    val attr: String,
    val definer: String?,
    val override: Boolean = true,
    val chance: Double = 100.0,
    val amount: String = "1",
)
enum class AttributeSources {
    MYTHIC_LIB,
    //ATTRIBUTE_PLUS,
    //MYTHIC_MOBS,
    ///SX_ATTRIBUTE_2,
    //SX_ATTRIBUTE_3
}
