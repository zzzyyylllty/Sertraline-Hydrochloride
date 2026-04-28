package io.github.zzzyyylllty.sertraline.listener.sertraline.featureloader

import io.github.zzzyyylllty.sertraline.Sertraline.levels
import io.github.zzzyyylllty.sertraline.data.Level
import io.github.zzzyyylllty.sertraline.event.FeatureLoadEvent
import io.github.zzzyyylllty.sertraline.logger.warningS
import taboolib.common.platform.event.SubscribeEvent

/**
 * 处理sertraline:level特性
 * 将等级ID解析为实际的Level对象
 */
@SubscribeEvent
fun getFeatureLevel(e: FeatureLoadEvent) {
    if (e.feature != "sertraline:level") return

    // 物品没有定义等级特性，直接跳过（不是错误）
    val raw = e.content ?: return

    val levelId = when (raw) {
        is String -> raw
        is Number -> raw.toString()
        else -> {
            warningS("Invalid level value for item ${e.sItemKey}: ${e.content}")
            e.result = e.content
            return
        }
    }

    // 查找对应的等级
    val level = levels[levelId]
    if (level == null) {
        warningS("Level '$levelId' not found for item ${e.sItemKey}")
        // 返回原始值，让默认处理继续
        e.result = levelId
        return
    }

    // 设置结果为等级ID（字符串）
    e.result = levelId

    // 同时在itemCache中存储等级引用
    val cache = io.github.zzzyyylllty.sertraline.Sertraline.itemCache[e.sItemKey]?.toMutableMap() ?: mutableMapOf()
    cache["level"] = level
    cache["levelId"] = levelId
    e.sItemKey?.let { io.github.zzzyyylllty.sertraline.Sertraline.itemCache[it] = cache }
}