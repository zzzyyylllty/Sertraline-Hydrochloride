package io.github.zzzyyylllty.sertraline.util.loreformat

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.loreFormats
import io.github.zzzyyylllty.sertraline.Sertraline.tiers
import io.github.zzzyyylllty.sertraline.Sertraline.types
import io.github.zzzyyylllty.sertraline.Sertraline.levels
import io.github.zzzyyylllty.sertraline.config.ConfigUtil
import io.github.zzzyyylllty.sertraline.config.asListEnhanced
import io.github.zzzyyylllty.sertraline.data.LineMode.*
import io.github.zzzyyylllty.sertraline.data.LoreElement
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.data.Tier
import io.github.zzzyyylllty.sertraline.data.Type
import io.github.zzzyyylllty.sertraline.data.Level
import io.github.zzzyyylllty.sertraline.function.kether.parseKether
import net.kyori.adventure.text.Component
import io.github.zzzyyylllty.sertraline.util.minimessage.toComponent
import io.github.zzzyyylllty.sertraline.util.toLowerCase
import org.bukkit.entity.Player
import taboolib.platform.compat.replacePlaceholder
import io.github.zzzyyylllty.sertraline.util.jsonUtils


fun handleLoreFormat(item: ModernSItem, player: Player?,orgLore: List<Component>?, isVisual: Boolean = true): List<Component>? {

    // 获取loreformat
    val loreFormat = loreFormats[item.getDeepData("sertraline:lore-format")] ?: return null

    // 如果模式不匹配
    if (loreFormat.settings.visual != isVisual) return null

    val lore = mutableListOf<String>()

    loreFormat.elements.forEach { element ->
        // 开始显示lore
        if (handleLoreExists(element, item)) {
            lore.addAll(handleKeyLore(item, element, player))
        }
    }

    val nLore = if (loreFormat.settings.skipBlank) mergeConsecutiveEmptyStrings(lore) else lore

    val compList = if (loreFormat.settings.overwrite) {
        nLore.toComponent()
    } else {
        val orgList = orgLore?.toMutableList() ?: mutableListOf()
        orgList.addAll(nLore.toComponent())
        orgList
    }

    return compList
}

fun Any?.performNormalPlaceholders(content: String,player: Player?,sItem: ModernSItem, skipGeneralPlaceholders: Boolean = false): String {
    val numeral = this.toString().toDoubleOrNull() ?: 0.0
    val string = this.toString()
//    var content = content
//    if (numeral != null) {
//        content = content.replace("{plus}", if (numeral > 0.0) config.getString("placeholders.plus", "+") ?: "+" else "")
//        content = content.replace("{minus}", if (numeral < 0.0) config.getString("placeholders.minus", "-") ?: "-" else "")
//    }
//    content = content
//        .replace("{value}", string).performPlaceholders(sItem, player).toString()
//        .replace("{round}", string).performPlaceholders(sItem, player)?.toDouble()?.roundToInt().toString()
//        .replace("{auto}", string).performPlaceholders(sItem, player)?.toDouble()?.round

    val regex = "\\{(.*?)}".toRegex() // 匹配 ${xxx} 的正则表达式
    val parse = mutableListOf<String>() // 创建一个Map来存储结果

    regex.findAll(content).forEach { matchResult ->
        val key = matchResult.groupValues[1]
        parse.add(key)
    }

    var content = content

    for (entry in parse) {
        val entry = entry.toLowerCase()
        val new = when {
            entry == "plus" -> if (numeral > 0.0) config.getString("placeholders.plus", "+") ?: "+" else ""
            entry == "minus" -> if (numeral < 0.0) config.getString("placeholders.minus", "-") ?: "-" else ""
            entry == "value" -> string
            entry == "auto" -> string.removeSuffix(".0")
            entry.startsWith("round:") -> "%${entry.removePrefix("round:")}.2f".format(string.toDoubleOrNull() ?: 0.0)
            else -> continue
        }
        content = content.replace("{$entry}", if (skipGeneralPlaceholders) new else new.performPlaceholders(sItem, player)!!
        )
    }
    // 处理品质占位符和其他占位符
    if (!skipGeneralPlaceholders) {
        content = content.performPlaceholders(sItem, player) ?: content
    }
    return content
}

fun String.replaceTierPlaceholders(tier: Tier): String {
    return this
        .replace("{tier}", tier.name)
        .replace("{tier:format}", tier.color + tier.name)
        .replace("{tier:weight}", tier.weight.toString())
        .replace("{tier:description}", tier.description)
        .replace("{tier:id}", tier.id)
        .replace("{tier:color}", tier.color)
}

fun String.replaceTypePlaceholders(type: Type): String {
    return this
        .replace("{type}", type.name)
        .replace("{type:name}", type.name)
        .replace("{type:id}", type.id)
        .replace("{type:parent}", type.parent ?: "")
        .replace("{type:description}", type.description)
}

