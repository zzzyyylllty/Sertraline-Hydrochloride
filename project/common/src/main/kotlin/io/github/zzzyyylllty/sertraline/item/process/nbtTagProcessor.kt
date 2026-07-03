package io.github.zzzyyylllty.sertraline.item.process

import io.github.zzzyyylllty.sertraline.config.ConfigUtil
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.item.process.tag.ProcessItemTagData
import io.github.zzzyyylllty.sertraline.item.process.tag.processTagPrefix
import io.github.zzzyyylllty.sertraline.util.ItemTagUtil.parseMapNBT
import org.bukkit.entity.Player
import taboolib.module.nms.getItemTag

fun nbtTagProcessor(data: ProcessItemTagData, player: Player?, repl: Map<String, List<String>>, target: MutableMap<String, String?>) {

    val itemStack = data.itemStack ?: return
    val tag = itemStack.getItemTag(true)
    val tagMap: Map<String, Any?> = tag.parseMapNBT()

    repl["nbt"]?.let {
        processTagPrefix(
            prefix = "nbt",
            dataSourceEmptyCheck = { tagMap.isEmpty() },
            getReplaceValue = { parseResult, section, default, cleanedSection ->
                val value = cleanedSection?.let { location -> ConfigUtil.getDeep(tagMap, location) }
                devLog("nbt: $cleanedSection -> $value")
                value ?: default
            },
            repl = it,
            target = target
        )
    }
}
