package io.github.zzzyyylllty.sertraline.util

import com.github.retrooper.packetevents.protocol.recipe.Ingredient
import com.willfp.eco.core.items.CustomItem
import io.github.zzzyyylllty.sertraline.data.*
import org.bukkit.Tag
import org.bukkit.inventory.ItemStack
import com.cryptomorin.xseries.XItemStack
import top.maplex.arim.tools.itemmanager.ItemManager


val temporarySItemCache = mutableMapOf<SertralineRecipeFilter, ModernSItem>()

data class SertralineRecipeFilter(
    val filter: List<String>,
    val sItem: ModernSItem,
)

/**
 * 配方管理器
 * 负责注册和注销配方
 */
object RecipeManager {

    // ===== 反射缓存 =====

    // 核心类
    private val resourceLocationClass by lazy {
        getClazz(assembleMCClass("resources.ResourceLocation"))!!
    }
    private val resourceKeyClass by lazy {
        getClazz(assembleMCClass("resources.ResourceKey"))!!
    }
    private val recipeManagerClass by lazy {
        getClazz(assembleMCClass("world.item.crafting.RecipeManager"))!!
    }
    private val recipeHolderClass by lazy {
        getClazz(assembleMCClass("world.item.crafting.RecipeHolder"))!!
    }
    private val recipeMapClass by lazy {
        getClazz(assembleMCClass("world.item.crafting.RecipeMap"))!!
    }
    private val ingredientClass by lazy {
        getClazz(assembleMCClass("world.item.crafting.Ingredient"))!!
    }
    private val itemStackClass by lazy {
        getClazz(assembleMCClass("world.item.ItemStack"))!!
    }
    private val shapedRecipeClass by lazy {
        getClazz(assembleMCClass("world.item.crafting.ShapedRecipe"))!!
    }
    private val shapelessRecipeClass by lazy {
        getClazz(assembleMCClass("world.item.crafting.ShapelessRecipe"))!!
    }
    private val minecraftServerClass by lazy {
        getClazz(assembleMCClass("server.MinecraftServer"))!!
    }
    private val registriesClass by lazy {
        getClazz(assembleMCClass("core.registries.Registries"))!!
    }
    private val tagKeyClass by lazy {
        getClazz(assembleMCClass("tags.TagKey"))!!
    }
    private val craftingBookCategoryClass by lazy {
        getClazz(assembleMCClass("world.item.crafting.CraftingBookCategory"))!!
    }

    // CraftBukkit 类
    private val craftServerClass by lazy {
        getClazz(assembleCBClass("CraftServer"))!!
    }
    private val craftItemStackClass by lazy {
        getClazz(assembleCBClass("inventory.CraftItemStack"))!!
    }

    // 方法缓存
    private val resourceLocationFromNamespaceAndPath by lazy {
        getStaticMethod(
            resourceLocationClass,
            resourceLocationClass,
            String::class.java,
            String::class.java
        )!!
    }

    private val resourceKeyCreate by lazy {
        getStaticMethod(
            resourceKeyClass,
            resourceKeyClass,
            resourceKeyClass,
            resourceLocationClass
        )!!
    }

    private val minecraftServerGetServer by lazy {
        getStaticMethod(
            minecraftServerClass,
            minecraftServerClass
        )!!
    }

    private val minecraftServerGetRecipeManager by lazy {
        getMethod(
            minecraftServerClass,
            recipeManagerClass,
            0
        )!!
    }

    private val recipeManagerAddRecipe by lazy {
        getMethod(
            recipeManagerClass,
            Void.TYPE,
            0,
            recipeHolderClass
        )!!
    }

    private val recipeMapRemoveRecipe by lazy {
        getMethod(
            recipeMapClass,
            Boolean::class.javaPrimitiveType!!,
            0,
            resourceKeyClass
        )!!
    }

    private val recipeHolderConstructor by lazy {
        recipeHolderClass.getDeclaredConstructor(
            resourceKeyClass,
            Any::class.java  // Recipe
        ).apply { isAccessible = true }
    }

    private val craftItemStackAsNMSCopy by lazy {
        getStaticMethod(
            craftItemStackClass,
            itemStackClass,
            org.bukkit.inventory.ItemStack::class.java
        )!!
    }

