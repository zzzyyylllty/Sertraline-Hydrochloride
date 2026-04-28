package io.github.zzzyyylllty.sertraline.listener.sertraline.featureloader

import io.github.zzzyyylllty.sertraline.Sertraline.tiers
import io.github.zzzyyylllty.sertraline.data.Tier
import io.github.zzzyyylllty.sertraline.event.FeatureLoadEvent
import io.github.zzzyyylllty.sertraline.logger.warningS
import taboolib.common.platform.event.SubscribeEvent

/**
 * 处理sertraline:tier特性
 * 将品质ID解析为实际的Tier对象
 */
@SubscribeEvent
fun getFeatureTier(e: FeatureLoadEvent) {
    if (e.feature != "sertraline:tier") return

    // 物品没有定义品质特性，直接跳过（不是错误）
    val raw = e.content ?: return

    val tierId = when (raw) {
        is String -> raw
        is Number -> raw.toString()
        else -> {
            warningS("Invalid tier value for item ${e.sItemKey}: ${e.content}")
            e.result = e.content
            return
        }
    }

    // 查找对应的品质
    val tier = tiers[tierId]
    if (tier == null) {
        warningS("Tier '$tierId' not found for item ${e.sItemKey}")
        // 返回原始值，让默认处理继续
        e.result = tierId
        return
    }

    // 设置结果为品质ID（字符串）
    e.result = tierId

    // 同时在itemCache中存储品质引用
    val cache = io.github.zzzyyylllty.sertraline.Sertraline.itemCache[e.sItemKey]?.toMutableMap() ?: mutableMapOf()
    cache["tier"] = tier
    cache["tierId"] = tierId
    e.sItemKey?.let { io.github.zzzyyylllty.sertraline.Sertraline.itemCache[it] = cache }
}