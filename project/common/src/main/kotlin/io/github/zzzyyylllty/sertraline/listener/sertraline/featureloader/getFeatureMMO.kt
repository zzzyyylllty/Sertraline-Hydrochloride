package io.github.zzzyyylllty.sertraline.listener.sertraline.featureloader


//@Suppress("UNCHECKED_CAST", "unused")
//@SubscribeEvent
//fun getFeatureMMO(e: FeatureLoadEvent) {
//    if (!e.feature.startsWith("mmo:")) return
//    val feature = e.feature
//    val content = e.content as? Map<*,*> ?: return
//
//    var cache = itemCache[e.sItemKey]?.toMutableMap() ?: mutableMapOf()
//
//    val mmoCache = (cache["mmo"] as Map<String, Any?>).toMutableMap()
//
//    mmoCache[e.feature] = e.content
//
//    cache = mmoCache
//
//    e.sItemKey?.let { itemCache[it] = cache }
//}