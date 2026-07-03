package io.github.zzzyyylllty.sertraline.recipe

import io.github.zzzyyylllty.sertraline.Sertraline
import io.github.zzzyyylllty.sertraline.data.*
import io.github.zzzyyylllty.sertraline.item.sertralineItemBuilder
import io.github.zzzyyylllty.sertraline.logger.severeS
import io.github.zzzyyylllty.sertraline.logger.warningS
import io.github.zzzyyylllty.sertraline.util.ExternalItemHelper
import io.github.zzzyyylllty.sertraline.util.assembleMCClass
import io.github.zzzyyylllty.sertraline.util.getClazz
import io.github.zzzyyylllty.sertraline.util.getDeclaredField
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.Tag
import java.lang.reflect.Field

/**
 * NMS 配方工厂。
 * 自动检测 Minecraft 版本（1.21.4 / 26.1.2），
 * 使用反射操作 RecipeManager，支持双版本构造签名。
 */
object NMSRecipeFactory {

    private const val NAMESPACE = "sertraline"

    // ==================== 版本检测 ====================

    enum class MinecraftVersion { v1_21_4, v26_1_2 }

    /** 通过检测 Identifier 类是否存在来判断版本 */
    private val minecraftVersion: MinecraftVersion by lazy {
        if (runCatching { Class.forName("net.minecraft.resources.Identifier") }.isSuccess)
            MinecraftVersion.v26_1_2
        else
            MinecraftVersion.v1_21_4
    }

    // ==================== 反射缓存: ResourceLocation (1.21.4) / Identifier (26.1.2) ====================

    private val clazzResourceLocation: Class<*> by lazy {
        when (minecraftVersion) {
            MinecraftVersion.v1_21_4 -> getClazz("net.minecraft.resources.ResourceLocation")
            MinecraftVersion.v26_1_2 -> getClazz("net.minecraft.resources.Identifier")
        }
    }
    private val methodRLFromNamespaceAndPath by lazy {
        clazzResourceLocation.getMethod("fromNamespaceAndPath", String::class.java, String::class.java)
    }

    private fun createResourceLocation(namespace: String, path: String): Any {
        return methodRLFromNamespaceAndPath.invoke(null, namespace, path)
    }

    // ==================== 反射缓存: ResourceKey ====================

    private val clazzResourceKey by lazy { getClazz(assembleMCClass("resources.ResourceKey")) }
    private val methodResourceKeyCreate by lazy {
        clazzResourceKey.getDeclaredMethod("create", clazzResourceKey, clazzResourceLocation)
    }

    // RECIPE 注册表 key — 1.21.4: Registries.RECIPE; 26.1.2: 同
    private val recipeRegistryKey: Any by lazy {
        clazzRegistries.getDeclaredField("RECIPE").apply { isAccessible = true }.get(null)
    }

    private fun createRecipeResourceKey(namespace: String, path: String): Any {
        val location = createResourceLocation(namespace, path)
        return methodResourceKeyCreate.invoke(null, recipeRegistryKey, location)
    }

    // ==================== 反射缓存: ItemStack (NMS) ====================

    private val clazzCraftItemStack by lazy { getClazz("org.bukkit.craftbukkit.inventory.CraftItemStack") }
    private val methodAsNMSCopy by lazy {
        clazzCraftItemStack.getDeclaredMethod("asNMSCopy", org.bukkit.inventory.ItemStack::class.java)
    }
    private val methodAsBukkitCopy by lazy {
        clazzCraftItemStack.getDeclaredMethod("asCraftMirror", getClazz(assembleMCClass("world.item.ItemStack")))
    }

    private fun toNMSStack(bukkit: org.bukkit.inventory.ItemStack): Any {
        return methodAsNMSCopy.invoke(null, bukkit)
    }

    // ==================== 反射缓存: Ingredient ====================

    private val clazzIngredient by lazy { getClazz(assembleMCClass("world.item.crafting.Ingredient")) }
    private val clazzNMSItemStack by lazy { getClazz(assembleMCClass("world.item.ItemStack")) }
    private val clazzNMSItem by lazy { getClazz(assembleMCClass("world.item.Item")) }
    private val clazzItemLike by lazy { getClazz(assembleMCClass("world.level.ItemLike")) }
    private val methodGetItem by lazy {
        clazzNMSItemStack.getDeclaredMethod("getItem")
    }

