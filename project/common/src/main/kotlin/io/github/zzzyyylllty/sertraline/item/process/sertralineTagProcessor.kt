package io.github.zzzyyylllty.sertraline.item.process

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.config.asListEnhanded
import io.github.zzzyyylllty.sertraline.function.data.getSavedData
import io.github.zzzyyylllty.sertraline.function.kether.evalKether
import io.github.zzzyyylllty.sertraline.function.kether.parseKether
import io.github.zzzyyylllty.sertraline.listener.sertraline.tag.ProcessItemTagData
import io.github.zzzyyylllty.sertraline.listener.sertraline.tag.processRawTagKey
import io.papermc.paper.command.brigadier.argument.ArgumentTypes.player
import org.bukkit.entity.Player
import taboolib.module.kether.KetherFunction
import taboolib.module.kether.ScriptOptions
import taboolib.module.nms.getItemTag

fun sertralineTagProcessor(data: ProcessItemTagData,player: Player?): ProcessItemTagData {
    val repl = data.repl.toMutableMap()
    val itemData = getSavedData(data.item, data.itemStack, false, player)
    val itemVal = itemData.itemVal
    val itemVar = itemData.itemVar
    val itemDynamic = itemData.itemDynamic
    val collect = itemData.collect()
    var json = data.itemJson
    val name = data.item.key
    if (config.getBoolean("tags.kether",true)) {
        if (json.contains("kether:")) {
            val processlist = repl.filter { it.key.startsWith("kether:") }
            processlist.forEach {
                val orginial = it.key.processRawTagKey("val:")
                val split = orginial.split("?:")
                val section = split.first()
                val default = if (split.size >= 2) split.last() else null
                val replace = section.evalKether(data.player, collect, cacheId = "${name}_kether").get() ?: default
                if (replace != null) repl[it.key] = replace.toString()
            }
        }
    }
    if (config.getBoolean("tags.val",true) && itemVal?.isEmpty() == false && json.contains("val:")) {
        val processlist = repl.filter { it.key.startsWith("val:") }
        processlist.forEach {
            val orginial = it.key.processRawTagKey("val:")
            val split = orginial.split("?:")
            val section = split.first()
            val default = if (split.size >= 2) split.last() else null
            val replace = itemVal[section] ?: default
            if (replace != null) repl[it.key] = replace.toString()
        }
    }
    if (config.getBoolean("tags.var",true) && itemVar?.isEmpty() == false && json.contains("var:")) {
        val processlist = repl.filter { it.key.startsWith("var:") }
        processlist.forEach {
            val orginial = it.key.processRawTagKey("var:")
            val split = orginial.split("?:")
            val section = split.first()
            val default = if (split.size >= 2) split.last() else null
            val replace = (itemVar)[section] ?: default
            if (replace != null) repl[it.key] = replace.toString()
        }
    }
    if (config.getBoolean("tags.dynamic",true) && itemDynamic?.isEmpty() == false && json.contains("dynamic:")) {
        val processlist = repl.filter { it.key.startsWith("dynamic:") }
        processlist.forEach {
            val orginial = it.key.processRawTagKey("dynamic:")
            val split = orginial.split("?:")
            val section = split.first()
            val default = if (split.size >= 2) split.last() else null
            val replace = itemDynamic[section].asListEnhanded()?.evalKether(data.player, collect, cacheId = "${name}_dynamic")?.get() ?: default
            if (replace != null) repl[it.key] = replace.toString()
        }
    }
    return data.copy(repl = repl)
}
