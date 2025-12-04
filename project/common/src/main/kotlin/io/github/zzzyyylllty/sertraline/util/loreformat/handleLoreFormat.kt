package io.github.zzzyyylllty.sertraline.util.loreformat

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.configUtil
import io.github.zzzyyylllty.sertraline.Sertraline.loreFormats
import io.github.zzzyyylllty.sertraline.config.asListEnhanded
import io.github.zzzyyylllty.sertraline.data.LineMode
import io.github.zzzyyylllty.sertraline.data.LineMode.*
import io.github.zzzyyylllty.sertraline.data.LoreElement
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.function.kether.parseKether
import net.kyori.adventure.text.Component
import io.github.zzzyyylllty.sertraline.util.minimessage.toComponent
import io.github.zzzyyylllty.sertraline.util.minimessage.toComponentJson
import io.github.zzzyyylllty.sertraline.util.toLowerCase
import org.bukkit.entity.Player
import taboolib.common.util.replaceWithOrder
import taboolib.module.kether.KetherFunction
import taboolib.module.kether.ScriptOptions
import taboolib.platform.compat.replacePlaceholder
import kotlin.math.roundToInt

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

fun Any?.performNormalPlaceholders(content: String,player: Player?,sItem: ModernSItem): String {
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
        val nentry = entry.toLowerCase()
        content = content.replace("{$entry}", when {
            nentry == "plus" -> if (numeral > 0.0) config.getString("placeholders.plus", "+") ?: "+" else ""
            nentry == "minus" -> if (numeral < 0.0) config.getString("placeholders.minus", "-") ?: "-" else ""
            nentry == "value" -> string
            nentry == "auto" -> string.removeSuffix(".0")
            nentry.startsWith("round:") -> "%${nentry.removePrefix("round:")}.2f".format(string.toDoubleOrNull() ?: 0.0)
            else -> continue
        }.performPlaceholders(sItem, player)!!
        )
    }
    return content
}
fun String?.performPlaceholders(sItem: ModernSItem,player: Player?): String? {
    var content = this ?: run {
        return null
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
            configUtil.getDeep(item.config, key.removePrefix("*")).asListEnhanded()
        } else {
            item.getDeepData(key).asListEnhanded()
        } ?: emptyList()

        keyValueList.map { value ->
            value.performNormalPlaceholders(element.content, player, item)
        }
    } else {
        element.content.performPlaceholders(item, player)?.let { listOf(it) } ?: emptyList()
    }
}

fun handleExistLore(key: String,item: ModernSItem): Boolean {
    return if (key.startsWith("*")) {
        configUtil.existDeep(item.config, key.removePrefix("*"))
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