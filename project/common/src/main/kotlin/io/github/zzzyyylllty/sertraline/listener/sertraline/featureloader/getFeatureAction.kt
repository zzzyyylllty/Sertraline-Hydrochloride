package io.github.zzzyyylllty.sertraline.listener.sertraline.featureloader

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.itemCache
import io.github.zzzyyylllty.sertraline.Sertraline.jexlScriptCache
import io.github.zzzyyylllty.sertraline.Sertraline.jsScriptCache
import io.github.zzzyyylllty.sertraline.config.asListEnhanded
import io.github.zzzyyylllty.sertraline.config.asListedStringEnhanded
import io.github.zzzyyylllty.sertraline.event.FeatureLoadEvent
import io.github.zzzyyylllty.sertraline.logger.severeS
import io.github.zzzyyylllty.sertraline.util.ComplexTypeHelper
import io.github.zzzyyylllty.sertraline.util.prodJexlCompiler
import io.github.zzzyyylllty.sertraline.util.serialize.generateHash
import io.github.zzzyyylllty.sertraline.util.toBooleanTolerance
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
                "async" to (it["async"])?.toBooleanTolerance(),
                "kether" to (it["kether"] ?: it["ke"]).asListEnhanded(),
                "javascript" to (it["javascript"] ?: it["js"]).asListedStringEnhanded(),
                "jexl" to (it["jexl"] ?: it["je"]).asListedStringEnhanded(),
                "fluxon" to (it["fluxon"] ?: it["fl"]).asListedStringEnhanded(),
                "gjs" to (it["graaljs"] ?: it["gjs"]).asListedStringEnhanded(),
                // "kotlinscript" to (it["kotlinscript"] ?: it["kts"] ?: it["kt"]).asListedStringEnhanded()
            ))
        }

        for (map in list) {

            // 预编译脚本
            if (config.getBoolean("preload.script.jexl",true)) (map["jexl"] as? String)?.let { script ->
                try {
                    val compiled = prodJexlCompiler.compileToScript(script)
                    jexlScriptCache[script.generateHash()] = compiled
                } catch (ex: Exception) {
                    severeS("An error was occurred in trying to pre-compile jexl script.")
                    severeS("Item: ${e.sItemKey}")
                    severeS("script: $script")
                    severeS("exception: $ex")
                }
            }
            if (config.getBoolean("preload.script.javascript",true)) (map["javascript"] as? String)?.let { script ->
                try {
                    val compiled = script.compileJS()
                    jsScriptCache[script.generateHash()] = compiled
                } catch (ex: Exception) {
                    severeS("An error was occurred in trying to pre-compile js script.")
                    severeS("Item: ${e.sItemKey}")
                    severeS("script: $script")
                    severeS("exception: $ex")
                }
            }
            // Fluxon预编译不会传入对应变量，有可能导致编译不通过，因此取消预编译功能。
            // if (config.getBoolean("preload.script.fluxon",true)) (map["fluxon"] as? String)?.let { script ->
            //     FluxonShell.preload(script)
            // }
        }

        actions[it.key as String] = list
    }

    val cache = itemCache[e.sItemKey]?.toMutableMap() ?: mutableMapOf()

    cache["preloadActions"] = ComplexTypeHelper(actions).getAsActions()
    cache["actions"] = triggers

    e.sItemKey?.let { itemCache[it] = cache }

    e.result = actions
}