    // Ingredient.of(ItemLike...) — varargs（1.21.4 和 26.1.2 均如此）
    private val methodIngredientOfStacks by lazy {
        val arrayClass = java.lang.reflect.Array.newInstance(clazzItemLike, 0).javaClass
        clazzIngredient.getDeclaredMethod("of", arrayClass)
    }

    // Ingredient.of(TagKey<Item>)
    private val clazzTagKey by lazy { getClazz("net.minecraft.tags.TagKey") }
    private val clazzRegistries by lazy { getClazz("net.minecraft.core.registries.Registries") }
    private val methodIngredientOfTag by lazy {
        clazzIngredient.getDeclaredMethod("of", clazzTagKey)
    }

    private fun toNMSItems(nmsStacks: List<Any>): List<Any> {
        return nmsStacks.map { methodGetItem.invoke(it) }
    }

    private fun createItemLikeArray(items: List<Any>): Any {
        val array = java.lang.reflect.Array.newInstance(clazzItemLike, items.size)
        items.forEachIndexed { i, item -> java.lang.reflect.Array.set(array, i, item) }
        return array
    }

    /**
     * 创建 NMS Ingredient。
     * - 对 Tag 使用 Ingredient.of(TagKey)
     * - 对具体物品使用 Ingredient.of(ItemLike...)（匹配 Item 类型）
     */
    fun createIngredient(ingredient: RecipeIngredient): Any {
        return when (ingredient) {
            is RecipeIngredient.Item -> {
                val stacks = resolveItemStacks(ingredient.itemId, ingredient.amount)
                if (stacks.isEmpty()) throw IllegalArgumentException("No items resolved for: ${ingredient.itemId}")
                val nmsStacks = stacks.map { toNMSStack(it) }
                val items = toNMSItems(nmsStacks)
                methodIngredientOfStacks.invoke(null, createItemLikeArray(items))
            }
            is RecipeIngredient.Tag -> {
                val tagKey = resolveTagKey(ingredient.tagId)
                methodIngredientOfTag.invoke(null, tagKey)
            }
            is RecipeIngredient.Choice -> {
                val stacks = ingredient.options.flatMap { resolveIngredientStacks(it) }
                if (stacks.isEmpty()) throw IllegalArgumentException("Empty choice ingredient")
                val nmsStacks = stacks.map { toNMSStack(it) }
                val items = toNMSItems(nmsStacks)
                methodIngredientOfStacks.invoke(null, createItemLikeArray(items))
            }
        }
    }

    private fun resolveIngredientStacks(ingredient: RecipeIngredient): List<ItemStack> {
        return when (ingredient) {
            is RecipeIngredient.Item -> resolveItemStacks(ingredient.itemId, ingredient.amount)
            is RecipeIngredient.Tag -> resolveTagStacks(ingredient.tagId, ingredient.amount)
            is RecipeIngredient.Choice -> ingredient.options.flatMap { resolveIngredientStacks(it) }
        }
    }

