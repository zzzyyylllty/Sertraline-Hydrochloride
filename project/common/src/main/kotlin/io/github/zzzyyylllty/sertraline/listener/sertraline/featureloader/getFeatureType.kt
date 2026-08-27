package io.github.zzzyyylllty.sertraline.listener.sertraline.featureloader

import io.github.zzzyyylllty.sertraline.Sertraline.types
import io.github.zzzyyylllty.sertraline.data.Type
import io.github.zzzyyylllty.sertraline.event.FeatureLoadEvent
import io.github.zzzyyylllty.sertraline.logger.warningS
import taboolib.common.platform.event.SubscribeEvent

/**
 * Handles the sertraline:type feature
 * Resolves a type ID into the actual Type object
 */
@SubscribeEvent
fun getFeatureType(e: FeatureLoadEvent) {
    if (e.feature != "sertraline:type") return

    // The item does not define the type feature, so skip it (not an error)
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

    // Find the corresponding type
    val type = types[typeId]
    if (type == null) {
        warningS("Type '$typeId' not found for item ${e.sItemKey}")
        // Return the original value and let default handling continue
        e.result = typeId
        return
    }

    // Set the result to the type ID (string)
    e.result = typeId

    // Also store the type reference in itemCache
    val cache = io.github.zzzyyylllty.sertraline.Sertraline.itemCache[e.sItemKey]?.toMutableMap() ?: mutableMapOf()
    cache["type"] = type
    cache["typeId"] = typeId
    e.sItemKey?.let { io.github.zzzyyylllty.sertraline.Sertraline.itemCache[it] = cache }
}