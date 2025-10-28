package io.github.zzzyyylllty.sertraline.listener.sertraline

import io.github.zzzyyylllty.sertraline.Sertraline.consoleSender
import io.github.zzzyyylllty.sertraline.config.ConfigUtil
import io.github.zzzyyylllty.sertraline.event.ItemLoadEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.BukkitListener.BukkitListener

@SubscribeEvent(priority = EventPriority.LOWEST)
fun itemModuleSertraline(e: ItemLoadEvent) {
    //if (settings.)

    val prefix = "sertraline"
    val c = ConfigUtil()

    val features = listOf(
        "$prefix:tier",
        "$prefix:lore-format",
    )


    e.itemData.putAll(c.getFeatures(e.arguments, features, e.itemData))
}
