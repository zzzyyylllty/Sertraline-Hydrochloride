package io.github.zzzyyylllty.sertraline.config

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.data.*
import io.github.zzzyyylllty.sertraline.logger.infoL
import io.github.zzzyyylllty.sertraline.logger.severeL
import io.github.zzzyyylllty.sertraline.logger.warningL
import io.github.zzzyyylllty.sertraline.util.RecipeManager
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFolder
import java.io.File

const val recipeDisabled = true

/**
 * 加载配方配置文件
 */
fun loadRecipeFiles() {
    if (recipeDisabled) return
    infoL("Recipe_Load")

    val recipesFolder = File(getDataFolder(), "recipes")
    if (!recipesFolder.exists()) {
        warningL("Recipe_Load_Regen")
        releaseResourceFolder("recipes")
    }

    val files = recipesFolder.listFiles()
    if (files == null) {
        severeL("Recipe_Load_Not_Found")
        return
    }

    var loadedCount = 0
    var errorCount = 0

    for (file in files) {
        if (file.isDirectory) {
            file.listFiles()?.forEach {
                val (loaded, errors) = loadRecipeFile(it)
                loadedCount += loaded
                errorCount += errors
            }
        } else {
            val (loaded, errors) = loadRecipeFile(file)
            loadedCount += loaded
            errorCount += errors
        }
    }

    infoL("Recipe_Load_Complete", loadedCount, errorCount)
}

/**
 * 加载单个配方文件
 * @return Pair(成功加载的配方数量, 错误数量)
 */
fun loadRecipeFile(file: File): Pair<Int, Int> {
    if (file.isDirectory) {
        var totalLoaded = 0
        var totalErrors = 0
        file.listFiles()?.forEach {
            val (loaded, errors) = loadRecipeFile(it)
            totalLoaded += loaded
            totalErrors += errors
        }
        return Pair(totalLoaded, totalErrors)
    }

    val regex = (config["file-load.recipe"] ?: ".*").toString()
    if (!checkRegexMatch(file.name, regex)) {
        return Pair(0, 0)
    }

    return try {
        val map = multiExtensionLoader(file)

        if (map == null || map.isEmpty()) {
            severeL("Recipe_Load_Error_Empty", file.name)
            return Pair(0, 1)
        }

        var loaded = 0
        var errors = 0

        for ((recipeId, recipeData) in map.entries) {
            try {
                val recipe = parseRecipe(recipeId, recipeData as? Map<String, Any?> ?: emptyMap())
                if (RecipeManager.registerRecipe(recipe)) {
                    loaded++
                } else {
                    severeL("Recipe_Load_Error_Register", file.name, recipeId)
                    errors++
                }
            } catch (e: Exception) {
                severeL("Recipe_Load_Error_Parse", file.name, recipeId, e.message ?: "Unknown error")
                errors++
            }
        }

        Pair(loaded, errors)
    } catch (e: Exception) {
        severeL("Recipe_Load_Error_File", file.name, e.message ?: "Unknown error")
        Pair(0, 1)
    }
}

// ==================== 辅助扩展函数 ====================
private fun Map<String, Any?>.getOptional(key: String): Any? = this[key]

private fun Map<String, Any?>.getString(key: String): String? = this[key]?.toString()

private fun Map<String, Any?>.getInt(key: String): Int? = this[key]?.toString()?.toIntOrNull()

private fun Map<String, Any?>.getFloat(key: String): Float? = this[key]?.toString()?.toFloatOrNull()

private fun Map<String, Any?>.getBoolean(key: String): Boolean? = this[key]?.toString()?.toBooleanStrictOrNull()

private fun Map<String, Any?>.getMap(key: String): Map<String, Any?>? = this[key] as? Map<String, Any?>

private fun Map<String, Any?>.getList(key: String): List<Any?>? = this[key] as? List<*>

