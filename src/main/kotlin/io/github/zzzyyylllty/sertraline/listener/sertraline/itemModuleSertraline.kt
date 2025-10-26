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
    val c = ConfigUtil()
    val sertraline = e.arguments?.let {
        c.getDeep(it,"sertraline") as Map<String, Any?>?
    }

    val features = listOf("sertraline:tier")

    e.itemData = c.getFeatures(sertraline, features, e.itemData)
}
