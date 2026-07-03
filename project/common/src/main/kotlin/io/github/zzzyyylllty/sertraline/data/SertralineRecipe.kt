package io.github.zzzyyylllty.sertraline.data

/**
 * 配方类型枚举
 */
enum class RecipeType {
    CRAFTING,
    FURNACE,
    BLASTING,
    SMOKING,
    CAMPFIRE,
    STONECUTTING,
    SMITHING_TRANSFORM,
    SMITHING_TRIM
}

/**
 * 配方格式：有序/无序（仅对 CRAFTING 有效）
 */
enum class RecipeFormat {
    SHAPED, SHAPELESS
}

/**
 * 配方提供方式
 */
enum class RecipeProvider {
    SIMPLE,  // Bukkit API
    COMPLEX  // NMS 注入（通过 NMSRecipeFactory 反射操作 RecipeManager）
}

/**
 * 输入组件过滤模式
 */
enum class ComponentsMode {
    REMOVE, KEEP
}

/**
 * 输入选项：控制合成时对输入物品的数据处理
 */
data class InputOptions(
    val dataFilter: List<String> = emptyList(),
    val components: ComponentFilter? = null
)

data class ComponentFilter(
    val mode: ComponentsMode = ComponentsMode.REMOVE,
    val elements: List<String> = emptyList()
)

/**
 * 配方原料
 */
sealed class RecipeIngredient {
    /** 具体物品，如 sertraline:doe_card / minecraft:stone / itemsadder:some_item */
    data class Item(val itemId: String, val amount: Int = 1) : RecipeIngredient()

    /** 标签，如 minecraft:planks / #minecraft:planks */
    data class Tag(val tagId: String, val amount: Int = 1) : RecipeIngredient()

    /** 多选一 */
    data class Choice(val options: List<RecipeIngredient>) : RecipeIngredient()
}

/**
 * 配方结果物品
 */
data class RecipeResult(
    val itemId: String,
    val count: Int = 1,
    val functions: List<RecipeFunction> = emptyList()
)

/**
 * 结果函数（合成后执行）
 */
sealed class RecipeFunction {
    data class Kether(val script: String) : RecipeFunction()
    data class JavaScript(val script: String) : RecipeFunction()
    data class Command(val command: String) : RecipeFunction()
}

/**
 * 配方数据
 */
sealed class RecipeData {
    abstract val id: String
    abstract val type: RecipeType
    abstract val provider: RecipeProvider
    abstract val result: RecipeResult
    abstract val inputOptions: InputOptions

    data class Shaped(
        override val id: String,
        override val result: RecipeResult,
        override val inputOptions: InputOptions,
        override val provider: RecipeProvider = RecipeProvider.SIMPLE,
        val pattern: List<String>,
        val key: Map<Char, RecipeIngredient>,
        val showNotification: Boolean = true,
        val group: String? = null
    ) : RecipeData() {
        override val type: RecipeType = RecipeType.CRAFTING
    }

    data class Shapeless(
        override val id: String,
        override val result: RecipeResult,
        override val inputOptions: InputOptions,
        override val provider: RecipeProvider = RecipeProvider.SIMPLE,
        val ingredients: List<RecipeIngredient>,
        val showNotification: Boolean = true,
        val group: String? = null
    ) : RecipeData() {
        override val type: RecipeType = RecipeType.CRAFTING
    }

    data class Cooking(
        override val id: String,
        override val result: RecipeResult,
        override val inputOptions: InputOptions,
        override val type: RecipeType,
        override val provider: RecipeProvider = RecipeProvider.SIMPLE,
        val ingredient: RecipeIngredient,
        val experience: Float = 0.1f,
        val cookingTime: Int = 200,
        val group: String? = null
    ) : RecipeData()

    data class Stonecutting(
        override val id: String,
        override val result: RecipeResult,
        override val inputOptions: InputOptions,
        override val provider: RecipeProvider = RecipeProvider.SIMPLE,
        val ingredient: RecipeIngredient,
        val group: String? = null
    ) : RecipeData() {
        override val type: RecipeType = RecipeType.STONECUTTING
    }

    data class Smithing(
        override val id: String,
        override val result: RecipeResult,
        override val inputOptions: InputOptions,
        override val type: RecipeType,
        override val provider: RecipeProvider = RecipeProvider.SIMPLE,
        val template: RecipeIngredient,
        val base: RecipeIngredient,
        val addition: RecipeIngredient,
        val group: String? = null
    ) : RecipeData()
}
