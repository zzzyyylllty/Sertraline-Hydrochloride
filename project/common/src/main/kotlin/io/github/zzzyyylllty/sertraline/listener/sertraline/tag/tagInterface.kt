package io.github.zzzyyylllty.sertraline.listener.sertraline.tag

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.collections.contains

fun interface TagProcessor {
    fun process(item: ModernSItem,itemStack: ItemStack, player: Player?): ItemStack
}

class TagProcessorManager {
    // LinkedHashMap保持插入顺序
    private val processors = LinkedHashMap<String, TagProcessor>()

    fun registerProcessor(name: String, processor: TagProcessor) {
        if (config.getBoolean("modules.$name.feature", true)) processors[name] = processor
    }

    fun unregisterProcessor(name: String) {
        processors.remove(name)
    }

    fun unregisterAllProcessor() {
        processors.clear()
    }

    fun processItem(item: ModernSItem,itemStack: ItemStack, player: Player?): ItemStack {
        var currentItem = itemStack
        for (processor in processors) {
            if (item.config.contains(processor.key)) currentItem = processor.value.process(item, currentItem, player)
        }
        return currentItem
    }

    fun listProcessors(): List<String> = processors.keys.toList()
}