package io.github.zzzyyylllty.sertraline.listener.sertraline.builder

import io.github.zzzyyylllty.sertraline.data.ModernSItem
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack


fun interface ItemProcessor {
    fun process(item: ModernSItem,itemStack: ItemStack, player: Player?): ItemStack
}

class ItemProcessorManager {
    // LinkedHashMap保持插入顺序
    private val processors = LinkedHashMap<String, ItemProcessor>()

    // 注册处理器，名称作为key，方便管理
    fun registerProcessor(name: String, processor: ItemProcessor) {
        processors[name] = processor
    }

    // 移除处理器
    fun unregisterProcessor(name: String) {
        processors.remove(name)
    }

    fun unregisterAllProcessor() {
        processors.clear()
    }

    // 按顺应用所有处理器
    fun processItem(item: ModernSItem,itemStack: ItemStack, player: Player?): ItemStack {
        var currentItem = itemStack
        for (processor in processors) {
            currentItem = processor.value.process(item, itemStack, player)
        }
        return currentItem
    }

    // 可以查看当前注册处理器
    fun listProcessors(): List<String> = processors.keys.toList()
}