    private val craftItemStackAsBukkitCopy by lazy {
        getStaticMethod(
            craftItemStackClass,
            org.bukkit.inventory.ItemStack::class.java,
            itemStackClass
        )!!
    }

    // 字段缓存
    private val recipeManagerRecipesField by lazy {
        getDeclaredField(recipeManagerClass, recipeMapClass, 0)!!
    }

    private val ingredientItemStacksField by lazy {
        // 1.21.4+ 使用 Set<ItemStack>
        getDeclaredField(ingredientClass, Set::class.java, 0)!!
    }

    private val registriesRecipeField by lazy {
        getDeclaredField(registriesClass, "RECIPE")!!.apply {
            isAccessible = true
        }
    }

    private val craftingBookCategoryMisc by lazy {
        craftingBookCategoryClass.enumConstants.first {
            it.toString() == "MISC"
        }
    }

    // ===== 已注册配方跟踪 =====
    private val registeredRecipes = mutableSetOf<String>()

    /**
     * 注册配方（必须在主线程调用）
     */
    fun registerRecipe(recipe: RecipeData): Boolean {
        return runCatching {
            if (recipe.id in registeredRecipes) {
                unregisterRecipe(recipe.id)
            }

            val nmsRecipe = createNMSRecipe(recipe)
            val recipeKey = createRecipeResourceKey(recipe.id)
            val recipeHolder = recipeHolderConstructor.newInstance(recipeKey, nmsRecipe)

            val recipeManager = getRecipeManager()
            recipeManagerAddRecipe.invoke(recipeManager, recipeHolder)

            registeredRecipes.add(recipe.id)
            true
        }.getOrElse { e ->
            e.printStackTrace()
            false
        }
    }

    /**
     * 注销配方（必须在主线程调用）
     */
    fun unregisterRecipe(recipeId: String): Boolean {
        return runCatching {
            val recipeKey = createRecipeResourceKey(recipeId)
            val recipeManager = getRecipeManager()
            val recipeMap = recipeManagerRecipesField.get(recipeManager)

            val removed = recipeMapRemoveRecipe.invoke(recipeMap, recipeKey) as Boolean

            if (removed) {
                registeredRecipes.remove(recipeId)
            }
            removed
        }.getOrElse { e ->
            e.printStackTrace()
            false
        }
    }

    /**
     * 获取所有已注册的配方ID
     */
    fun getRegisteredRecipes(): Set<String> = registeredRecipes.toSet()

    // ===== 内部辅助方法 =====

    private fun getRecipeManager(): Any {
        val server = minecraftServerGetServer.invoke(null)
        return minecraftServerGetRecipeManager.invoke(server)
    }

    private fun createResourceLocation(id: String): Any {
        val parts = id.split(":", limit = 2)
        val namespace = if (parts.size == 2) parts[0] else "minecraft"
        val path = if (parts.size == 2) parts[1] else parts[0]
        return resourceLocationFromNamespaceAndPath.invoke(null, namespace, path)
    }

    private fun createRecipeResourceKey(recipeId: String): Any {
        val recipeRegistry = registriesRecipeField.get(null)
        val resourceLocation = createResourceLocation(recipeId)
        return resourceKeyCreate.invoke(null, recipeRegistry, resourceLocation)
    }

    /**
     * 创建 NMS Ingredient
     */
    private fun createIngredient(ingredient: RecipeIngredient): Any {
        // 获取所有匹配的物品堆
        val itemStacks = resolveIngredientItems(ingredient)

        // 创建空的 Ingredient（通过反射调用静态方法或构造函数）
        val nmsItemStacks = itemStacks.map {
            craftItemStackAsNMSCopy.invoke(null, it)
        }.toSet()

        // 使用反射创建 Ingredient 并设置物品列表
        val ingredientInstance = createEmptyIngredient()
        ingredientItemStacksField.set(ingredientInstance, nmsItemStacks)

        return ingredientInstance
    }

    private fun createEmptyIngredient(): Any {
        // 1.21.4+ 可以使用 Ingredient.of() 或构造函数
        val ofMethod = ingredientClass.getDeclaredMethod("of")
        ofMethod.isAccessible = true
        return ofMethod.invoke(null)
    }

