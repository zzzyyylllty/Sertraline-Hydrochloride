package io.github.zzzyyylllty.sertraline.config

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.craftingStations
import io.github.zzzyyylllty.sertraline.data.*
import io.github.zzzyyylllty.sertraline.logger.infoL
import io.github.zzzyyylllty.sertraline.logger.severeL
import io.github.zzzyyylllty.sertraline.logger.warningL
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFolder
import java.io.File

fun loadCraftingStationFiles() {
    infoL("CraftingStation_Load")

    val stationsFolder = File(getDataFolder(), "crafting-stations")
    if (!stationsFolder.exists()) {
        warningL("CraftingStation_Load_Regen")
        releaseResourceFolder("crafting-stations")
    }

    val files = stationsFolder.listFiles()
    if (files == null) {
        severeL("CraftingStation_Load_Not_Found")
        return
    }

    for (file in files) {
        if (file.isDirectory) {
            file.listFiles()?.forEach {
                loadCraftingStationFile(it)
            }
        } else {
            loadCraftingStationFile(file)
        }
    }

    infoL("CraftingStation_Load_Complete", craftingStations.size)
}

fun loadCraftingStationFile(file: File): Boolean {
    if (file.isDirectory) {
        file.listFiles()?.forEach {
            loadCraftingStationFile(it)
        }
        return false
    }

    val regex = (config["file-load.crafting-station"] ?: ".*").toString()
    if (!checkRegexMatch(file.name, regex)) {
        return false
    }

    return try {
        val map = multiExtensionLoader(file)

        if (map == null || map.isEmpty()) {
            severeL("CraftingStation_Load_Error_Empty", file.name)
            return false
        }

        for (entry in map.entries) {
            val stationId = entry.key
            val value = map[stationId]
            try {
                val station = parseCraftingStation(stationId, value as? Map<String, Any?>? ?: linkedMapOf())
                craftingStations[stationId] = station
            } catch (e: Exception) {
                severeL("CraftingStation_Load_Error_Station", file.name, stationId, e.message ?: "Unknown error")
            }
        }

        true
    } catch (e: Exception) {
        severeL("CraftingStation_Load_Error_Parse", file.name, e.message ?: "Unknown error")
        false
    }
}

// ==================== 辅助扩展函数 ====================
private fun Map<String, Any?>.getOptional(key: String): Any? = this[key]

private fun Map<String, Any?>.getString(key: String): String? = this[key]?.toString()

private fun Map<String, Any?>.getInt(key: String): Int? = this[key]?.toString()?.toIntOrNull()

private fun Map<String, Any?>.getBoolean(key: String): Boolean? = this[key]?.toString()?.toBooleanStrictOrNull()

private fun Map<String, Any?>.getMap(key: String): Map<String, Any?>? = this[key] as? Map<String, Any?>

private fun Map<String, Any?>.getList(key: String): List<Any?>? = this[key] as? List<*>

// 将 Map<String, Any?> 转换为 Map<String, String>，忽略 null 值
private fun Map<String, Any?>.toStringMap(): Map<String, String> =
    this.filterValues { it != null }.mapValues { it.value!!.toString() }

// 宽容地获取条件列表：支持 conditions 或 condition 字段，支持列表或单个map
private fun Map<String, Any?>.getConditions(key: String): List<Map<String, Any?>> {
    // 尝试获取key，如果没有则尝试单数形式（去掉's'）
    var raw = this[key]
    if (raw == null && key.endsWith('s')) {
        val singularKey = key.dropLast(1)
        raw = this[singularKey]
    }
    if (raw == null) return emptyList()
    return when (raw) {
        is List<*> -> raw.filterIsInstance<Map<String, Any?>>()
        is Map<*, *> -> listOf(raw.filterKeys { it is String }.mapKeys { it.key as String })
        else -> emptyList()
    }
}

// ==================== 解析函数 ====================
private fun parseCraftingStation(id: String, map: Map<String, Any?>): CraftingStation {
    val option = map.getMap("option") ?: emptyMap()
    val displayMap = map.getMap("display") ?: throw IllegalArgumentException("CraftingStation $id missing display config")
    val display = parseDisplayConfig(displayMap)
    val recipesMap = map.getMap("recipes") ?: emptyMap()
    val recipes = recipesMap.mapValues { (recipeId, recipeMap) ->
        parseStationRecipe(recipeId, recipeMap as? Map<String, Any?> ?: emptyMap())
    }
    return CraftingStation(option, display, recipes)
}

