package io.github.zzzyyylllty.sertraline.listener.sertraline

import io.github.zzzyyylllty.sertraline.event.ItemLoadEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.BukkitListener.BukkitListener

@SubscribeEvent(priority = EventPriority.NORMAL)
fun itemModuleCustomData(e: ItemLoadEvent) {
    ((e.arguments["custom_data"]) as Map<String, Any?>?)?.let { e.itemData["custom_data"] = it }
    (e.arguments["chotenatb"] as? Map<String, Any?>)?.let { e.itemData["chotenatb"] = it }
    (e.arguments["chotenatb_complex"] as? List<Map<String, Any?>>)?.let { e.itemData["chotenatb_complex"] = it }
}
