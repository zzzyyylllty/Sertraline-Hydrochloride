package io.github.zzzyyylllty.sertraline.listener.sertraline.tag

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.tagManager
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import org.apache.commons.lang3.text.StrTokenizer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.collections.contains

data class ProcessItemTagData(
    var itemJson: String,
    var item: ModernSItem,
    val itemStack: ItemStack?,
    val player: Player?,
    val repl: Map<String, String?>
)


fun interface TagProcessor {
    fun process(data: ProcessItemTagData, player: Player?): ProcessItemTagData
}

class TagProcessorManager {
    // LinkedHashMap保持插入顺序
    private val processors = LinkedHashMap<String, TagProcessor>()

    // 注册处理器，名称作为key，方便管理
    fun registerProcessor(name: String, processor: TagProcessor) {
        if (config.getBoolean("tags.$name", true)) processors[name] = processor
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
        var itemData = ProcessItemTagData(itemJson, item, itemStack, player, extractPlaceholders(itemJson))
        for (processor in processors) {
            itemData = processor.value.process(itemData, player)
        }
        var json = itemData.itemJson
        itemData.repl.forEach { (key, value) ->
            if (value != "null" && value != null) {
                // 如果替换的值不是空的
                json = json.replace("\${$key}$", value)
            } else if (!key.endsWith("!!")) {
                // 如果key不被标记为非空，即可空，即数值为null时也会被替换成null。
                json = json.replace("\${$key}$", "null")
            } else {
                // 如果key被标记为非空，即数值为null时也会被替换成空字符串。
                json = json.replace("\${$key}$", "")
            }
        }
        return json
    }

    // 可以查看当前注册处理
    fun listProcessors(): List<String> = processors.keys.toList()
}

fun extractPlaceholders(input: String): Map<String, String?> {
    val regex = "\\$\\{(.*?)}\\$".toRegex() // 匹配 ${xxx} 的正则表达式
    val result = mutableMapOf<String, String?>() // 创建一个Map来存储结果

    regex.findAll(input).forEach { matchResult ->
        val key = matchResult.groupValues[1]
        result[key] = null
    }

    return result
}

fun String.processRawTagKey(prefix: String): String {
    return this.removePrefix(prefix).removeSuffix("!!")
}