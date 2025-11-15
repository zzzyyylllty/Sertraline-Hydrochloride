package io.github.zzzyyylllty.sertraline.listener.sertraline

import io.github.zzzyyylllty.sertraline.Sertraline.consoleSender
import io.github.zzzyyylllty.sertraline.Sertraline.mappings
import io.github.zzzyyylllty.sertraline.config.ConfigUtil
import io.github.zzzyyylllty.sertraline.event.ItemLoadEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.BukkitListener.BukkitListener
import java.util.LinkedHashMap

@SubscribeEvent(priority = EventPriority.NORMAL)
fun itemModuleCustomData(e: ItemLoadEvent) {
    ((e.arguments["custom_data"] ?: e.arguments["customdata"]) as Map<String, Any>?)?.let { e.itemData["custom_data:custom_data"] = it }
}