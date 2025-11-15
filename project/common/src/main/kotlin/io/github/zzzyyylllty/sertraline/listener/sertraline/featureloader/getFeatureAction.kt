package io.github.zzzyyylllty.sertraline.listener.sertraline.featureloader

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.itemCache
import io.github.zzzyyylllty.sertraline.Sertraline.jexlScriptCache
import io.github.zzzyyylllty.sertraline.Sertraline.jsScriptCache
import io.github.zzzyyylllty.sertraline.config.asListEnhanded
import io.github.zzzyyylllty.sertraline.config.asListedStringEnhanded
import io.github.zzzyyylllty.sertraline.event.FeatureLoadEvent
import io.github.zzzyyylllty.sertraline.util.prodJexlCompiler
import io.github.zzzyyylllty.sertraline.util.serialize.generateHash
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common5.compileJS


@Suppress("UNCHECKED_CAST", "unused")
@SubscribeEvent
fun actionFeatures(e: FeatureLoadEvent) {
    if (e.feature != "sertraline:actions") return
    val feature = e.feature
    val content = e.content as? Map<*,*> ?: return

    val actions = LinkedHashMap<String, List<LinkedHashMap<*,*>>>()
    val triggers = mutableListOf<String>()
    content.forEach {
        triggers.add(it.key.toString())
        val rawList = it.value as List<LinkedHashMap<*,*>>
        val list = mutableListOf<LinkedHashMap<*,*>>()
        rawList.forEach { it ->

            list.add(linkedMapOf(
                "condition" to (it["condition"] ?: it["require"] ?: it["requirement"] ?: it["requirements"]).asListEnhanded(),
                "kether" to (it["kether"] ?: it["ke"]).asListEnhanded(),
                "javascript" to (it["javascript"] ?: it["js"]).asListedStringEnhanded(),
                "jexl" to (it["jexl"] ?: it["je"]).asListedStringEnhanded(),
                "fluxon" to (it["fluxon"] ?: it["fl"]).asListedStringEnhanded(),
                // "kotlinscript" to (it["kotlinscript"] ?: it["kts"] ?: it["kt"]).asListedStringEnhanded()
            ))
        }

        for (map in list) {

            // 预编译脚本
            if (config.getBoolean("preload.script.jexl",true)) (map["jexl"] as? String)?.let { script ->
                try {
                    val compiled = prodJexlCompiler.compileToScript(script)
                    jexlScriptCache[script.generateHash()] = compiled
                } catch (ignored: Exception) {

                }
            }
            if (config.getBoolean("preload.script.javascript",true)) (map["javascript"] as? String)?.let { script ->
                try {
                    val compiled = script.compileJS()
                    jsScriptCache[script.generateHash()] = compiled
                } catch (ignored: Exception) {

                }
            }
            // Fluxon预编译不会传入对应变量，有可能导致编译不通过，因此取消预编译功能。
            // if (config.getBoolean("preload.script.fluxon",true)) (map["fluxon"] as? String)?.let { script ->
            //     FluxonShell.preload(script)
            // }
        }

        actions[it.key as String] = list
    }

    e.sItemKey?.let { itemCache.put(it, mapOf("actions" to triggers)) }

    e.result = actions
}