private fun parseDisplayConfig(map: Map<String, Any?>): DisplayConfig {
    val title = map.getString("title") ?: ""
    val layout = map.getList("layout")?.filterIsInstance<String>() ?: emptyList()
    val elementsMap = map.getMap("elements") ?: emptyMap()
    val elements = elementsMap.mapValues { (elementId, elementMap) ->
        parseElementConfig(elementId, elementMap as? Map<String, Any?> ?: emptyMap())
    }
    val key = map.getString("key") ?: throw IllegalArgumentException("DisplayConfig missing key")
    return DisplayConfig(title, layout, elements, key)
}

private fun parseElementConfig(id: String, map: Map<String, Any?>): ElementConfig {
    val char = map.getString("char")
    val slot = map.getInt("slot")
    val material = map.getString("material") ?: throw IllegalArgumentException("ElementConfig $id missing material")
    val name = map.getString("name") ?: throw IllegalArgumentException("ElementConfig $id missing name")
    val lore = map.getString("lore")
    val roll = map.getBoolean("roll")
    val agents = map.getMap("agents")?.toStringMap()
    return ElementConfig(char, slot, material, name, lore, roll, agents)
}

private fun parseStationRecipe(id: String, map: Map<String, Any?>): StationRecipe {
    val displayName = map.getString("displayName") ?: id
    val displayTime = map.getString("displayTime")
    val time = map.getString("time") ?: "0"
    val options = map.getMap("options") ?: emptyMap()
    // 宽容解析 conditions
    val conditionMaps = map.getConditions("conditions")
    val conditions = conditionMaps.map { parseConditionConfig(it) }.takeIf { it.isNotEmpty() }
    val messages = map.getMap("messages")?.toStringMap()
    val displayMap = map.getMap("display")
    val display = displayMap?.let { parseOmniItem(it) }
    val inputsList = map.getList("inputs") ?: emptyList()
    val inputs = inputsList.filterIsInstance<Map<String, Any?>>().map { parseStationRecipeInput(it) }
    val outputsList = map.getList("outputs") ?: emptyList()
    val outputs = outputsList.filterIsInstance<Map<String, Any?>>().map { parseOmniItem(it) }
    val agents = map.getMap("agents")?.toStringMap()
    return StationRecipe(displayName, displayTime, time, options, conditions, messages, display, inputs, outputs, agents)
}

private fun parseConditionConfig(map: Map<String, Any?>): ConditionConfig {
    val name = map.getString("name") ?: ""
    val type = map.getString("type") ?: ""
    val amount = map.getString("amount") ?: ""
    val required = map.getString("required") ?: ""
    val condition = map.getString("condition") ?: ""
    val agents = map.getMap("agents")?.toStringMap()
    return ConditionConfig(name, type, amount, required, condition, agents)
}

private fun parseStationRecipeInput(map: Map<String, Any?>): StationRecipeInput {
    val displayName = map.getString("displayName") ?: ""
    val input = parseOmniItem(map)
    val plural = map.getString("plural")
    return StationRecipeInput(displayName, input, plural)
}

private fun parseOmniItem(map: Map<String, Any?>): OmniItem {
    // OmniItem 字段：source, item, parameters, components, amount
    // 配置中 item 字段可能是完整标识符，如 "plugin:item"
    val itemStr = map.getString("item") ?: throw IllegalArgumentException("OmniItem missing item")
    // 解析 source 和 item
    val source: String
    val item: String
    if (itemStr.contains(":")) {
        val parts = itemStr.split(":", limit = 2)
        source = parts[0]
        item = parts[1]
    } else {
        // 默认 source 为 "mc" (minecraft)
        source = "mc"
        item = itemStr
    }
    val parameters = map.getMap("parameters")?.let { LinkedHashMap(it) }
    val components = map.getMap("components")?.let { LinkedHashMap(it) }
    val amount = map.getString("amount")
    return OmniItem(source, item, parameters, components, amount)
}