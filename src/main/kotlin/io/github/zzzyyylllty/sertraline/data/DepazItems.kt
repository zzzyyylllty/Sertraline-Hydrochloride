package io.github.zzzyyylllty.sertraline.data

import io.github.zzzyyylllty.sertraline.function.sertralize.AnySerializer
import io.github.zzzyyylllty.sertraline.function.sertralize.ItemStackSerializer
import kotlinx.serialization.Serializable
import org.bukkit.inventory.ItemStack

@Serializable
data class DepazItems(
    val id: String,
    val originalItem: VanillaItemInst,
    val actions: MutableList<Action>,
    val attributeParts: MutableList<AttributePart>,
    val data: LinkedHashMap<String, @Serializable(with = AnySerializer::class) Any> //= LinkedHashMap(),
)

/**
 * DepazItem 实例
 * 完成了 Attribute 处理，运行Kether求值等
 * 可被构建为 [ItemStack]
 * 物品未写入 NBT
 * */

@Serializable
data class DepazItemInst(
    val id: String,
    val item: @Serializable(with = ItemStackSerializer::class) ItemStack,
    val attributes: MutableList<AttributeInst>,
    val data: LinkedHashMap<String, @Serializable(with = AnySerializer::class) Any> //= LinkedHashMap(),
)
@Serializable
data class DepazItemUnsolvedInst(
    val id: String,
    val item: @Serializable(with = ItemStackSerializer::class) ItemStack,
    val attributes: MutableList<AttributeInst>,
    val data: LinkedHashMap<String, @Serializable(with = AnySerializer::class) Any> //= LinkedHashMap(),
)

@Serializable
data class VanillaItemInst(
    val material: String,
    val name: String?,
    val lore: List<String>,
    val model: Int,
    val nbt: List<LinkedHashMap<String, @Serializable(with = AnySerializer::class) Any>>,
    val materialLoreEnabled: Boolean,
)