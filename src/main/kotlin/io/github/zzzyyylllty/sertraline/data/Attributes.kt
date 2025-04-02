package io.github.zzzyyylllty.sertraline.data

import kotlinx.serialization.Serializable
import java.util.UUID

data class AttributePart(
    val attributeSources: AttributeSources = AttributeSources.MYTHIC_LIB,
    val attr: LinkedHashMap<String, String>,
    val definer: String,
    val uuid: String?,
    // val override: Boolean = true,
    val chance: String = "100.0",
    val source: String = "VOID", // MythicLib 等一些玩意有效
    val mythicLibEquipSlot: String = "OTHER", // MythicLib 等一些玩意有效
    val requireSlot: List<String>,
    val conditionOnBuild: String? = null,
    val conditionOnEffect: String? = null
)
@Serializable
enum class AttributeSources {
    MYTHIC_LIB,
    //ATTRIBUTE_PLUS,
    //MYTHIC_MOBS,
    ///SX_ATTRIBUTE_2,
    //SX_ATTRIBUTE_3
}
@Serializable
data class AttributeInst(
    val attributeSources: AttributeSources = AttributeSources.MYTHIC_LIB,
    val attr: LinkedHashMap<String, String> = LinkedHashMap<String, String>(),
    val definer: String = "sertraline",
    val uuid: String? = UUID.randomUUID().toString(),
    val source: String = "OTHER", // MythicLib 等一些玩意有效
    val mythicLibEquipSlot: String = "OTHER", // MythicLib 等一些玩意有效
    val requireSlot: List<String>
)
