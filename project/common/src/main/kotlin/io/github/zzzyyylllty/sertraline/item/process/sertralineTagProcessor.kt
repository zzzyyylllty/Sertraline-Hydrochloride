package io.github.zzzyyylllty.sertraline.item.process

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.config.asListEnhanded
import io.github.zzzyyylllty.sertraline.function.data.getSavedData
import io.github.zzzyyylllty.sertraline.function.kether.evalKether
import io.github.zzzyyylllty.sertraline.listener.sertraline.tag.ProcessItemTagData
import io.papermc.paper.command.brigadier.argument.ArgumentTypes.player
import org.bukkit.entity.Player
import taboolib.module.nms.getItemTag

fun sertralineTagProcessor(data: ProcessItemTagData,player: Player?): ProcessItemTagData {
    val repl = data.repl.toMutableMap()
    val itemData = getSavedData(data.item, data.itemStack, false, player)
    val itemVal = itemData.itemVal
    val itemVar = itemData.itemVar
    val itemDynamic = itemData.itemDynamic
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
