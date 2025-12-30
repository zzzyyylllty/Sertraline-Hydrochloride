package io.github.zzzyyylllty.sertraline.listener.sertraline

import io.github.zzzyyylllty.sertraline.Sertraline.consoleSender
import io.github.zzzyyylllty.sertraline.Sertraline.mappings
import io.github.zzzyyylllty.sertraline.config.ConfigUtil
import io.github.zzzyyylllty.sertraline.event.ItemLoadEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.BukkitListener.BukkitListener

@SubscribeEvent(priority = EventPriority.LOW)
fun itemModuleXBuilder(e: ItemLoadEvent) {
    
    val prefix = "xbuilder"
    val c = ConfigUtil

    val features = listOf(
        "$prefix:material",
        "$prefix:name",
        "$prefix:lore",
        "$prefix:item-model",
    )

    e.itemData.putAll(c.getFeatures(e.itemKey, e.arguments, features, e.itemData))
}
