package io.github.zzzyyylllty.sertraline.item.process

import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.item.process.tag.ProcessItemTagData
import io.github.zzzyyylllty.sertraline.item.process.tag.processTagPrefix
import io.github.zzzyyylllty.sertraline.util.replacePlaceholderSafety
import net.kyori.adventure.text.minimessage.translation.Argument.target
import org.bukkit.entity.Player

fun papiTagProcessor(data: ProcessItemTagData, player: Player?, repl: Map<String, List<String>>, target: MutableMap<String, String?>) {

    repl["papi"]?.let {
        processTagPrefix(
            prefix = "papi",
            dataSourceEmptyCheck = { false },
            getReplaceValue = { parseResult, section, default, cleanedSection ->
                val papi = "%$section%".replacePlaceholderSafety(player)
                devLog("papi: $papi")
                if (papi == "%$section%" || papi == "null") default else papi
            },
            repl = it,
            target = target
        )
    }
}
