package io.github.zzzyyylllty.sertraline.recipe

import io.github.zzzyyylllty.sertraline.Sertraline
import io.github.zzzyyylllty.sertraline.data.*
import io.github.zzzyyylllty.sertraline.item.sertralineItemBuilder
import io.github.zzzyyylllty.sertraline.logger.severeS
import io.github.zzzyyylllty.sertraline.logger.warningS
import io.github.zzzyyylllty.sertraline.util.ExternalItemHelper
import io.github.zzzyyylllty.sertraline.util.assembleCBClassCompat
import io.github.zzzyyylllty.sertraline.util.assembleMCClass
import io.github.zzzyyylllty.sertraline.util.getClazz
import io.github.zzzyyylllty.sertraline.util.getClazzCompat
import io.github.zzzyyylllty.sertraline.util.getDeclaredFieldCompat
import io.github.zzzyyylllty.sertraline.util.getDeclaredMethodCompat
import io.github.zzzyyylllty.sertraline.util.getMethod
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
            MinecraftVersion.v1_21_4 -> getClazzCompat(
                "net.minecraft.resources.ResourceLocation",
                "net.minecraft.resources.MinecraftKey"
            )
            MinecraftVersion.v26_1_2 -> getClazz("net.minecraft.resources.Identifier")
        }
    }
    private val methodRLFromNamespaceAndPath by lazy {
        getDeclaredMethodCompat(
            clazzResourceLocation,
            listOf("fromNamespaceAndPath", "a"),
            null,
            String::class.java,
            String::class.java
        )
    }

    private fun createResourceLocation(namespace: String, path: String): Any {
        return methodRLFromNamespaceAndPath.invoke(null, namespace, path)
    }

    // ==================== 反射缓存: ResourceKey ====================

    private val clazzResourceKey by lazy { getClazz(assembleMCClass("resources.ResourceKey")) }
    private val methodResourceKeyCreate by lazy {
        getDeclaredMethodCompat(
            clazzResourceKey,
            listOf("create", "a"),
            null,
            clazzResourceKey,
            clazzResourceLocation
        )
    }

    // RECIPE 注册表 key — 1.21.4: Registries.RECIPE; 26.1.2: 同
    private val recipeRegistryKey: Any by lazy {
        getDeclaredFieldCompat(clazzRegistries, "RECIPE", "bk").get(null)
    }

    private fun createRecipeResourceKey(namespace: String, path: String): Any {
        val location = createResourceLocation(namespace, path)
        return methodResourceKeyCreate.invoke(null, recipeRegistryKey, location)
    }

    // ==================== 反射缓存: ItemStack (NMS) ====================

    private val clazzCraftItemStack by lazy {
        getClazzCompat(
            "org.bukkit.craftbukkit.inventory.CraftItemStack",
            assembleCBClassCompat("inventory.CraftItemStack")
        )
    }
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

    private val clazzIngredient by lazy {
        getClazzCompat(
            assembleMCClass("world.item.crafting.Ingredient"),
            assembleMCClass("world.item.crafting.RecipeItemStack")
        )
    }
    private val clazzNMSItemStack by lazy { getClazz(assembleMCClass("world.item.ItemStack")) }
    private val clazzNMSItem by lazy { getClazz(assembleMCClass("world.item.Item")) }
    private val clazzItemLike by lazy {
        getClazzCompat(
            assembleMCClass("world.level.ItemLike"),
            assembleMCClass("world.level.IMaterial")
        )
    }
    private val methodGetItem by lazy {
        getDeclaredMethodCompat(clazzNMSItemStack, listOf("getItem", "h"))
    }

    // Ingredient.of(ItemLike...) — varargs（1.21.4 和 26.1.2 均如此）
    private val methodIngredientOfStacks by lazy {
        val arrayClass = java.lang.reflect.Array.newInstance(clazzItemLike, 0).javaClass
        getDeclaredMethodCompat(clazzIngredient, listOf("of", "a"), null, arrayClass)
    }

    // RecipeItemStack.ofStacks(List<ItemStack>) — 精确原料：保留完整组件，配方书按组件显示；
    // 合成测试要求组件完全一致（对 placeholder 物品有 mismatch 风险，仅显式 exact 时使用）
    private val methodIngredientOfStacksExact by lazy {
        getDeclaredMethodCompat(clazzIngredient, listOf("ofStacks", "a"), null, List::class.java)
    }

    // Ingredient.of(TagKey<Item>) — 1.21.4 双平台均无此方法，
    // 改走 Registry.getTagOrEmpty(TagKey) → HolderSet.direct(List) → of(HolderSet)
    private val clazzTagKey by lazy { getClazz("net.minecraft.tags.TagKey") }
    private val clazzRegistries by lazy { getClazz("net.minecraft.core.registries.Registries") }
    private val builtInRegistriesClass by lazy { getClazz("net.minecraft.core.registries.BuiltInRegistries") }
    private val clazzHolderSet by lazy { getClazz("net.minecraft.core.HolderSet") }
    private val clazzModernRegistry by lazy {
        getClazzCompat("net.minecraft.core.IRegistry", "net.minecraft.core.Registry")
    }
    private val itemRegistry by lazy {
        getDeclaredFieldCompat(builtInRegistriesClass, "ITEM", "g").get(null)
    }
    private val methodGetTagOrEmpty by lazy {
        getDeclaredMethodCompat(clazzModernRegistry, listOf("getTagOrEmpty", "c"), null, clazzTagKey)
    }
    private val methodHolderSetDirect by lazy {
        getDeclaredMethodCompat(clazzHolderSet, listOf("direct", "a"), null, List::class.java)
    }
    private val methodIngredientOfHolderSet by lazy {
        getDeclaredMethodCompat(clazzIngredient, listOf("of", "a"), null, clazzHolderSet)
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
                if (ingredient.exact) {
                    // 精确原料：完整组件（含 lore 等）会显示在配方书与合成界面
                    return methodIngredientOfStacksExact.invoke(null, nmsStacks)
                }
                val items = toNMSItems(nmsStacks)
                methodIngredientOfStacks.invoke(null, createItemLikeArray(items))
            }
            is RecipeIngredient.Tag -> {
                val tagKey = resolveTagKey(ingredient.tagId)
                @Suppress("UNCHECKED_CAST")
                val holders = methodGetTagOrEmpty.invoke(itemRegistry, tagKey) as Iterable<Any>
                val holderList = ArrayList<Any>()
                for (holder in holders) holderList.add(holder)
                if (holderList.isEmpty()) throw IllegalArgumentException("Tag has no items: ${ingredient.tagId}")
                val holderSet = methodHolderSetDirect.invoke(null, holderList)
                methodIngredientOfHolderSet.invoke(null, holderSet)
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

    private fun resolveTagKey(tagId: String): Any {
        val normalized = normalizeTagKey(tagId)
        val (namespace, path) = normalized.split(":", limit = 2)
        val location = createResourceLocation(namespace, path)
        // TagKey.create(Registries.ITEM, location/identifier)
        val methodTagKeyCreate = getDeclaredMethodCompat(
            clazzTagKey,
            listOf("create", "a"),
            null,
            clazzResourceKey,
            clazzResourceLocation
        )

        val itemRegistryKey = getDeclaredFieldCompat(clazzRegistries, "ITEM", "K").get(null)

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
        getDeclaredMethodCompat(clazzRecipeHolder, listOf("id", "a"))
    }

    // ==================== 反射缓存: RecipeManager / RecipeMap ====================

    private val clazzRecipeManager by lazy {
        getClazzCompat(
            assembleMCClass("world.item.crafting.RecipeManager"),
            assembleMCClass("world.item.crafting.CraftingManager")
        )
    }
    private val clazzMinecraftServer by lazy { getClazz("net.minecraft.server.MinecraftServer") }
    private val methodGetServer by lazy {
        clazzMinecraftServer.getDeclaredMethod("getServer")
    }
    private val methodGetRecipeManager by lazy {
        getDeclaredMethodCompat(clazzMinecraftServer, listOf("getRecipeManager", "aI"))
    }

    // RecipeManager.recipes 字段 — 1.21.4+ 均为 RecipeMap 类型
    private val fieldRecipes: Field by lazy {
        getDeclaredFieldCompat(clazzRecipeManager, "recipes", "e", type = clazzRecipeMap)
    }

    // RecipeMap — 不可变，需通过 create() 重建
    private val clazzRecipeMap by lazy { getClazz(assembleMCClass("world.item.crafting.RecipeMap")) }
    private val methodRecipeMapCreate by lazy {
        getDeclaredMethodCompat(clazzRecipeMap, listOf("create", "a"), null, Iterable::class.java)
    }
    private val methodRecipeMapValues by lazy {
        getDeclaredMethodCompat(clazzRecipeMap, listOf("values", "a"))
    }

    // CraftingManager.finalizeRecipeLoading() — 从 RecipeMap 重建 display/listener 映射
    // (f/g/h/i) 并触发 PlayerList.reloadRecipes() 刷新在线玩家配方书。
    // 只重建 field e 时配方已注册（/recipe give 可用）但配方书 display 查询
    // (displaysForRecipe) 在 listeners 中找不到该配方，导致配方书不显示。
    private val methodFinalizeRecipeLoading by lazy {
        getDeclaredMethodCompat(clazzRecipeManager, listOf("finalizeRecipeLoading", "a"))
    }

    // ==================== 反射缓存: 玩家配方书 (RecipeBookServer) ====================
    // Bukkit 的 Player.discoverRecipe 在 spigot-api 1.21.4 已移除，只能走 NMS 解锁路径：
    // EntityPlayer.getRecipeBookServer() → RecipeBookServer.add(Collection<RecipeHolder<?>>, EntityPlayer)
    // （obf: a — 同名 b 为 remove，勿用；add 内部会触发 craft 事件、进度触发、发送 Add 包）

    private val clazzCraftPlayer by lazy {
        getClazzCompat("org.bukkit.craftbukkit.entity.CraftPlayer", assembleCBClassCompat("entity.CraftPlayer"))
    }
    private val clazzEntityPlayer by lazy {
        getClazzCompat("net.minecraft.server.level.EntityPlayer", "net.minecraft.server.level.ServerPlayer")
    }
    private val clazzRecipeBookServer by lazy {
        getClazzCompat("net.minecraft.stats.RecipeBookServer", "net.minecraft.stats.ServerRecipeBook")
    }
    private val methodGetHandle by lazy {
        // getHandle 声明在 CraftEntity 父类上，getDeclaredMethodCompat 找不到，须查继承方法
        getMethod(clazzCraftPlayer, clazzEntityPlayer, 0)
            ?: throw IllegalStateException("Cannot find CraftPlayer.getHandle()")
    }
    private val methodGetRecipeBookServer by lazy {
        getDeclaredMethodCompat(clazzEntityPlayer, listOf("getRecipeBookServer", "J"))
    }
    private val methodRecipeManagerByKey by lazy {
        getDeclaredMethodCompat(clazzRecipeManager, listOf("byKey", "b"), null, clazzResourceKey)
    }
    private val methodRecipeBookAdd by lazy {
        getDeclaredMethodCompat(
            clazzRecipeBookServer,
            listOf("add", "a"),
            null,
            Collection::class.java,
            clazzEntityPlayer
        )
    }

    /**
     * 为玩家解锁配方书中的指定配方（与进度解锁同一路径）。
     * 使配方在玩家 join 时即可显示，无需先合成一次。
     * @return 成功解锁并发送的配方数量
     */
    fun unlockForPlayer(player: org.bukkit.entity.Player, recipeIds: Collection<String>): Int {
        if (recipeIds.isEmpty()) return 0
        return try {
            val entityPlayer = methodGetHandle.invoke(player)
            val recipeBook = methodGetRecipeBookServer.invoke(entityPlayer)
            val manager = methodGetRecipeManager.invoke(methodGetServer.invoke(null))
            val holders = ArrayList<Any>(recipeIds.size)
            for (id in recipeIds) {
                val (namespace, path) = parseId(id)
                val key = createRecipeResourceKey(namespace, path)
                @Suppress("UNCHECKED_CAST")
                val optional = methodRecipeManagerByKey.invoke(manager, key) as java.util.Optional<*>
                if (optional.isPresent) holders.add(optional.get())
            }
            if (holders.isEmpty()) return 0
            methodRecipeBookAdd.invoke(recipeBook, holders, entityPlayer) as Int
        } catch (e: Exception) {
            warningS("Failed to unlock recipe book for ${player.name}: ${e.message}")
            0
        }
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
            methodFinalizeRecipeLoading.invoke(manager)
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
        // 重建 display/listener maps 并刷新玩家配方书，否则配方书不显示注入的配方
        methodFinalizeRecipeLoading.invoke(manager)
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

    private val clazzShapedRecipe by lazy {
        getClazzCompat(
            assembleMCClass("world.item.crafting.ShapedRecipe"),
            assembleMCClass("world.item.crafting.ShapedRecipes")
        )
    }
    private val clazzShapedRecipePattern by lazy { getClazz(assembleMCClass("world.item.crafting.ShapedRecipePattern")) }
    private val methodShapedPatternOf by lazy {
        getDeclaredMethodCompat(clazzShapedRecipePattern, listOf("of", "a"), null, Map::class.java, List::class.java)
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

    private val clazzShapelessRecipe by lazy {
        getClazzCompat(
            assembleMCClass("world.item.crafting.ShapelessRecipe"),
            assembleMCClass("world.item.crafting.ShapelessRecipes")
        )
    }
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
            RecipeType.FURNACE -> getClazzCompat(
                assembleMCClass("world.item.crafting.SmeltingRecipe"),
                assembleMCClass("world.item.crafting.FurnaceRecipe")
            )
            RecipeType.BLASTING -> getClazzCompat(
                assembleMCClass("world.item.crafting.BlastingRecipe"),
                assembleMCClass("world.item.crafting.RecipeBlasting")
            )
            RecipeType.SMOKING -> getClazzCompat(
                assembleMCClass("world.item.crafting.SmokingRecipe"),
                assembleMCClass("world.item.crafting.RecipeSmoking")
            )
            RecipeType.CAMPFIRE -> getClazzCompat(
                assembleMCClass("world.item.crafting.CampfireCookingRecipe"),
                assembleMCClass("world.item.crafting.RecipeCampfire")
            )
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

    private val clazzStonecutterRecipe by lazy {
        getClazzCompat(
            assembleMCClass("world.item.crafting.StonecutterRecipe"),
            assembleMCClass("world.item.crafting.RecipeStonecutting")
        )
    }
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
