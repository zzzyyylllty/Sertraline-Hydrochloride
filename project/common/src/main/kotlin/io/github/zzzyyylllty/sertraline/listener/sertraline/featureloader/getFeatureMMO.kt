package io.github.zzzyyylllty.sertraline.listener.sertraline.featureloader

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.gjsScriptCache
import io.github.zzzyyylllty.sertraline.Sertraline.itemCache
import io.github.zzzyyylllty.sertraline.Sertraline.jexlScriptCache
import io.github.zzzyyylllty.sertraline.Sertraline.jsScriptCache
import io.github.zzzyyylllty.sertraline.config.asListEnhanded
import io.github.zzzyyylllty.sertraline.config.asListedStringEnhanded
import io.github.zzzyyylllty.sertraline.event.FeatureLoadEvent
import io.github.zzzyyylllty.sertraline.logger.severeS
import io.github.zzzyyylllty.sertraline.util.ComplexTypeHelper
import io.github.zzzyyylllty.sertraline.util.GraalJsUtil
import io.github.zzzyyylllty.sertraline.util.prodJexlCompiler
import io.github.zzzyyylllty.sertraline.util.serialize.generateHash
import io.github.zzzyyylllty.sertraline.util.toBooleanTolerance
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common5.compileJS


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