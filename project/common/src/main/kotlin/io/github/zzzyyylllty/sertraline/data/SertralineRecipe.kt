package io.github.zzzyyylllty.sertraline.data

import org.bukkit.inventory.ItemStack

/**
 * 配方类型枚举
 */
enum class RecipeType {
    SHAPED,           // 有序合成
    SHAPELESS,        // 无序合成
    SMELTING,         // 熔炉
    BLASTING,         // 高炉
    SMOKING,          // 烟熏
    CAMPFIRE_COOKING, // 营火烹饪
    STONECUTTING,     // 切石
    SMITHING_TRANSFORM, // 锻造台-转换
    SMITHING_TRIM     // 锻造台-装饰
}

/**
 * 配方原料
 * 支持具体物品ID或标签
 */
sealed class RecipeIngredient {
    /**
     * 具体物品
     * @param itemId 物品ID，如 "minecraft:oak_planks"
     */
    data class Item(val itemId: String) : RecipeIngredient()

    /**
     * 标签
     * @param tagId 标签ID，如 "minecraft:planks"（不需要#前缀）
     */
    data class Tag(val tagId: String) : RecipeIngredient()

    /**
     * 多个选项（或关系）
     * @param options 多个原料选项
     */
    data class Choice(val options: List<RecipeIngredient>) : RecipeIngredient()
}

/**
 * 配方数据类
 */
sealed class RecipeData {
    abstract val id: String  // 配方ID，如 "myplugin:custom_recipe"
    abstract val result: RecipeResult

    /**
     * 有序合成配方
     * @param pattern 3x3 合成表图案，使用字符表示，如 listOf("AAA", "ABA", "AAA")
     * @param key 字符到原料的映射，如 mapOf('A' to ..., 'B' to ...)
     */
    data class Shaped(
        override val id: String,
        override val result: RecipeResult,
        val pattern: List<String>,  // 最多3行，每行最多3个字符
        val key: Map<Char, RecipeIngredient>,
        val showNotification: Boolean = true
    ) : RecipeData()

    /**
     * 无序合成配方
     * @param ingredients 原料列表
     */
    data class Shapeless(
        override val id: String,
        override val result: RecipeResult,
        val ingredients: List<RecipeIngredient>,
        val showNotification: Boolean = true
    ) : RecipeData()

    /**
     * 熔炉类配方
     * @param ingredient 输入原料
     * @param experience 经验值
     * @param cookingTime 烹饪时间（tick）
     */
    data class Cooking(
        override val id: String,
        override val result: RecipeResult,
        val type: RecipeType, // SMELTING, BLASTING, SMOKING, CAMPFIRE_COOKING
        val ingredient: RecipeIngredient,
        val experience: Float = 0.1f,
        val cookingTime: Int = 200
    ) : RecipeData()

    /**
     * 切石配方
     */
    data class Stonecutting(
        override val id: String,
        override val result: RecipeResult,
        val ingredient: RecipeIngredient
    ) : RecipeData()

    /**
     * 锻造台配方
     */
    data class Smithing(
        override val id: String,
        override val result: RecipeResult,
        val type: RecipeType, // SMITHING_TRANSFORM, SMITHING_TRIM
        val template: RecipeIngredient,
        val base: RecipeIngredient,
        val addition: RecipeIngredient
    ) : RecipeData()
}

/**
 * 配方结果
 */
data class RecipeResult(
    val itemId: String,  // 物品ID
    val count: Int = 1   // 数量
)
