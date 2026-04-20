package io.github.zzzyyylllty.sertraline.listener.sertraline

import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.event.ItemLoadEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent

@SubscribeEvent(priority = EventPriority.MONITOR)
fun itemModuleSave(e: ItemLoadEvent) {
    val data = mutableMapOf<String, Any?>()
    for (entry in e.itemData) {
        val location = entry.key
        val split = location.split(":")
        if (split.size >= 2) {
            val major = split[0]
            val section = location.removePrefix("${split[0]}:")
            val majormap = (data[major] as? Map<*,*>)?.toMutableMap() ?: mutableMapOf()
            majormap[section] = entry.value
            data[major] = majormap
        } else data[location] = entry.value
    }
    val sItem = ModernSItem(
        e.itemKey,
        data = data as LinkedHashMap<*,*> as LinkedHashMap<String, Any?>,
        config = e.arguments as LinkedHashMap<*,*> as LinkedHashMap<String, Any?>
    )
    itemDebugger(e, sItem)
    e.itemKey?.let { itemMap[it] = sItem }
}
