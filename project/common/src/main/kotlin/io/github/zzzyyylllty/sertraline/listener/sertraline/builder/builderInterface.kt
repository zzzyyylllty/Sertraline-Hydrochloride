package io.github.zzzyyylllty.sertraline.listener.sertraline.builder

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack


fun interface ItemProcessor {
    fun process(item: ModernSItem,itemStack: ItemStack, player: Player?): ItemStack
}

class ItemProcessorManager {
    // LinkedHashMap preserves insertion order
    private val processors = LinkedHashMap<String, ItemProcessor>()

    // Register a processor by name as the key, for easier management
    fun registerProcessor(name: String, processor: ItemProcessor) {
        if (config.getBoolean("modules.$name.feature", true)) processors[name] = processor
    }

    // Remove a processor
    fun unregisterProcessor(name: String) {
        processors.remove(name)
    }

    fun unregisterAllProcessor() {
        processors.clear()
    }

    // Apply all processors in order
    fun processItem(item: ModernSItem,itemStack: ItemStack, player: Player?): ItemStack {
        var currentItem = itemStack
        for (processor in processors) {
            if (item.config.contains(processor.key)) currentItem = processor.value.process(item, currentItem, player)
        }
        return currentItem
    }

    // For inspecting the currently registered processors
    fun listProcessors(): List<String> = processors.keys.toList()
}