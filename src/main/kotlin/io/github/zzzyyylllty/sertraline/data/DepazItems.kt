package io.github.zzzyyylllty.sertraline.data

import io.github.zzzyyylllty.sertraline.function.sertralize.AnySerializer
import kotlinx.serialization.Serializable
import org.bukkit.inventory.ItemStack

@Serializable
data class DepazItems(
    val id: String,
    val originalItem: VanillaItemInst,
    val actions: MutableList<Action>,
    val attributeParts: MutableList<AttributePart>,
    val data: LinkedHashMap<String, @Serializable(AnySerializer::class) Any>,
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
    val attributes: MutableList<AttributeInst>,
    val data: LinkedHashMap<String, @Serializable(AnySerializer::class) Any>,
)

@Serializable
data class VanillaItemInst(
    val material: String,
    val name: String? = material,
    val lore: List<String>,
    val model: Int,
    val nbt: List<java.util.LinkedHashMap<String, @Serializable(AnySerializer::class) Any>>,
    val materialLoreEnabled: Boolean,
)
@Serializable
data class Category(
    val icon: DepazItems,
    val name: String,
    val lore: List<String>
)

@Serializable
sealed class DataValue {
    @Serializable data class Text(val content: String) : DataValue()
    @Serializable data class Double(val value: Double) : DataValue()
    @Serializable data class Int(val value: Int) : DataValue()
    @Serializable data class Long(val value: Long) : DataValue()
}