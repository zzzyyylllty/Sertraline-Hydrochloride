package io.github.zzzyyylllty.sertraline.config

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.data.*
import io.github.zzzyyylllty.sertraline.logger.infoL
import io.github.zzzyyylllty.sertraline.logger.severeL
import io.github.zzzyyylllty.sertraline.logger.warningL
import io.github.zzzyyylllty.sertraline.util.SertralineRecipeManager
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFolder
import java.io.File

/**
 * 加载配方配置文件。
 * 格式参考 recipes/sample.yml。
 */
fun loadRecipeFiles() {
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

    // 先注销所有旧配方
    SertralineRecipeManager.unregisterAll()

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
 * 加载单个配方文件。
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
        val raw = multiExtensionLoader(file) ?: return Pair(0, 1)
        val map = TemplateManager.resolveInMap(raw)

        var loaded = 0
        var errors = 0

        for ((recipeId, recipeData) in map.entries) {
            try {
                val recipe = parseRecipe(recipeId, recipeData as? Map<String, Any?> ?: emptyMap())
                if (SertralineRecipeManager.registerRecipe(recipe)) {
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
private fun Map<String, Any?>.getFlexibleList(key: String): List<Any?> {
    val value = this[key] ?: return emptyList()
    return when (value) {
        is List<*> -> value.toList()
        else -> listOf(value)
    }
}

// ==================== 主解析函数 ====================

private fun parseRecipe(id: String, map: Map<String, Any?>): RecipeData {
    val typeStr = map.getString("type") ?: throw IllegalArgumentException("Recipe $id missing type")
    val type = runCatching {
        RecipeType.valueOf(typeStr.uppercase())
    }.getOrElse {
        throw IllegalArgumentException("Recipe $id has invalid type: $typeStr")
    }

    val formatStr = map.getString("format")
    val format = formatStr?.let { runCatching { RecipeFormat.valueOf(it.uppercase()) }.getOrNull() }

    val providerStr = map.getString("provider") ?: "SIMPLE"
    val provider = runCatching {
        RecipeProvider.valueOf(providerStr.uppercase())
    }.getOrElse {
        throw IllegalArgumentException("Recipe $id has invalid provider: $providerStr")
    }

    val inputOptions = parseInputOptions(map.getMap("input-options"))

    return when (type) {
        RecipeType.CRAFTING -> parseCrafting(id, provider, inputOptions, format, map)
        RecipeType.FURNACE, RecipeType.BLASTING, RecipeType.SMOKING, RecipeType.CAMPFIRE ->
            parseCooking(id, provider, inputOptions, type, map)
        RecipeType.STONECUTTING -> parseStonecutting(id, provider, inputOptions, map)
        RecipeType.SMITHING_TRANSFORM, RecipeType.SMITHING_TRIM ->
            parseSmithing(id, provider, inputOptions, type, map)
    }
}

// ==================== Input Options ====================

private fun parseInputOptions(map: Map<String, Any?>?): InputOptions {
    if (map == null) return InputOptions()

    val dataFilter = map.getList("data-filter")?.filterIsInstance<String>() ?: emptyList()

    val components = map.getMap("components")?.let { compMap ->
        val modeStr = compMap.getString("mode") ?: return@let null
        val mode = runCatching { ComponentsMode.valueOf(modeStr.uppercase()) }.getOrNull() ?: return@let null
        val elements = compMap.getList("elements")?.filterIsInstance<String>() ?: emptyList()
        ComponentFilter(mode, elements)
    }

    return InputOptions(dataFilter, components)
}

// ==================== CRAFTING ====================

private fun parseCrafting(
    id: String,
    provider: RecipeProvider,
    inputOptions: InputOptions,
    format: RecipeFormat?,
    map: Map<String, Any?>
): RecipeData {
    val resultMap = map.getMap("result") ?: throw IllegalArgumentException("Recipe $id missing result")
    val result = parseRecipeResult(resultMap)
    val group = map.getString("group")

    return when (format ?: RecipeFormat.SHAPED) {
        RecipeFormat.SHAPED -> {
            val patternList = map.getList("pattern")
                ?: throw IllegalArgumentException("Shaped recipe $id missing pattern")
            val pattern = patternList.filterIsInstance<String>()
            if (pattern.isEmpty() || pattern.size > 3) {
                throw IllegalArgumentException("Shaped recipe $id pattern must have 1-3 rows")
            }
            val keyMap = map.getMap("ingredients")
                ?: throw IllegalArgumentException("Shaped recipe $id missing ingredients")
            val key = keyMap.mapKeys { (keyStr, _) ->
                if (keyStr.length != 1) throw IllegalArgumentException("Shaped recipe $id key must be single char: '$keyStr'")
                keyStr[0]
            }.mapValues { (_, value) ->
                parseIngredient(value)
            }
            val showNotification = map.getBoolean("showNotification") ?: true
            RecipeData.Shaped(id, result, inputOptions, provider, pattern, key, showNotification, group)
        }
        RecipeFormat.SHAPELESS -> {
            val ingredientsList = map.getFlexibleList("ingredients")
            val ingredients = ingredientsList.map { parseIngredient(it) }
            if (ingredients.isEmpty()) {
                throw IllegalArgumentException("Shapeless recipe $id must have at least one ingredient")
            }
            val showNotification = map.getBoolean("showNotification") ?: true
            RecipeData.Shapeless(id, result, inputOptions, provider, ingredients, showNotification, group)
        }
    }
}

// ==================== Cooking ====================

private fun parseCooking(
    id: String,
    provider: RecipeProvider,
    inputOptions: InputOptions,
    type: RecipeType,
    map: Map<String, Any?>
): RecipeData.Cooking {
    val resultMap = map.getMap("result") ?: throw IllegalArgumentException("Recipe $id missing result")
    val result = parseRecipeResult(resultMap)
    val ingredient = parseIngredient(map["ingredient"] ?: throw IllegalArgumentException("Cooking recipe $id missing ingredient"))
    val experience = map.getFloat("experience") ?: 0.1f
    val cookingTime = map.getInt("cookingTime") ?: map.getInt("time") ?: 200
    val group = map.getString("group")
    return RecipeData.Cooking(id, result, inputOptions, type, provider, ingredient, experience, cookingTime, group)
}

// ==================== Stonecutting ====================

private fun parseStonecutting(
    id: String,
    provider: RecipeProvider,
    inputOptions: InputOptions,
    map: Map<String, Any?>
): RecipeData.Stonecutting {
    val resultMap = map.getMap("result") ?: throw IllegalArgumentException("Recipe $id missing result")
    val result = parseRecipeResult(resultMap)
    val ingredient = parseIngredient(map["ingredient"] ?: throw IllegalArgumentException("Stonecutting recipe $id missing ingredient"))
    val group = map.getString("group")
    return RecipeData.Stonecutting(id, result, inputOptions, provider, ingredient, group)
}

// ==================== Smithing ====================

private fun parseSmithing(
    id: String,
    provider: RecipeProvider,
    inputOptions: InputOptions,
    type: RecipeType,
    map: Map<String, Any?>
): RecipeData.Smithing {
    val resultMap = map.getMap("result") ?: throw IllegalArgumentException("Recipe $id missing result")
    val result = parseRecipeResult(resultMap)
    val template = parseIngredient(map["template"] ?: throw IllegalArgumentException("Smithing recipe $id missing template"))
    val base = parseIngredient(map["base"] ?: throw IllegalArgumentException("Smithing recipe $id missing base"))
    val addition = parseIngredient(map["addition"] ?: throw IllegalArgumentException("Smithing recipe $id missing addition"))
    val group = map.getString("group")
    return RecipeData.Smithing(id, result, inputOptions, type, provider, template, base, addition, group)
}

// ==================== Result ====================

private fun parseRecipeResult(map: Map<String, Any?>): RecipeResult {
    val itemId = map.getString("id") ?: throw IllegalArgumentException("RecipeResult missing id")
    val count = map.getInt("count") ?: 1
    val functions = parseFunctions(map.getFlexibleList("function"))
    return RecipeResult(itemId, count, functions)
}

private fun parseFunctions(list: List<Any?>): List<RecipeFunction> {
    return list.mapNotNull { item ->
        when (item) {
            is String -> RecipeFunction.Kether(item)
            is Map<*, *> -> {
                val funcMap = item as Map<String, Any?>
                val type = funcMap.getString("type")?.lowercase() ?: return@mapNotNull null
                when (type) {
                    "kether" -> funcMap["kether"]?.let { RecipeFunction.Kether(it.toString()) }
                    "javascript", "js" -> funcMap["script"]?.let { RecipeFunction.JavaScript(it.toString()) }
                        ?: funcMap["javascript"]?.let { RecipeFunction.JavaScript(it.toString()) }
                    "command" -> funcMap["command"]?.let { RecipeFunction.Command(it.toString()) }
                    else -> null
                }
            }
            else -> null
        }
    }
}

// ==================== Ingredient ====================

private fun parseIngredient(value: Any?): RecipeIngredient {
    return when (value) {
        is String -> parseIngredientFromString(value)
        is Map<*, *> -> {
            val map = value as Map<String, Any?>
            when {
                map.containsKey("options") -> {
                    val options = map.getList("options")?.map { parseIngredient(it) } ?: emptyList()
                    RecipeIngredient.Choice(options)
                }
                map.containsKey("itemId") -> {
                    RecipeIngredient.Item(
                        map.getString("itemId") ?: error("Ingredient missing itemId"),
                        map.getInt("amount") ?: 1
                    )
                }
                map.containsKey("tagId") -> {
                    RecipeIngredient.Tag(
                        map.getString("tagId") ?: error("Ingredient missing tagId"),
                        map.getInt("amount") ?: 1
                    )
                }
                else -> throw IllegalArgumentException("Ingredient map must contain 'itemId', 'tagId', or 'options'")
            }
        }
        else -> throw IllegalArgumentException("Unsupported ingredient format: ${value?.javaClass?.simpleName}")
    }
}

private fun parseIngredientFromString(str: String): RecipeIngredient {
    val trimmed = str.trim()
    if (trimmed.isEmpty()) throw IllegalArgumentException("Ingredient string cannot be empty")

    val parts = trimmed.split("\\s+".toRegex())
    val base = parts[0]
    val amount = if (parts.size > 1) (parts[1].toIntOrNull() ?: 1) else 1

    return when {
        base.startsWith("#") || base.startsWith("tag:") -> RecipeIngredient.Tag(base, amount)
        base.contains(":") -> RecipeIngredient.Item(base, amount)
        else -> RecipeIngredient.Item("minecraft:$base", amount)
    }
}
