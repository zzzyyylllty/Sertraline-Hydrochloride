package io.github.zzzyyylllty.sertraline.listener.sertraline.featureloader

import io.github.zzzyyylllty.sertraline.Sertraline.types
import io.github.zzzyyylllty.sertraline.data.Type
import io.github.zzzyyylllty.sertraline.event.FeatureLoadEvent
import io.github.zzzyyylllty.sertraline.logger.warningS
import taboolib.common.platform.event.SubscribeEvent

/**
 * 处理sertraline:type特性
 * 将类型ID解析为实际的Type对象
 */
@SubscribeEvent
fun getFeatureType(e: FeatureLoadEvent) {
    if (e.feature != "sertraline:type") return

    // 物品没有定义类型特性，直接跳过（不是错误）
    val raw = e.content ?: return

    val typeId = when (raw) {
        is String -> raw
        is Number -> raw.toString()
        else -> {
            warningS("Invalid type value for item ${e.sItemKey}: ${e.content}")
            e.result = e.content
            return
        }
    }

    // 查找对应的类型
    val type = types[typeId]
    if (type == null) {
        warningS("Type '$typeId' not found for item ${e.sItemKey}")
        // 返回原始值，让默认处理继续
        e.result = typeId
        return
    }

    // 设置结果为类型ID（字符串）
    e.result = typeId

    // 同时在itemCache中存储类型引用
    val cache = io.github.zzzyyylllty.sertraline.Sertraline.itemCache[e.sItemKey]?.toMutableMap() ?: mutableMapOf()
    cache["type"] = type
    cache["typeId"] = typeId
    e.sItemKey?.let { io.github.zzzyyylllty.sertraline.Sertraline.itemCache[it] = cache }
}