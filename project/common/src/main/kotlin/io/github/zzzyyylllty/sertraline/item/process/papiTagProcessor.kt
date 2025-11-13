package io.github.zzzyyylllty.sertraline.item.process

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.config.asListEnhanded
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.function.data.getSavedData
import io.github.zzzyyylllty.sertraline.function.kether.evalKether
import io.github.zzzyyylllty.sertraline.listener.sertraline.tag.ProcessItemTagData
import io.github.zzzyyylllty.sertraline.listener.sertraline.tag.processRawTagKey
import io.github.zzzyyylllty.sertraline.util.replacePlaceholderSafety
import io.papermc.paper.command.brigadier.argument.ArgumentTypes.player
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.entity.Player
import taboolib.module.nms.getItemTag
import taboolib.platform.compat.replacePlaceholder

fun papiTagProcessor(data: ProcessItemTagData,player: Player?): ProcessItemTagData {
    val repl = data.repl.toMutableMap()
    var json = data.itemJson
    if (config.getBoolean("tags.papi",true)) {
        val processlist = repl.filter { it.key.startsWith("papi:") }
        processlist.forEach {
            val orginial = it.key.processRawTagKey("papi:")
            val split = orginial.split("?:")
            val section = split.first()
            val default = if (split.size >= 2) split.last() else null
            val papi = "%$section%".replacePlaceholderSafety(player)
            val replace = if (papi == "%$section%" || papi == "null") default else papi
            if (replace != null) repl[it.key] = replace
        }
        devLog("Placeholder before: $json")
        json = json.replacePlaceholderSafety(player)
        devLog("Placeholder after: $json")
    }
    return data.copy(repl = repl, itemJson = json)
}
