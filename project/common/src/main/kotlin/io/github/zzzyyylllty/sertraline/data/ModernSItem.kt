package io.github.zzzyyylllty.sertraline.data

import io.github.zzzyyylllty.sertraline.Sertraline.jexlScriptCache
import io.github.zzzyyylllty.sertraline.Sertraline.jsScriptCache
import io.github.zzzyyylllty.sertraline.function.fluxon.FluxonShell
import io.github.zzzyyylllty.sertraline.function.fluxon.script.FunctionComponent.FluxonComponentObject
import io.github.zzzyyylllty.sertraline.function.javascript.EventUtil
import io.github.zzzyyylllty.sertraline.function.javascript.ItemStackUtil
import io.github.zzzyyylllty.sertraline.function.kether.evalKether
import io.github.zzzyyylllty.sertraline.function.kether.evalKetherBoolean
import io.github.zzzyyylllty.sertraline.util.jsonUtils
import io.github.zzzyyylllty.sertraline.util.minimessage.mmJsonUtil
import io.github.zzzyyylllty.sertraline.util.minimessage.mmUtil
import io.github.zzzyyylllty.sertraline.util.prodJexlCompiler
import io.github.zzzyyylllty.sertraline.util.serialize.generateHash
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import taboolib.common5.compileJS
import taboolib.module.nms.getItemTag
import javax.script.SimpleBindings

val defaultData by lazy {
    linkedMapOf(
        "mmUtil" to mmUtil,
        "mmJsonUtil" to mmJsonUtil,
        "jsonUtils" to jsonUtils,
        "ItemStackUtil" to ItemStackUtil,
        "EventUtil" to EventUtil
    )
}

data class ModernSItem(
    val key: String,
    val data: Map<String, Any?> = mapOf(),
    val config: Map<String, Any?> = mapOf(),
) {
    fun serialize(): String? {
        return jsonUtils.toJson(this)
    }
}

fun deserializeSItem(string: String): ModernSItem? {
    return jsonUtils.fromJson(string, ModernSItem::class.java)
}

data class Action(
    val condition: List<String>? = null,
    val async: Boolean? = null,
    val kether: List<String>? = null,
    val javaScript: String? = null,
    val jexl: String? = null,
    val fluxon: String? = null,
//    val kotlinScript: String? = null,
) {



    fun runAction(player: Player, data: Map<String, Any?>, i: ItemStack?, e: Event?, cancellableEvent: Cancellable?, sqlI: ModernSItem) {

        var parsedData = data.toMutableMap()
        parsedData["sItem"] = sqlI
        parsedData["bItem"] = i
        parsedData["event"] = e
        parsedData["cancellableEvent"] = cancellableEvent
        parsedData["player"] = player
        parsedData.putAll(defaultData)


        if (condition?.evalKetherBoolean(player, parsedData) ?: true) {
            kether?.evalKether(player, parsedData)
        }

        javaScript?.let {
            val hash = it.generateHash()
            val cache = jsScriptCache[hash]
            if (cache != null) {
                cache.eval(SimpleBindings(parsedData))
            } else {
                val compiled = it.compileJS()
                compiled?.let { it ->
                    jsScriptCache[hash] = it
                    it.eval(SimpleBindings(parsedData))
                }
            }
        }

        jexl?.let {
            val hash = it.generateHash()
            val cache = jexlScriptCache[hash]
            if (cache != null) {
                cache.eval(parsedData)
            } else {
                val compiled = prodJexlCompiler.compileToScript(it)
                compiled.let { it ->
                    jexlScriptCache[hash] = it
                    it.eval(parsedData)
                }
            }
        }

        fluxon?.let {
            FluxonShell.invoke(it) {
                root.rootVariables += parsedData
            }
        }


//        kotlinScript?.let {
//            runKotlinScriptJsr223(it, parsedData, bukkitPlugin::class.java.classLoader)
//        }

    }
}

data class ItemData(
    val itemVal: Map<String, Any?>? = mapOf(),
    val itemVar: Map<String, Any?>? = mapOf(),
    val itemDynamic: Map<String, Any?>? = mapOf(),
    val itemId: String? = null,
) {
    fun collect(): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        itemVal?.let { map.putAll(it) }
        itemVar?.let { map.putAll(it) }
        itemDynamic?.let { map.putAll(it) }
        itemId?.let { map.put("_itemname", it) }
        return map
    }

}
