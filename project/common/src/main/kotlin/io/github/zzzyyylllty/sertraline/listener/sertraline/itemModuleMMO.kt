package io.github.zzzyyylllty.sertraline.listener.sertraline

import io.github.zzzyyylllty.sertraline.config.ConfigUtil
import io.github.zzzyyylllty.sertraline.config.asListEnhanded
import io.github.zzzyyylllty.sertraline.event.ItemLoadEvent
import io.github.zzzyyylllty.sertraline.util.serialize.generateUUID
import io.github.zzzyyylllty.sertraline.util.toUpperCase
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent


val mmoFilter = listOf("id", "source", "slot", "allowed")

@SubscribeEvent(priority = EventPriority.NORMAL)
fun itemModuleMMO(e: ItemLoadEvent) {

    val prefix = "mmo"
    val c = ConfigUtil


    ((e.arguments["mmo"]) as Map<String, Any>?)?.let {

        // 默认生成一个ID
        e.itemData["mmo:id"] = e.itemKey

        e.itemData["mmo:allowed"] = listOf<String>()

        for (attribute in it) {
            val key = attribute.key
            if (mmoFilter.contains(key)) {
                when (key) {
                    // 特殊处理
                    "id" -> e.itemData["mmo:${key}"] = attribute.value.toString()
                    "source" -> e.itemData["mmo:${key}"] = attribute.value.toString()
                    "slot" -> e.itemData["mmo:${key}"] = attribute.value.toString()
                    "allowed" -> e.itemData["mmo:${key}"] = attribute.value.asListEnhanded() ?: listOf<String>()
                }
            } else {
                e.itemData["mmo:${key.replace("-","_").toUpperCase()}"] = attribute.value
            }
        }
    }

}