    private fun resolveItemStacks(itemId: String, amount: Int): List<ItemStack> {
        val (namespace, key) = parseId(itemId)
        return when {
            namespace == "sertraline" -> {
                val sItem = Sertraline.itemMap[key] ?: return emptyList()
                val stack = sertralineItemBuilder(key, null) ?: return emptyList()
                stack.amount = amount
                listOf(stack)
            }
            namespace == "minecraft" || namespace == "vanilla" -> {
                val mat = Material.matchMaterial(key) ?: return emptyList()
                listOf(ItemStack(mat, amount))
            }
            else -> {
                val stack = ExternalItemHelper.buildNoPlayer(namespace, key) ?: return emptyList()
                stack.amount = amount
                listOf(stack)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun resolveTagKey(tagId: String): Any {
        val normalized = normalizeTagKey(tagId)
        val (namespace, path) = normalized.split(":", limit = 2)
        val location = createResourceLocation(namespace, path)
        // TagKey.create(Registries.ITEM, location/identifier)
        val methodTagKeyCreate = clazzTagKey.getDeclaredMethod("create", clazzResourceKey, clazzResourceLocation)
        methodTagKeyCreate.isAccessible = true

        val itemRegistryKey = clazzRegistries.getDeclaredField("ITEM").apply { isAccessible = true }.get(null)

        return methodTagKeyCreate.invoke(null, itemRegistryKey, location)
    }

    private fun resolveTagStacks(tagId: String, amount: Int): List<ItemStack> {
        val normalized = normalizeTagKey(tagId)
        val bukkitTag = resolveBukkitTag(normalized) ?: return emptyList()
        return bukkitTag.values.map { ItemStack(it, amount) }
    }

    private fun parseId(id: String): Pair<String, String> {
        val idx = id.indexOf(':')
        return if (idx == -1) "minecraft" to id.lowercase()
        else id.substring(0, idx).lowercase() to id.substring(idx + 1)
    }

    private fun normalizeTagKey(key: String): String {
        var n = key.trim()
        if (n.startsWith('#')) n = n.substring(1)
        if (n.startsWith("tag:")) n = "minecraft:" + n.substring(4)
        if (!n.contains(":")) n = "minecraft:$n"
        return n
    }

    private fun resolveBukkitTag(normalized: String): Tag<Material>? {
        return try {
            val key = NamespacedKey.fromString(normalized) ?: return null
            @Suppress("DEPRECATION")
            org.bukkit.Bukkit.getTag(Tag.REGISTRY_ITEMS, key, Material::class.java)
        } catch (_: Exception) { null }
    }

    // ==================== 反射缓存: RecipeHolder ====================

    private val clazzRecipeHolder by lazy { getClazz("net.minecraft.world.item.crafting.RecipeHolder") }

    private val constructorRecipeHolder by lazy {
        clazzRecipeHolder.constructors.firstOrNull()
            ?: throw IllegalStateException("Cannot find RecipeHolder constructor")
    }

    private val methodHolderId by lazy {
        clazzRecipeHolder.getDeclaredMethod("id")
    }

    // ==================== 反射缓存: RecipeManager / RecipeMap ====================

    private val clazzRecipeManager by lazy { getClazz(assembleMCClass("world.item.crafting.RecipeManager")) }
    private val clazzMinecraftServer by lazy { getClazz("net.minecraft.server.MinecraftServer") }
    private val methodGetServer by lazy {
        clazzMinecraftServer.getDeclaredMethod("getServer")
    }
    private val methodGetRecipeManager by lazy {
        clazzMinecraftServer.getDeclaredMethod("getRecipeManager")
    }

    // RecipeManager.recipes 字段 — 1.21.4+ 均为 RecipeMap 类型
    private val fieldRecipes: Field by lazy {
        getDeclaredField(clazzRecipeManager, "recipes")
            ?: throw IllegalStateException("Cannot find RecipeManager.recipes field")
    }

    // RecipeMap — 不可变，需通过 create() 重建
    private val clazzRecipeMap by lazy { getClazz(assembleMCClass("world.item.crafting.RecipeMap")) }
    private val methodRecipeMapCreate by lazy {
        clazzRecipeMap.getDeclaredMethod("create", Iterable::class.java)
    }
    private val methodRecipeMapValues by lazy {
        clazzRecipeMap.getDeclaredMethod("values")
    }

    // ==================== 26.1.2 专用: CommonInfo / BookInfo / ItemStackTemplate ====================

    // Recipe.CommonInfo(boolean showNotification)
    private val clazzCommonInfo by lazy { getClazz("net.minecraft.world.item.crafting.Recipe\$CommonInfo") }
    private val constructorCommonInfo by lazy {
        clazzCommonInfo.getDeclaredConstructor(Boolean::class.javaPrimitiveType)
    }

    // CraftingRecipe.CraftingBookInfo(CraftingBookCategory, String group)
    private val clazzCraftingBookInfo by lazy { getClazz("net.minecraft.world.item.crafting.CraftingRecipe\$CraftingBookInfo") }
    private val constructorCraftingBookInfo by lazy {
        clazzCraftingBookInfo.getDeclaredConstructor(clazzCraftingBookCategory, String::class.java)
    }

    // AbstractCookingRecipe.CookingBookInfo(CookingBookCategory, String group)
    private val clazzCookingBookInfo by lazy { getClazz("net.minecraft.world.item.crafting.AbstractCookingRecipe\$CookingBookInfo") }
    private val constructorCookingBookInfo by lazy {
        clazzCookingBookInfo.getDeclaredConstructor(clazzCookingBookCategory, String::class.java)
    }

    // ItemStackTemplate.fromNonEmptyStack(ItemStack) — NMS ItemStack → ItemStackTemplate
    private val clazzItemStackTemplate by lazy { getClazz("net.minecraft.world.item.ItemStackTemplate") }
    private val methodFromNonEmptyStack by lazy {
        clazzItemStackTemplate.getDeclaredMethod("fromNonEmptyStack", clazzNMSItemStack)
    }

    private fun createCommonInfo(showNotification: Boolean): Any {
        return constructorCommonInfo.newInstance(showNotification)
    }

    private fun createCraftingBookInfo(group: String): Any {
        return constructorCraftingBookInfo.newInstance(enumCraftingMISC, group)
    }

    private fun createCookingBookInfo(group: String): Any {
        return constructorCookingBookInfo.newInstance(enumCookingMISC, group)
    }

    private fun toItemStackTemplate(nmsStack: Any): Any {
        return methodFromNonEmptyStack.invoke(null, nmsStack)
    }

    // ==================== 配方分类枚举 ====================

    private val clazzCraftingBookCategory by lazy { getClazz(assembleMCClass("world.item.crafting.CraftingBookCategory")) }
    private val enumCraftingMISC: Any by lazy {
        clazzCraftingBookCategory.enumConstants.first { it.toString() == "MISC" }
    }

    private val clazzCookingBookCategory by lazy { getClazz(assembleMCClass("world.item.crafting.CookingBookCategory")) }
    private val enumCookingMISC: Any by lazy {
        clazzCookingBookCategory.enumConstants.first { it.toString() == "MISC" }
    }

    // ==================== 注册 / 注销 ====================

    fun register(recipeData: RecipeData): Boolean {
        return try {
            val path = recipeData.id.removePrefix("$NAMESPACE:")
            val recipeKey = createRecipeResourceKey(NAMESPACE, path)
            val recipeObj = buildNMSRecipe(recipeData) ?: return false
            val holder = constructorRecipeHolder.newInstance(recipeKey, recipeObj)
            injectRecipe(recipeKey, holder)
            true
        } catch (e: Exception) {
            severeS("NMS recipe registration failed for ${recipeData.id}: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    fun unregister(namespace: String, path: String): Boolean {
        return try {
            val recipeKey = createRecipeResourceKey(namespace, path)
            val server = methodGetServer.invoke(null)
            val manager = methodGetRecipeManager.invoke(server)
            val recipeMap = fieldRecipes.get(manager)
            val existingValues = methodRecipeMapValues.invoke(recipeMap) as Collection<*>

            val filtered = existingValues.filter { holder ->
                val holderId = methodHolderId.invoke(holder)
                holderId != recipeKey
            }

            if (filtered.size == existingValues.size) return false

            val newRecipeMap = methodRecipeMapCreate.invoke(null, filtered)
            fieldRecipes.set(manager, newRecipeMap)
            true
        } catch (e: Exception) {
            warningS("NMS recipe unregister failed for $namespace:$path: ${e.message}")
            false
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun injectRecipe(recipeKey: Any, holder: Any) {
        val server = methodGetServer.invoke(null)
        val manager = methodGetRecipeManager.invoke(server)
        val recipeMap = fieldRecipes.get(manager)

        val existingValues = methodRecipeMapValues.invoke(recipeMap) as Collection<Any>

        // 过滤掉同 key 旧配方，避免 ImmutableMap.Builder 抛重复 key 异常
        val filtered = existingValues.filter { existing ->
            val existingId = methodHolderId.invoke(existing)
            existingId != recipeKey
        }

        val allValues = ArrayList<Any>(filtered.size + 1)
        allValues.addAll(filtered)
        allValues.add(holder)

        val newRecipeMap = methodRecipeMapCreate.invoke(null, allValues as Collection<Any>)
        fieldRecipes.set(manager, newRecipeMap)
    }

    // ==================== 构建调度 ====================

    private fun buildNMSRecipe(recipe: RecipeData): Any? {
        return when (recipe) {
            is RecipeData.Shaped -> buildShaped(recipe)
            is RecipeData.Shapeless -> buildShapeless(recipe)
            is RecipeData.Cooking -> buildCooking(recipe)
            is RecipeData.Stonecutting -> buildStonecutting(recipe)
            is RecipeData.Smithing -> buildSmithing(recipe)
        }
    }

    // ---- Shaped ----

    private val clazzShapedRecipe by lazy { getClazz(assembleMCClass("world.item.crafting.ShapedRecipe")) }
    private val clazzShapedRecipePattern by lazy { getClazz(assembleMCClass("world.item.crafting.ShapedRecipePattern")) }
    private val methodShapedPatternOf by lazy {
        clazzShapedRecipePattern.getDeclaredMethod("of", Map::class.java, List::class.java)
    }

    private fun createShapedPattern(ingredientMap: Any?, pattern: List<String>): Any {
        val dataResult = methodShapedPatternOf.invoke(null, ingredientMap, pattern)
        // of() 在 1.21.4 / 26.1.2 都直接返回 ShapedRecipePattern，非 DataResult
        return try {
            val resultMethod = dataResult::class.java.getMethod("result")
            val optional = resultMethod.invoke(dataResult) as java.util.Optional<*>
            optional.orElseThrow { RuntimeException("ShapedRecipePattern.of returned empty") }
        } catch (_: NoSuchMethodException) {
            dataResult
        }
    }

    private val constructorShaped by lazy { clazzShapedRecipe.constructors.first() }

    private fun buildShaped(recipe: RecipeData.Shaped): Any? {
        val result = buildNMSResult(recipe.result) ?: return null
        val ingredientMap = java.util.LinkedHashMap<Char, Any>()
        recipe.key.forEach { (c, ing) -> ingredientMap[c] = createIngredient(ing) }
        val pattern = createShapedPattern(ingredientMap, recipe.pattern)

        return when (minecraftVersion) {
            MinecraftVersion.v1_21_4 -> constructorShaped.newInstance(
                recipe.group ?: "",
                enumCraftingMISC,
                pattern,
                result,
                recipe.showNotification
            )
            MinecraftVersion.v26_1_2 -> constructorShaped.newInstance(
                createCommonInfo(recipe.showNotification),
                createCraftingBookInfo(recipe.group ?: ""),
                pattern,
                result  // result 在 26.1.2 已是 ItemStackTemplate
            )
        }
    }

    // ---- Shapeless ----

    private val clazzShapelessRecipe by lazy { getClazz(assembleMCClass("world.item.crafting.ShapelessRecipe")) }
    private val constructorShapeless by lazy { clazzShapelessRecipe.constructors.first() }

    private fun buildShapeless(recipe: RecipeData.Shapeless): Any? {
        val result = buildNMSResult(recipe.result) ?: return null
        val ingredients = recipe.ingredients.map { createIngredient(it) }

        return when (minecraftVersion) {
            MinecraftVersion.v1_21_4 -> constructorShapeless.newInstance(
                recipe.group ?: "",
                enumCraftingMISC,
                result,
                java.util.ArrayList(ingredients)
            )
            MinecraftVersion.v26_1_2 -> constructorShapeless.newInstance(
                createCommonInfo(recipe.showNotification),
                createCraftingBookInfo(recipe.group ?: ""),
                result,  // result 在 26.1.2 已是 ItemStackTemplate
                java.util.ArrayList(ingredients)
            )
        }
    }

    // ---- Cooking ----

    private fun buildCooking(recipe: RecipeData.Cooking): Any? {
        val result = buildNMSResult(recipe.result) ?: return null
        val ingredient = createIngredient(recipe.ingredient)

        val recipeClass = when (recipe.type) {
            RecipeType.FURNACE -> getClazz(assembleMCClass("world.item.crafting.SmeltingRecipe"))
            RecipeType.BLASTING -> getClazz(assembleMCClass("world.item.crafting.BlastingRecipe"))
            RecipeType.SMOKING -> getClazz(assembleMCClass("world.item.crafting.SmokingRecipe"))
            RecipeType.CAMPFIRE -> getClazz(assembleMCClass("world.item.crafting.CampfireCookingRecipe"))
            else -> return null
        }

        val constructor = recipeClass.constructors.first()
        return when (minecraftVersion) {
            MinecraftVersion.v1_21_4 -> constructor.newInstance(
                recipe.group ?: "",
                enumCookingMISC,
                ingredient,
                result,
                recipe.experience,
                recipe.cookingTime
            )
            MinecraftVersion.v26_1_2 -> constructor.newInstance(
                createCommonInfo(true),
                createCookingBookInfo(recipe.group ?: ""),
                ingredient,
                result,
                recipe.experience,
                recipe.cookingTime
            )
        }
    }

    // ---- Stonecutting ----

    private val clazzStonecutterRecipe by lazy { getClazz(assembleMCClass("world.item.crafting.StonecutterRecipe")) }
    private val constructorStonecutter by lazy { clazzStonecutterRecipe.constructors.first() }

    private fun buildStonecutting(recipe: RecipeData.Stonecutting): Any? {
        val result = buildNMSResult(recipe.result) ?: return null
        val ingredient = createIngredient(recipe.ingredient)

        return when (minecraftVersion) {
            MinecraftVersion.v1_21_4 -> constructorStonecutter.newInstance(
                recipe.group ?: "",
                ingredient,
                result
            )
            MinecraftVersion.v26_1_2 -> constructorStonecutter.newInstance(
                createCommonInfo(true),
                ingredient,
                result
            )
        }
    }

    // ---- Smithing ----

    private val methodOptionalOf by lazy {
        java.util.Optional::class.java.getDeclaredMethod("of", Any::class.java)
    }

    private fun wrapOptional(ingredient: Any): Any {
        return methodOptionalOf.invoke(null, ingredient)
    }

    private fun buildSmithing(recipe: RecipeData.Smithing): Any? {
        val result = buildNMSResult(recipe.result) ?: return null

        return when (minecraftVersion) {
            MinecraftVersion.v1_21_4 -> buildSmithing21(recipe, result)
            MinecraftVersion.v26_1_2 -> buildSmithing26(recipe, result)
        }
    }

    // 1.21.4: (Optional<Ingredient> template, Optional<Ingredient> base, Optional<Ingredient> addition, ItemStack result)
    private fun buildSmithing21(recipe: RecipeData.Smithing, result: Any): Any? {
        val template = wrapOptional(createIngredient(recipe.template))
        val base = wrapOptional(createIngredient(recipe.base))
        val addition = wrapOptional(createIngredient(recipe.addition))

        return when (recipe.type) {
            RecipeType.SMITHING_TRANSFORM -> {
                val clazz = getClazz(assembleMCClass("world.item.crafting.SmithingTransformRecipe"))
                clazz.constructors.first().newInstance(template, base, addition, result)
            }
            RecipeType.SMITHING_TRIM -> {
                val clazz = getClazz(assembleMCClass("world.item.crafting.SmithingTrimRecipe"))
                clazz.constructors.first().newInstance(template, base, addition)
            }
            else -> null
        }
    }

    // 26.1.2:
    //   SmithingTransformRecipe(CommonInfo, Optional<Ingredient> template, Ingredient base, Optional<Ingredient> addition, ItemStackTemplate)
    //   SmithingTrimRecipe(CommonInfo, Ingredient, Ingredient, Ingredient, Holder<TrimPattern>) — 暂不支持
    private fun buildSmithing26(recipe: RecipeData.Smithing, result: Any): Any? {
        return when (recipe.type) {
            RecipeType.SMITHING_TRANSFORM -> {
                val template = wrapOptional(createIngredient(recipe.template))
                val base = createIngredient(recipe.base)  // 26.1.2: base 不是 Optional
                val addition = wrapOptional(createIngredient(recipe.addition))
                val clazz = getClazz(assembleMCClass("world.item.crafting.SmithingTransformRecipe"))
                clazz.constructors.first().newInstance(
                    createCommonInfo(true),
                    template,
                    base,
                    addition,
                    result
                )
            }
            RecipeType.SMITHING_TRIM -> {
                // 26.1.2 需要 Holder<TrimPattern>，暂不支持
                warningS("SmithingTrim is not supported on Minecraft 26.1.2 via NMS injection")
                null
            }
            else -> null
        }
    }

    // ---- Result ----

    private fun buildNMSResult(result: RecipeResult): Any? {
        val bukkitStack = buildBukkitResult(result) ?: return null
        val nmsStack = toNMSStack(bukkitStack)
        return when (minecraftVersion) {
            MinecraftVersion.v1_21_4 -> nmsStack
            MinecraftVersion.v26_1_2 -> toItemStackTemplate(nmsStack)
        }
    }

    private fun buildBukkitResult(result: RecipeResult): ItemStack? {
        val (namespace, key) = parseId(result.itemId)
        return when {
            namespace == "sertraline" -> {
                sertralineItemBuilder(key, null)?.apply { amount = result.count }
            }
            namespace == "minecraft" || namespace == "vanilla" -> {
                val mat = Material.matchMaterial(key) ?: return null
                ItemStack(mat, result.count)
            }
            else -> {
                ExternalItemHelper.buildNoPlayer(namespace, key)?.apply { amount = result.count }
            }
        }
    }
}
