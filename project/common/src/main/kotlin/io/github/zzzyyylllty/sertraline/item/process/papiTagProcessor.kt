package io.github.zzzyyylllty.sertraline.item.process

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.config.asListEnhanced
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

    processTagPrefix(
        prefix = "papi",
        jsonKey = "tags.papi",
        dataSourceEmptyCheck = { false },
        getReplaceValue = { parseResult, section, default, cleanedSection ->
            val papi = "%$section%".replacePlaceholderSafety(player)
            devLog("papi: $papi")
            if (papi == "%$section%" || papi == "null") default else papi
        },
        json = json,
        repl = repl
    )

    val regex = Regex("\"%([^%]+)%\"")
    json = (regex.replace(json) { matchResult ->
        "%${matchResult.groupValues[1]}%"
    }).replacePlaceholderSafety(player)

    return data.copy(repl = repl, itemJson = json)
}