    /**
     * 解析配方原料为 Bukkit ItemStack 列表
     */
    private fun resolveIngredientItems(ingredient: RecipeIngredient): List<ItemStack> {
        return when (ingredient) {
            is RecipeIngredient.Item -> {
                // 具体物品
                val material = org.bukkit.Material.matchMaterial(ingredient.itemId.uppercase())
                    ?: throw IllegalArgumentException("Unknown item: ${ingredient.itemId}")
                listOf(org.bukkit.inventory.ItemStack(material))
            }
            is RecipeIngredient.Tag -> {
                // 标签 - 获取标签下的所有物品
                val tag = mapOf<String, ItemStack>()
//                val tag = org.bukkit.Registry<Tag>.getTag(
//                    org.bukkit.NamespacedKey.fromString(ingredient.tagId)
//                        ?: throw IllegalArgumentException("Invalid tag: ${ingredient.tagId}")
//                ) ?: throw IllegalArgumentException("Unknown tag: ${ingredient.tagId}")

                tag.values.map { ItemStack(it) }
            }
            is RecipeIngredient.Choice -> {
                // 多个选项
                ingredient.options.flatMap { resolveIngredientItems(it) }
            }
        }
    }

    /**
     * 创建 NMS 配方对象
     */
    private fun createNMSRecipe(recipe: RecipeData): Any {
        return when (recipe) {
            is RecipeData.Shaped -> createShapedRecipe(recipe)
            is RecipeData.Shapeless -> createShapelessRecipe(recipe)
            is RecipeData.Cooking -> createCookingRecipe(recipe)
            is RecipeData.Stonecutting -> createStonecuttingRecipe(recipe)
            is RecipeData.Smithing -> createSmithingRecipe(recipe)
        }
    }

    private fun createShapedRecipe(recipe: RecipeData.Shaped): Any {
        // 这里需要根据实际的 ShapedRecipe 构造函数来实现
        // 1.21.4+ 的构造函数签名需要查看具体版本
        // 通常包括：分组、图案、原料映射、结果等

        val result = createResultItemStack(recipe.result)
        val ingredients = recipe.key.mapValues { createIngredient(it.value) }

        // 根据实际构造函数参数调整
        val constructor = shapedRecipeClass.constructors.first()
        constructor.isAccessible = true

        // 这里需要根据实际参数构造
        // 示例（需要根据实际情况调整）：
        return constructor.newInstance(
            craftingBookCategoryMisc,
            // ... 其他参数
        )
    }

    private fun createShapelessRecipe(recipe: RecipeData.Shapeless): Any {
        val result = createResultItemStack(recipe.result)
        val ingredients = recipe.ingredients.map { createIngredient(it) }

        val constructor = shapelessRecipeClass.constructors.first()
        constructor.isAccessible = true

        return constructor.newInstance(
            craftingBookCategoryMisc,
            // ... 其他参数
        )
    }

    private fun createCookingRecipe(recipe: RecipeData.Cooking): Any {
        // 根据 type 选择对应的配方类
        val recipeClass = when (recipe.type) {
            RecipeType.SMELTING -> getClazz(assembleMCClass("world.item.crafting.SmeltingRecipe"))
            RecipeType.BLASTING -> getClazz(assembleMCClass("world.item.crafting.BlastingRecipe"))
            RecipeType.SMOKING -> getClazz(assembleMCClass("world.item.crafting.SmokingRecipe"))
            RecipeType.CAMPFIRE_COOKING -> getClazz(assembleMCClass("world.item.crafting.CampfireCookingRecipe"))
            else -> throw IllegalArgumentException("Invalid cooking recipe type: ${recipe.type}")
        }!!

        val ingredient = createIngredient(recipe.ingredient)
        val result = createResultItemStack(recipe.result)

        val constructor = recipeClass.constructors.first()
        constructor.isAccessible = true

        return constructor.newInstance(
            // ... 参数
        )
    }

    private fun createStonecuttingRecipe(recipe: RecipeData.Stonecutting): Any {
        val stonecuttingRecipeClass = getClazz(assembleMCClass("world.item.crafting.StonecutterRecipe"))!!
        val ingredient = createIngredient(recipe.ingredient)
        val result = createResultItemStack(recipe.result)

        val constructor = stonecuttingRecipeClass.constructors.first()
        constructor.isAccessible = true

        return constructor.newInstance(
            // ... 参数
        )
    }

