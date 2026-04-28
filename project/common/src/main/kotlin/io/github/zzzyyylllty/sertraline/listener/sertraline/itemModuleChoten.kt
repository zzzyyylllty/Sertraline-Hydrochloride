package io.github.zzzyyylllty.sertraline.listener.sertraline

import io.github.zzzyyylllty.sertraline.config.ConfigUtil
import io.github.zzzyyylllty.sertraline.config.asListEnhanced
import io.github.zzzyyylllty.sertraline.event.ItemLoadEvent
import io.github.zzzyyylllty.sertraline.util.toUpperCase
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent

private val chotenFilter = setOf("id", "source", "slot", "allowed")

@SubscribeEvent(priority = EventPriority.NORMAL)
fun itemModuleChoten(e: ItemLoadEvent) {
    ((e.arguments["choten"]) as Map<String, Any>?)?.let { chotenConfig ->

        // 默认生成一个ID
        e.itemData["choten:id"] = e.itemKey
        e.itemData["choten:allowed"] = listOf<String>()

        for (attribute in chotenConfig) {
            val key = attribute.key
            if (key in chotenFilter) {
                when (key) {
                    "id" -> e.itemData["choten:$key"] = attribute.value.toString()
                    "source" -> e.itemData["choten:$key"] = attribute.value.toString()
                    "slot" -> e.itemData["choten:$key"] = attribute.value.toString()
                    "allowed" -> e.itemData["choten:$key"] = attribute.value.asListEnhanced() ?: listOf<String>()
                }
            } else {
                e.itemData["choten:${key.replace("-", "_").lowercase()}"] = attribute.value
            }
        }
    }
}