// 宽容地获取列表，支持单个元素自动转为列表
private fun Map<String, Any?>.getFlexibleList(key: String): List<Any?> {
    val value = this[key] ?: return emptyList()
    return when (value) {
        is List<*> -> value.toList()
        else -> listOf(value)
    }
}

// ==================== 解析函数 ====================
private fun parseRecipe(id: String, map: Map<String, Any?>): RecipeData {
    val typeStr = map.getString("type") ?: throw IllegalArgumentException("Recipe $id missing type")
    val type = try {
        RecipeType.valueOf(typeStr.uppercase())
    } catch (e: IllegalArgumentException) {
        throw IllegalArgumentException("Recipe $id has invalid type: $typeStr")
    }

    val resultMap = map.getMap("result") ?: throw IllegalArgumentException("Recipe $id missing result")
    val result = parseRecipeResult(resultMap)

    return when (type) {
        RecipeType.SHAPED -> parseShapedRecipe(id, result, map)
        RecipeType.SHAPELESS -> parseShapelessRecipe(id, result, map)
        RecipeType.SMELTING, RecipeType.BLASTING, RecipeType.SMOKING, RecipeType.CAMPFIRE_COOKING ->
            parseCookingRecipe(id, result, type, map)
        RecipeType.STONECUTTING -> parseStonecuttingRecipe(id, result, map)
        RecipeType.SMITHING_TRANSFORM, RecipeType.SMITHING_TRIM ->
            parseSmithingRecipe(id, result, type, map)
    }
}

private fun parseRecipeResult(map: Map<String, Any?>): RecipeResult {
    val itemId = map.getString("itemId") ?: throw IllegalArgumentException("RecipeResult missing itemId")
    val count = map.getInt("count") ?: 1
    return RecipeResult(itemId, count)
}

private fun parseShapedRecipe(id: String, result: RecipeResult, map: Map<String, Any?>): RecipeData.Shaped {
    val patternList = map.getList("pattern") ?: throw IllegalArgumentException("Shaped recipe $id missing pattern")
    val pattern = patternList.filterIsInstance<String>()

    if (pattern.isEmpty() || pattern.size > 3) {
        throw IllegalArgumentException("Shaped recipe $id pattern must have 1-3 rows")
    }

    val keyMap = map.getMap("key") ?: throw IllegalArgumentException("Shaped recipe $id missing key")
    val key = keyMap.mapKeys { (keyStr, _) ->
        if (keyStr.length != 1) {
            throw IllegalArgumentException("Shaped recipe $id key must be single character: '$keyStr'")
        }
        keyStr[0]
    }.mapValues { (_, ingredientValue) ->
        parseIngredient(ingredientValue)
    }

    val showNotification = map.getBoolean("showNotification") ?: true

    return RecipeData.Shaped(id, result, pattern, key, showNotification)
}

private fun parseShapelessRecipe(id: String, result: RecipeResult, map: Map<String, Any?>): RecipeData.Shapeless {
    val ingredientsList = map.getFlexibleList("ingredients")
    val ingredients = ingredientsList.map { parseIngredient(it) }

    if (ingredients.isEmpty()) {
        throw IllegalArgumentException("Shapeless recipe $id must have at least one ingredient")
    }

    val showNotification = map.getBoolean("showNotification") ?: true

    return RecipeData.Shapeless(id, result, ingredients, showNotification)
}

private fun parseCookingRecipe(
    id: String,
    result: RecipeResult,
    type: RecipeType,
    map: Map<String, Any?>
): RecipeData.Cooking {
    val ingredientValue = map["ingredient"] ?: throw IllegalArgumentException("Cooking recipe $id missing ingredient")
    val ingredient = parseIngredient(ingredientValue)

    val experience = map.getFloat("experience") ?: 0.1f
    val cookingTime = map.getInt("cookingTime") ?: 200

    return RecipeData.Cooking(id, result, type, ingredient, experience, cookingTime)
}

