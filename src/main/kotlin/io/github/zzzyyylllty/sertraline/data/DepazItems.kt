package io.github.zzzyyylllty.sertraline.data

import kotlinx.serialization.Serializable
import org.bukkit.inventory.ItemStack

data class DepazItems(
    val id: String,
    val originalItem: VanillaItemInst,
    val actions: MutableList<Action>,
    val attributeParts: MutableList<AttributePart>
)

/**
 * DepazItem 实例
 * 完成了 Attribute 处理，运行Kether求值等
 * 可被构建为 [ItemStack]
 * 物品未写入 NBT
 * */

data class DepazItemInst(
    val id: String,
    val item: ItemStack,
    val attributes: MutableList<AttributeInst>
)

data class VanillaItemInst(
    val material: String,
    val name: String?,
    val lore: List<String>,
    val model: Int,
    val nbt: LinkedHashMap<String, Any>
)