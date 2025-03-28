package io.github.zzzyyylllty.sertraline.data

import org.bukkit.inventory.ItemStack

data class DepazItems(
    val id: String,
    val originalItem: ItemStack,
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
