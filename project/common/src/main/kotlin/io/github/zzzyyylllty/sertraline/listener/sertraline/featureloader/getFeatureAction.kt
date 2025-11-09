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
    if (e.feature != "sertraline:action") return
    val feature = e.feature
    val content = e.content as Map<*,*>

    val actions = LinkedHashMap<String, List<Action>>()
    content.forEach {
        val rawList = it.value as List<LinkedHashMap<*,*>>
        val list = mutableListOf<Action>()
        rawList.forEach { it ->
            list.add(Action(
                (it["condition"] ?: it["require"] ?: it["requirement"] ?: it["requirements"]).asListEnhanded(),
                (it["kether"] ?: it["ke"]).asListEnhanded(),
                (it["javascript"] ?: it["js"]).asListEnhanded(),
                (it["fluxon"] ?: it["fl"]).asListEnhanded()
            ))
        }
        actions[it.key as String] = list
    }
    e.result = actions
}