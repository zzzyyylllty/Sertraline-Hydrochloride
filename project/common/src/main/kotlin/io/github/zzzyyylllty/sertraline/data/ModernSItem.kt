package io.github.zzzyyylllty.sertraline.data

import com.google.gson.reflect.TypeToken
import io.github.zzzyyylllty.sertraline.Sertraline.jexlScriptCache
import io.github.zzzyyylllty.sertraline.Sertraline.jsScriptCache
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.function.fluxon.FluxonShell
import io.github.zzzyyylllty.sertraline.function.kether.evalKether
import io.github.zzzyyylllty.sertraline.function.kether.evalKetherBoolean
import io.github.zzzyyylllty.sertraline.util.jsonUtils
import io.github.zzzyyylllty.sertraline.util.minimessage.mmJsonUtil
import io.github.zzzyyylllty.sertraline.util.minimessage.mmUtil
import io.github.zzzyyylllty.sertraline.util.prodJexlCompiler
import io.github.zzzyyylllty.sertraline.util.serialize.generateUUID
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import org.tabooproject.fluxon.Fluxon
import java.lang.reflect.Type
import taboolib.common5.compileJS
import javax.script.SimpleBindings

val defaultData by lazy {
    linkedMapOf(
        "mmUtil" to mmUtil,
        "mmJsonUtil" to mmJsonUtil,
        "jsonUtils" to jsonUtils
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
    var condition: List<String>? = null,
    var kether: List<String>? = null,
    var javaScript: String? = null,
    var jexl: String? = null,
    var fluxon: String? = null,
) {
    fun runAction(player: Player, data: Map<String, Any?>, i: ItemStack?, e: Event?, sqlI: ModernSItem) {
        val parsedData = data.toMutableMap()
        parsedData["@SertralineItem"] = sqlI
        parsedData["@SertralineItemStack"] = i
        parsedData["@SertralineEvent"] = e
        parsedData["sItem"] = sqlI
        parsedData["bItem"] = i
        parsedData["event"] = e
        parsedData["player"] = player
        parsedData.putAll(defaultData)

        if (condition?.evalKetherBoolean(player, parsedData) ?: true) {
            kether?.evalKether(player, parsedData)
        }

        javaScript?.let {
            val uuid = it.generateUUID()
            val cache = jsScriptCache[uuid]
            if (cache != null) {
                cache.eval(SimpleBindings(parsedData))
            } else {
                val compiled = it.compileJS()
                compiled?.let { it ->
                    jsScriptCache[uuid] = it
                    it.eval(SimpleBindings(parsedData))
                }
            }
        }

        jexl?.let {
            devLog("Start evaling jexl script.")
            val uuid = it.generateUUID()
            val cache = jexlScriptCache[uuid]
            if (cache != null) {
                cache.eval(parsedData)
            } else {
                val compiled = prodJexlCompiler.compileToScript(it)
                compiled.let { it ->
                    jexlScriptCache[uuid] = it
                    it.eval(parsedData)
                }
            }
        }

        fluxon?.let {
            FluxonShell.invoke(it) {
                root.rootVariables += parsedData
            }
        }


    }
}

data class ItemData(
    val itemVal: Map<String, Any>? = mapOf(),
    val itemVar: Map<String, Any>? = mapOf(),
    val itemDynamic: Map<String, Any>? = mapOf(),
    val itemId: String? = null,
) {
    fun collect(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        itemVal?.let { map.putAll(it) }
        itemVar?.let { map.putAll(it) }
        itemDynamic?.let { map.putAll(it) }
        itemId?.let { map.put("_itemname", it) }
        return map
    }

}