    private fun createSmithingRecipe(recipe: RecipeData.Smithing): Any {
        val recipeClass = when (recipe.type) {
            RecipeType.SMITHING_TRANSFORM -> getClazz(assembleMCClass("world.item.crafting.SmithingTransformRecipe"))
            RecipeType.SMITHING_TRIM -> getClazz(assembleMCClass("world.item.crafting.SmithingTrimRecipe"))
            else -> throw IllegalArgumentException("Invalid smithing recipe type: ${recipe.type}")
        }!!

        val template = createIngredient(recipe.template)
        val base = createIngredient(recipe.base)
        val addition = createIngredient(recipe.addition)
        val result = createResultItemStack(recipe.result)

        val constructor = recipeClass.constructors.first()
        constructor.isAccessible = true

        return constructor.newInstance(
            // ... 参数
        )
    }

    private fun createResultItemStack(result: RecipeResult): Any {
        val material = org.bukkit.Material.matchMaterial(result.itemId.uppercase())
            ?: throw IllegalArgumentException("Unknown item: ${result.itemId}")
        val bukkitStack = org.bukkit.inventory.ItemStack(material, result.count)
        return craftItemStackAsNMSCopy.invoke(null, bukkitStack)
    }
//
//
//    fun <T> toIngredient(items: MutableList<String>): Ingredient<T>? {
//        val itemIds: MutableSet<UniqueKey> = HashSet<UniqueKey>()
//        val minecraftItemIds: MutableSet<UniqueKey?> = HashSet<UniqueKey?>()
//        val itemManager: ItemManager<T?> = CraftEngine.instance().itemManager()
//        val elements: MutableList<IngredientElement?> = ArrayList<IngredientElement?>()
//        for (item in items) {
//            if (item.get(0) == '#') {
//                val tag: Key? = Key.of(item.substring(1))
//                elements.add(Tag(tag))
//                val uniqueKeys: MutableList<UniqueKey> = itemManager.itemIdsByTag(tag)
//                if (uniqueKeys.isEmpty()) {
//                    throw LocalizedResourceConfigException("warning.config.recipe.invalid_ingredient", item)
//                }
//                itemIds.addAll(uniqueKeys)
//                for (uniqueKey in uniqueKeys) {
//                    val ingredientSubstitutes: MutableList<UniqueKey> =
//                        itemManager.getIngredientSubstitutes(uniqueKey.key())
//                    if (!ingredientSubstitutes.isEmpty()) {
//                        itemIds.addAll(ingredientSubstitutes)
//                    }
//                }
//            } else {
//                val itemId: Key? = Key.of(item)
//                elements.add(Item(itemId))
//                if (itemManager.getBuildableItem(itemId).isEmpty()) {
//                    throw LocalizedResourceConfigException("warning.config.recipe.invalid_ingredient", item)
//                }
//                itemIds.add(UniqueKey.create(itemId))
//                val ingredientSubstitutes: MutableList<UniqueKey> = itemManager.getIngredientSubstitutes(itemId)
//                if (!ingredientSubstitutes.isEmpty()) {
//                    itemIds.addAll(ingredientSubstitutes)
//                }
//            }
//        }
//        var hasCustomItem = false
//        for (holder in itemIds) {
//            val optionalCustomItem: Optional<CustomItem<T?>?> = itemManager.getCustomItem(holder.key())
//            val vanillaItem: UniqueKey?
//            if (optionalCustomItem.isPresent()) {
//                val customItem: CustomItem<T?> = optionalCustomItem.get()
//                if (customItem.isVanillaItem()) {
//                    vanillaItem = holder
//                } else {
//                    vanillaItem = UniqueKey.create(customItem.material())
//                    hasCustomItem = true
//                }
//            } else {
//                if (itemManager.isVanillaItem(holder.key())) {
//                    vanillaItem = holder
//                } else {
//                    throw LocalizedResourceConfigException(
//                        "warning.config.recipe.invalid_ingredient",
//                        holder.key().asString()
//                    )
//                }
//            }
//            if (vanillaItem === UniqueKey.AIR) {
//                throw LocalizedResourceConfigException(
//                    "warning.config.recipe.invalid_ingredient",
//                    holder.key().asString()
//                )
//            }
//            minecraftItemIds.add(vanillaItem)
//        }
//        return if (itemIds.isEmpty()) null else Ingredient.of(elements, itemIds, minecraftItemIds, hasCustomItem)
//    }
}