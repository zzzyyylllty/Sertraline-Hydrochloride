package io.github.zzzyyylllty.sertraline.data

import java.util.UUID

data class Attribute(
    val type: AttributeSources = AttributeSources.MYTHIC_LIB,
    val attr: String,
    val definer: String,
    val uuid: UUID,
    // val override: Boolean = true,
    val chance: String = "100.0",
    val amount: String = "1",
    val source: String = "VOID", // MythicLib 等一些玩意有效
    val mythicLibEquipSlot: String = "OTHER", // MythicLib 等一些玩意有效
    val requireSlot: List<String>,
    val conditionOnBuild: String? = null,
    val conditionOnEffect: String? = null
)
enum class AttributeSources {
    MYTHIC_LIB,
    //ATTRIBUTE_PLUS,
    //MYTHIC_MOBS,
    ///SX_ATTRIBUTE_2,
    //SX_ATTRIBUTE_3
}

data class AttributeInst(
    val type: AttributeSources = AttributeSources.MYTHIC_LIB,
    val attr: String,
    val definer: String,
    val uuid: UUID,
    val amount: String = "1",
    val source: String = "OTHER", // MythicLib 等一些玩意有效
    val mythicLibEquipSlot: String = "OTHER", // MythicLib 等一些玩意有效
    val requireSlot: List<String>
)