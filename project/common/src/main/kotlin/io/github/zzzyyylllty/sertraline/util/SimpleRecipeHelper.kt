package io.github.zzzyyylllty.sertraline.util

import io.github.zzzyyylllty.sertraline.data.*
import io.github.zzzyyylllty.sertraline.item.sertralineItemBuilder
import io.github.zzzyyylllty.sertraline.logger.infoS
import io.github.zzzyyylllty.sertraline.Sertraline
import io.github.zzzyyylllty.sertraline.logger.severeS
import io.github.zzzyyylllty.sertraline.logger.warningS
import io.github.zzzyyylllty.sertraline.recipe.ItemResolver
import io.github.zzzyyylllty.sertraline.recipe.NMSRecipeFactory
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.*
import org.bukkit.inventory.recipe.CraftingBookCategory
import java.util.concurrent.Callable
import java.util.concurrent.ConcurrentHashMap

/**
 * Sertraline 配方管理器。
 * 使用 Bukkit API 注册/注销配方，取代原有的 NMS 反射方案。
 */
object SertralineRecipeManager {

    private val NAMESPACE = "sertraline"

    /** 已注册的 Sertraline 配方 key → RecipeData */
    private val registeredRecipes = ConcurrentHashMap<NamespacedKey, RecipeData>()

    /** Bukkit 插件实例，用于主线程调度 */
    private val bukkitPlugin: org.bukkit.plugin.Plugin by lazy {
        Bukkit.getPluginManager().getPlugin("Sertraline")
            ?: error("Sertraline Bukkit plugin not found")
    }

    /** 将操作切换到主线程执行（阻塞调用线程直到完成） */
    private fun <T> sync(callable: () -> T): T {
        if (Bukkit.isPrimaryThread()) return callable()
        return Bukkit.getScheduler().callSyncMethod(bukkitPlugin, Callable(callable)).get()
    }

    // ==================== 生命周期 ====================

    fun reload() {
        infoS("<green>Loaded recipes successfully.")
    }

    /**
     * 注销所有 Sertraline 配方。
     */
    fun unregisterAll() {
        registeredRecipes.forEach { (key, recipeData) ->
            runCatching {
                sync {
                    when (recipeData.provider) {
                        RecipeProvider.SIMPLE -> Bukkit.removeRecipe(key)
                        RecipeProvider.COMPLEX -> NMSRecipeFactory.unregister(key.namespace, key.key)
                    }
                }
            }.onFailure { e ->
                warningS("Failed to unregister recipe $key: ${e.message}")
            }
        }
        registeredRecipes.clear()
    }

    // ==================== 注册 ====================

    /**
     * 注册一个配方到 Bukkit（SIMPLE）或 NMS RecipeManager（COMPLEX）。
     * @return true 如果注册成功
     */
    fun registerRecipe(recipe: RecipeData): Boolean {
        return runCatching {
            val rawPath = recipe.id.removePrefix("$NAMESPACE:")
            val path = rawPath.lowercase().replace(Regex("[^a-z0-9/._-]"), "_")
            val key = NamespacedKey(NAMESPACE, path)

            when (recipe.provider) {
                RecipeProvider.SIMPLE -> {
                    val bukkitRecipe = when (recipe) {
                        is RecipeData.Shaped -> buildShaped(key, recipe)
                        is RecipeData.Shapeless -> buildShapeless(key, recipe)
                        is RecipeData.Cooking -> buildCooking(key, recipe)
                        is RecipeData.Stonecutting -> buildStonecutting(key, recipe)
                        is RecipeData.Smithing -> buildSmithing(key, recipe)
                    } ?: return false

                    val added = sync { Bukkit.addRecipe(bukkitRecipe) }
                    if (added) registeredRecipes[key] = recipe
                    added
                }
                RecipeProvider.COMPLEX -> {
                    val added = sync { NMSRecipeFactory.register(recipe) }
                    if (added) registeredRecipes[key] = recipe
                    added
                }
            }
        }.onFailure { e ->
            severeS("Failed to register recipe ${recipe.id}: ${e.message}")
        }.getOrDefault(false)
    }

    /**
     * 根据 NamespacedKey 获取 RecipeData。
     */
    fun getRecipeData(key: NamespacedKey): RecipeData? = registeredRecipes[key]

    /**
     * 获取所有已注册的配方数据。
     */
    fun getAllRecipeData(): Collection<RecipeData> = registeredRecipes.values

    // ==================== Builder: Shaped ====================