fun String.replaceLevelPlaceholders(level: Level): String {
    return this
        .replace("{level}", level.name)
        .replace("{level:name}", level.name)
        .replace("{level:id}", level.id)
        .replace("{level:description}", level.description)
        .replace("{level:color}", level.color)
        .replace("{level:weight}", level.weight.toString())
}

fun String?.performPlaceholders(sItem: ModernSItem,player: Player?): String? {
    var content = this ?: run {
        return null
    }

    if (content.contains("tier")) {
        val tierData = sItem.getDeepData("sertraline:tier")
        val tier = when (tierData) {
            is Tier -> tierData
            else -> tiers[tierData?.toString()]
        }
        tier?.let { content = content.replaceTierPlaceholders(it) }
    }

    if (content.contains("type")) {
        val typeData = sItem.getDeepData("sertraline:type")
        val type = when (typeData) {
            is Type -> typeData
            else -> types[typeData?.toString()]
        }
        type?.let { content = content.replaceTypePlaceholders(it) }
    }

    if (content.contains("level")) {
        val levelData = sItem.getDeepData("sertraline:level")
        val level = when (levelData) {
            is Level -> levelData
            else -> levels[levelData?.toString()]
        }
        level?.let { content = content.replaceLevelPlaceholders(it) }
    }

    player?.let { content = content.replacePlaceholder(it) }

    // inline kether
    if (content.contains("{{")) content = content.parseKether(player, cacheId = "${sItem.key}_placeholder")
    return content
}

fun handleLoreExists(element: LoreElement,item: ModernSItem): Boolean {
    when (element.lineMode) {
        ANY -> { // 包含任何一条
            for (e in element.lineRequire ?: emptyList()) {
                if (handleExistLore(e, item)) return true // 如果有任何一个符合的
            }
            return false
        }
        ALL -> { // 包含全部
            for (e in element.lineRequire ?: emptyList()) {
                if (!handleExistLore(e, item)) return false // 如果有任何一个不符合的
            }
            return true
        }
        NOT -> { // 不包含任何一条
            for (e in element.lineRequire ?: emptyList()) {
                if (handleExistLore(e, item)) return false // 如果有任何一个不符合的
            }
            return true
        }
        NOT_ALL -> { // 全部都不包含
            for (e in element.lineRequire ?: emptyList()) {
                if (handleExistLore(e, item)) return true // 如果有任何一个符合的
            }
            return false
        }
        else -> return true
    }
}

fun handleKeyLore(item: ModernSItem,element: LoreElement,player: Player?): List<String> {
    val key = element.key
    return if (key != null) {
        val keyValueList = if (key.startsWith("*")) {
            ConfigUtil.getDeep(item.config, key.removePrefix("*")).asListEnhanced()
        } else {
            item.getDeepData(key).asListEnhanced()
        } ?: emptyList()

        if (keyValueList.isEmpty()) return emptyList()

        // First perform normal placeholders without general placeholders
        val intermediateList = keyValueList.map { value ->
            value.performNormalPlaceholders(element.content, player, item, skipGeneralPlaceholders = true)
        }

        // Serialize list to JSON, apply placeholders, then deserialize
        try {
            val json = jsonUtils.toJson(intermediateList)
            val replacedJson = json.performPlaceholders(item, player) ?: json
            // Deserialize back to List<String>
            val array = jsonUtils.fromJson(replacedJson, Array<String>::class.java)
            array.toList()
        } catch (_: Exception) {
            // Fallback to individual processing if JSON serialization fails
            keyValueList.map { value ->
                value.performNormalPlaceholders(element.content, player, item)
            }
        }
    } else {
        element.content.performPlaceholders(item, player)?.let { listOf(it) } ?: emptyList()
    }
}

fun handleExistLore(key: String,item: ModernSItem): Boolean {
    return if (key.startsWith("*")) {
        ConfigUtil.existDeep(item.config, key.removePrefix("*"))
    } else {
        item.getDeepData(key) != null
    }
}

fun mergeConsecutiveEmptyStrings(list: List<String>): List<String> {
    if (list.isEmpty()) {
        return emptyList()
    }

    val result = mutableListOf<String>()
    var lastWasEmpty = false

    for (item in list) {
        if (item.isEmpty()) {
            if (!lastWasEmpty) {
                result.add("")
                lastWasEmpty = true
            }
        } else {
            result.add(item)
            lastWasEmpty = false
        }
    }

    return result
}

fun List<String>.performPlaceholders(sItem: ModernSItem, player: Player?): List<String> {
    if (isEmpty()) return emptyList()
    // Serialize list to JSON, apply placeholders, then deserialize
    try {
        val json = jsonUtils.toJson(this)
        val replacedJson = json.performPlaceholders(sItem, player) ?: json
        // Deserialize back to List<String>
        val array = jsonUtils.fromJson(replacedJson, Array<String>::class.java)
        return array.toList()
    } catch (_: Exception) {
        // Fallback to individual processing if JSON serialization fails
        return map { it.performPlaceholders(sItem, player) ?: it }
    }
}