package io.github.zzzyyylllty.sertraline.listener.sertraline.featureloader

import io.github.zzzyyylllty.sertraline.config.asListEnhanded
import io.github.zzzyyylllty.sertraline.data.Action
import io.github.zzzyyylllty.sertraline.event.FeatureLoadEvent
import io.github.zzzyyylllty.sertraline.util.jsonUtils
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import kotlin.coroutines.Continuation

@Suppress("UNCHECKED_CAST", "unused")
@SubscribeEvent
fun actionFeatures(e: FeatureLoadEvent) {
    if (e.feature != "sertraline:actions") return
    val feature = e.feature
    val content = e.content as Map<*,*>

    val actions = LinkedHashMap<String, List<LinkedHashMap<*,*>>>()
    content.forEach {
        val rawList = it.value as List<LinkedHashMap<*,*>>
        val list = mutableListOf<LinkedHashMap<*,*>>()
        rawList.forEach { it ->
            list.add(linkedMapOf(
                "condition" to (it["condition"] ?: it["require"] ?: it["requirement"] ?: it["requirements"]).asListEnhanded(),
                "kether" to (it["kether"] ?: it["ke"]).asListEnhanded(),
                "javascript" to (it["javascript"] ?: it["js"]).asListEnhanded(),
                "fluxon" to (it["fluxon"] ?: it["fl"]).asListEnhanded()
            ))
        }
        actions[it.key as String] = list
    }
    e.result = actions
}