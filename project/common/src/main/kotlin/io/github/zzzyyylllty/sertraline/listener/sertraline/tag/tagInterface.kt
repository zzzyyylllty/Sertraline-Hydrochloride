package io.github.zzzyyylllty.sertraline.listener.sertraline.tag

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.tagManager
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import org.apache.commons.lang3.text.StrTokenizer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.collections.contains

data class ProcessItemTagData(
    var itemJson: String,
    var item: ModernSItem,
    var itemStack: ItemStack?,
    val player: Player?,
    val repl: Map<String, String?>
)


fun interface TagProcessor {
    fun process(data: ProcessItemTagData): ProcessItemTagData
}

class TagProcessorManager {
    // LinkedHashMap保持插入顺序
    private val processors = LinkedHashMap<String, TagProcessor>()

    // 注册处理器，名称作为key，方便管理
    fun registerProcessor(name: String, processor: TagProcessor) {
        if (config.getBoolean("modules.$name.feature", true)) processors[name] = processor
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
            itemData = processor.value.process(itemData)
        }
        var json = itemData.itemJson
        itemData.repl.forEach { (key, value) ->
            if (value != null) json = json.replace("\${$key}", value)
        }
        return json
    }

    // 可以查看当前注册处理
    fun listProcessors(): List<String> = processors.keys.toList()
}

fun extractPlaceholders(input: String): Map<String, String?> {
    val regex = "\\$\\{(.*?)}".toRegex() // 匹配 ${xxx} 的正则表达式
    val result = mutableMapOf<String, String?>() // 创建一个Map来存储结果

    regex.findAll(input).forEach { matchResult ->
        val key = matchResult.groupValues[1]
        result[key] = null
    }

    return result
}
