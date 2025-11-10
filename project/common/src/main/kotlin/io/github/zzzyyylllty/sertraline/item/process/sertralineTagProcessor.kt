package io.github.zzzyyylllty.sertraline.item.process

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.config.asListEnhanded
import io.github.zzzyyylllty.sertraline.function.kether.evalKether
import io.github.zzzyyylllty.sertraline.listener.sertraline.tag.ProcessItemTagData
import taboolib.module.nms.getItemTag

fun sertralineTagProcessor(data: ProcessItemTagData): ProcessItemTagData {
    val repl = data.repl.toMutableMap()
    val itemVal = data.item.data["sertraline:vals"] as Map<*,*>?
    val itemVar = (data.itemStack?.getItemTag(true)["sertraline_data"] ?: data.item.data["sertraline:vars"]) as Map<*,*>?
    val itemDynamic = data.item.data["sertraline:dynamics"] as Map<*,*>?
    if (config.getBoolean("tags.val",true) && itemVal?.isEmpty() == false) {
        val processlist = repl.filter { it.key.startsWith("val:") }
        processlist.forEach {
            val orginial = it.key.removePrefix("val:")
            val split = orginial.split("?:")
            val section = split.first()
            val default = if (split.size >= 2) split.last() else null
            val replace = itemVal[section] ?: default
            if (replace != null) repl[orginial] = replace.toString()
        }
    }
    if (config.getBoolean("tags.var",true) && itemVar?.isEmpty() == false) {
        val processlist = repl.filter { it.key.startsWith("var:") }
        processlist.forEach {
            val orginial = it.key.removePrefix("var:")
            val split = orginial.split("?:")
            val section = split.first()
            val default = if (split.size >= 2) split.last() else null
            val replace = (itemVar)[section] ?: default
            if (replace != null) repl[orginial] = replace.toString()
        }
    }
    if (config.getBoolean("tags.dynamic",true) && itemDynamic?.isEmpty() == false) {
        val processlist = repl.filter { it.key.startsWith("dynamic:") }
        processlist.forEach {
            val orginial = it.key.removePrefix("dynamic:")
            val split = orginial.split("?:")
            val section = split.first()
            val default = if (split.size >= 2) split.last() else null
            val replace = itemDynamic[section].asListEnhanded()?.evalKether(data.player)?.get() ?: default
            if (replace != null) repl[orginial] = replace.toString()
        }
    }
    return data.copy(repl = repl)
}