private fun parseStonecuttingRecipe(id: String, result: RecipeResult, map: Map<String, Any?>): RecipeData.Stonecutting {
    val ingredientValue = map["ingredient"] ?: throw IllegalArgumentException("Stonecutting recipe $id missing ingredient")
    val ingredient = parseIngredient(ingredientValue)

    return RecipeData.Stonecutting(id, result, ingredient)
}

private fun parseSmithingRecipe(
    id: String,
    result: RecipeResult,
    type: RecipeType,
    map: Map<String, Any?>
): RecipeData.Smithing {
    val templateValue = map["template"] ?: throw IllegalArgumentException("Smithing recipe $id missing template")
    val template = parseIngredient(templateValue)

    val baseValue = map["base"] ?: throw IllegalArgumentException("Smithing recipe $id missing base")
    val base = parseIngredient(baseValue)

    val additionValue = map["addition"] ?: throw IllegalArgumentException("Smithing recipe $id missing addition")
    val addition = parseIngredient(additionValue)

    return RecipeData.Smithing(id, result, type, template, base, addition)
}

/**
 * 解析配方原料，支持多种格式：
 * 1. 字符串格式: "minecraft:iron_ingot" 或 "minecraft:iron_ingot 3"
 * 2. 标签格式: "tag:planks" 或 "tag:planks 2" 或 "#minecraft:planks"
 * 3. 对象格式: { "itemId": "minecraft:diamond", "amount": 2 }
 * 4. 标签对象格式: { "tagId": "minecraft:planks", "amount": 2 }
 * 5. 选择格式: { "options": [...] }
 */
private fun parseIngredient(value: Any?): RecipeIngredient {
    when (value) {
        is String -> {
            return parseIngredientFromString(value)
        }
        is Map<*, *> -> {
            val map = value as Map<String, Any?>

            // 检查是否是选择格式
            if (map.containsKey("options")) {
                val optionsList = map.getList("options") ?: emptyList()
                val options = optionsList.map { parseIngredient(it) }
                return RecipeIngredient.Choice(options)
            }

            // 检查是物品还是标签
            if (map.containsKey("itemId")) {
                val itemId = map.getString("itemId") ?: throw IllegalArgumentException("Ingredient missing itemId")
                val amount = map.getInt("amount") ?: 1
                return RecipeIngredient.Item(itemId, amount)
            } else if (map.containsKey("tagId")) {
                val tagId = map.getString("tagId") ?: throw IllegalArgumentException("Ingredient missing tagId")
                val amount = map.getInt("amount") ?: 1
                return RecipeIngredient.Tag(tagId, amount)
            } else {
                throw IllegalArgumentException("Ingredient map must contain either 'itemId' or 'tagId'")
            }
        }
        else -> {
            throw IllegalArgumentException("Unsupported ingredient format: ${value?.javaClass?.simpleName}")
        }
    }
}

/**
 * 从字符串解析配方原料
 * 支持格式:
 * - "minecraft:iron_ingot" (默认数量1)
 * - "minecraft:iron_ingot 3" (带数量)
 * - "tag:planks" (标签)
 * - "tag:planks 2" (带数量的标签)
 * - "#minecraft:planks" (标准Minecraft标签格式)
 */
private fun parseIngredientFromString(str: String): RecipeIngredient {
    val trimmed = str.trim()

    if (trimmed.isEmpty()) {
        throw IllegalArgumentException("Ingredient string cannot be empty")
    }

    // 检查是否包含数量（空格分隔）
    val parts = trimmed.split("\\s+".toRegex())
    val base = parts[0]
    val amount = if (parts.size > 1) {
        parts[1].toIntOrNull() ?: 1
    } else {
        1
    }

    // 判断是标签还是物品
    return when {
        base.startsWith("tag:") || base.startsWith("#") -> {
            // 标签格式
            RecipeIngredient.Tag(base, amount)
        }
        base.contains(":") -> {
            // 命名空间格式的物品
            RecipeIngredient.Item(base, amount)
        }
        else -> {
            // 简单物品ID，添加minecraft:命名空间
            RecipeIngredient.Item("minecraft:$base", amount)
        }
    }
}