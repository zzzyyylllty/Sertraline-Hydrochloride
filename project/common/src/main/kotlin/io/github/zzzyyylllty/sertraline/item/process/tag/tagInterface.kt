package io.github.zzzyyylllty.sertraline.item.process.tag

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.util.replacePlaceholderSafety
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.ui.type.Linked
import kotlin.collections.iterator

data class ProcessItemTagData(
    var item: ModernSItem,
    val itemStack: ItemStack?,
    val player: Player?
)


fun interface TagProcessor {
    fun process(data: ProcessItemTagData, player: Player?, repl: Map<String, List<String>>, target: MutableMap<String, String?>)
}

class TagProcessorManager {
    // LinkedHashMap保持插入顺序
    private val processors = LinkedHashMap<String, TagProcessor>()
    val aliases = LinkedHashMap<String, String>()

    // 注册处理器，名称作为key，方便管理
    fun registerProcessor(name: String, tagAliases: List<String> = listOf(), processor: TagProcessor) {
        if (config.getBoolean("tags.$name", true)) {
            processors[name] = processor
            aliases[name] = name
            for (alias in tagAliases) {
                if (config.getBoolean("tags.$alias", true)) aliases[alias] = name
            }
        }
    }

    // 移除处理器
    fun unregisterProcessor(name: String) {
        processors.remove(name)
    }

    fun unregisterAllProcessor() {
        processors.clear()
    }

    // 按顺应用所有处理器
    fun processItem(itemJson: String, item: ModernSItem,itemStack: ItemStack?, player: Player?): String {
//        var itemData = ProcessItemTagData(itemJson, item, itemStack, player, extractPlaceholders(itemJson))
//        for (processor in processors) {
//            itemData = processor.value.process(itemData, player)
//        }

        val repl: Set<String> = extractPlaceholders(itemJson) ?: return itemJson

        // 提取所有处理器名称，方便进行前缀匹配
        val processorNames = aliases.keys.toList()

        // 空的直接返回
        if (repl.isEmpty()) return itemJson

        // 进行转换
        val groupedRepl: Map<String, List<String>> = repl // 直接对 Set 进行操作
            .mapNotNull { fullKey -> // fullKey 是 Set 中的每个 String 元素
                // 尝试找到一个匹配的前缀
                val matchingProcessorName = processorNames.firstOrNull { pName ->
                    fullKey.startsWith("$pName:")
                }

                if (matchingProcessorName != null) {
                    // 如果找到匹配，则提取内部的key
                    val innerKey = fullKey.substringAfter("$matchingProcessorName:")
                    // 返回一个 Pair，包含匹配的处理器名称和内部key
                    matchingProcessorName to innerKey // 使用中缀函数创建 Pair
                } else {
                    // 如果没有找到匹配的处理器名称前缀，则返回 null，
                    // mapNotNull 会过滤掉这些 null 值
                    null
                }
            }
            .groupBy { (processorName, _) -> processorName } // 按照处理器名称进行分组，这里解构 Pair
            .mapValues { (_, listOfPairs) -> // 对每个分组，listOfPairs 是 List<Pair<String, String>>
                // 将 List<Pair<processorName, innerKey>> 转换为 List<innerKey>
                listOfPairs.map { (_, innerKey) -> innerKey } // 从 Pair 中提取 innerKey
            }

        val itemData = ProcessItemTagData(item, itemStack, player)
        val target = mutableMapOf<String, String?>()

        // linkedMapOf<处理器ID, LinkedHashMap<标签头ID, List<内容>>?>()
        // 例如 - sertraline={var={"name"="名称"}}
        val useProcessors = linkedMapOf<String, LinkedHashMap<String, List<String>>?>()

        for ((key, value) in groupedRepl) {

            // "val:常量ID"
            // key = "val"
            // value = "常量ID"

            val processorName = aliases[key] ?: continue
            if (useProcessors[processorName] == null) {
                useProcessors[processorName] = linkedMapOf()
            }
            useProcessors[processorName]?.put(key, value)
        }

        devLog("groupedRepl: $groupedRepl")
        devLog("itemData: $itemData")
        devLog("targetRepl: $target")
        devLog("useProcessors: $useProcessors")

        for ((name, processor) in useProcessors) {
            processor?.let {
                processors[name]?.process(itemData, player, it, target)
            }
        }

        var json = itemJson.replacePlaceholderSafety(player)

        target.forEach { (key, value) ->
            json = if (value != "null" && value != null) {
                // 如果替换的值不是空的
                json
                    .replace("\${$key}$", value)
                    .replace("\"\${$key}$\"", value)
            } else if (!key.endsWith("!!")) {
                // 如果key不被标记为非空，即可空，即数值为null时也会被替换成null。
                json
                    .replace("\${$key}$", "null")
                    .replace("\"\${$key}$\"", "null")
            } else {
                // 如果key被标记为非空，即数值为null时也会被替换成空字符串。
                json.replace("\${$key}$", "")
                    .replace("\"\${$key}$\"", "")
            }
        }

        return json
    }

    // 可以查看当前注册处理
    fun listProcessors(): List<String> = processors.keys.toList()
}

fun extractPlaceholders(input: String): Set<String>? {

    // 不包含标签直接返回
    if (!input.contains($$"${")) return null

    val regex = "\\$\\{(.*?)}\\$".toRegex() // 匹配 ${xxx} 的正则表达式
    val result = mutableSetOf<String>() // 创建一个Map来存储结果

    regex.findAll(input).forEach { matchResult ->
        val key = matchResult.groupValues[1]
        result.add(key)
    }

    return result
}

fun String.processRawTagKey(): String {
    return this.removeSuffix("!!")
}