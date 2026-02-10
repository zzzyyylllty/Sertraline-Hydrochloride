package io.github.zzzyyylllty.sertraline.listener.sertraline

import io.github.zzzyyylllty.sertraline.Sertraline.consoleSender
import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.config.ConfigUtil
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.event.ItemLoadEvent
import io.github.zzzyyylllty.sertraline.logger.warningL
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.chat.impl.PropertyValue
import taboolib.platform.BukkitListener.BukkitListener

fun itemDebugger(e: ItemLoadEvent, sItem: ModernSItem) {
    // 检查必需字段
    val minecraft = sItem.getMajorData("minecraft") as? Map<String, Any?>?
    val xbuilder = sItem.getMajorData("xbuilder") as? Map<String, Any?>?
    if (!(xbuilder?.containsKey("material") ?: false || minecraft?.containsKey("material") ?: false)) {
        warningL("Item_Validation_Error_NoMaterial", e.itemKey)
    }

}