    private fun buildShaped(key: NamespacedKey, recipe: RecipeData.Shaped): ShapedRecipe? {
        val result = buildResultStack(recipe.result) ?: return null
        val bukkit = ShapedRecipe(key, result)
        bukkit.shape(*recipe.pattern.toTypedArray())

        recipe.key.forEach { (char, ingredient) ->
            val choice = ItemResolver.resolve(ingredient)
            bukkit.setIngredient(char, choice)
        }

        recipe.group?.let { bukkit.setGroup(it) }
        bukkit.setCategory(CraftingBookCategory.MISC)
        trySetShowNotification(bukkit, recipe.showNotification)
        return bukkit
    }

    // ==================== Builder: Shapeless ====================

    private fun buildShapeless(key: NamespacedKey, recipe: RecipeData.Shapeless): ShapelessRecipe? {
        val result = buildResultStack(recipe.result) ?: return null
        val bukkit = ShapelessRecipe(key, result)

        recipe.ingredients.forEach { ingredient ->
            val choice = ItemResolver.resolve(ingredient)
            bukkit.addIngredient(choice)
        }

        recipe.group?.let { bukkit.setGroup(it) }
        bukkit.setCategory(CraftingBookCategory.MISC)
        trySetShowNotification(bukkit, recipe.showNotification)
        return bukkit
    }

    // ==================== Builder: Cooking ====================

    @Suppress("SpellCheckingInspection")
    private fun buildCooking(key: NamespacedKey, recipe: RecipeData.Cooking): CookingRecipe<*>? {
        val result = buildResultStack(recipe.result) ?: return null
        val choice = ItemResolver.resolve(recipe.ingredient)

        val bukkit: CookingRecipe<*> = when (recipe.type) {
            RecipeType.FURNACE -> FurnaceRecipe(key, result, choice, recipe.experience, recipe.cookingTime)
            RecipeType.BLASTING -> BlastingRecipe(key, result, choice, recipe.experience, recipe.cookingTime)
            RecipeType.SMOKING -> SmokingRecipe(key, result, choice, recipe.experience, recipe.cookingTime)
            RecipeType.CAMPFIRE -> CampfireRecipe(key, result, choice, recipe.experience, recipe.cookingTime)
            else -> return null
        }
        recipe.group?.let { bukkit.setGroup(it) }
        return bukkit
    }

    // ==================== Builder: Stonecutting ====================

    private fun buildStonecutting(key: NamespacedKey, recipe: RecipeData.Stonecutting): StonecuttingRecipe? {
        val result = buildResultStack(recipe.result) ?: return null
        val choice = ItemResolver.resolve(recipe.ingredient)
        val bukkit = StonecuttingRecipe(key, result, choice)
        recipe.group?.let { bukkit.setGroup(it) }
        return bukkit
    }

    // ==================== Builder: Smithing ====================

    private fun buildSmithing(key: NamespacedKey, recipe: RecipeData.Smithing): SmithingRecipe? {
        val result = buildResultStack(recipe.result) ?: return null
        val template = ItemResolver.resolve(recipe.template)
        val base = ItemResolver.resolve(recipe.base)
        val addition = ItemResolver.resolve(recipe.addition)

        val bukkit = when (recipe.type) {
            RecipeType.SMITHING_TRANSFORM -> SmithingTransformRecipe(key, result, template, base, addition)
            RecipeType.SMITHING_TRIM -> SmithingTrimRecipe(key, template, base, addition)
            else -> return null
        }
        return bukkit
    }

    // ==================== Result Builder ====================

    private fun buildResultStack(result: RecipeResult): ItemStack? {
        val (namespace, key) = parseId(result.itemId)
        return when {
            namespace == "sertraline" -> {
                val item = Sertraline.itemMap[key]
                if (item == null) {
                    warningS("Recipe result item 'sertraline:$key' not found in itemMap (missing item definition?)")
                    return null
                }
                sertralineItemBuilder(key, null)?.apply { amount = result.count }
            }
            namespace == "minecraft" || namespace == "vanilla" -> {
                val mat = Material.matchMaterial(key)
                if (mat == null) {
                    warningS("Recipe result material '$key' is not a valid Minecraft material")
                    return null
                }
                ItemStack(mat, result.count)
            }
            else -> {
                val stack = ExternalItemHelper.buildNoPlayer(namespace, key)
                if (stack == null) {
                    warningS("Recipe result external item '$namespace:$key' not found via ExternalItemHelper")
                    return null
                }
                stack.apply { amount = result.count }
            }
        }
    }

    private fun parseId(id: String): Pair<String, String> {
        val idx = id.indexOf(':')
        return if (idx == -1) "minecraft" to id.lowercase()
        else id.substring(0, idx).lowercase() to id.substring(idx + 1)
    }

    /**
     * Try to call setShowNotification via reflection (Paper API may not have it in some builds).
     */
    private fun trySetShowNotification(recipe: Any, show: Boolean) {
        runCatching {
            recipe::class.java.getMethod("setShowNotification", Boolean::class.javaPrimitiveType).invoke(recipe, show)
        }
    }
}
