package io.github.zzzyyylllty.sertraline.item.process

import io.github.zzzyyylllty.sertraline.config.asListEnhanced
import io.github.zzzyyylllty.sertraline.function.data.getSavedData
import io.github.zzzyyylllty.sertraline.function.kether.evalKether
import io.github.zzzyyylllty.sertraline.listener.sertraline.tag.ProcessItemTagData
import org.bukkit.entity.Player

fun sertralineTagProcessor(data: ProcessItemTagData,player: Player?): ProcessItemTagData {
    val repl = data.repl.toMutableMap()
    val itemData = getSavedData(data.item, data.itemStack, false, player)
    val itemVal = itemData.itemVal
    val itemVar = itemData.itemVar
    val itemDynamic = itemData.itemDynamic
    val collect = itemData.collect()
    val json = data.itemJson
    val name = data.item.key
    /*
    if (config.getBoolean("tags.kether",true)) {
        if (json.contains("kether:")) {
            val processlist = repl.filter { it.key.startsWith("kether:") }
            processlist.forEach {

                // 去除结尾的!!标记
                val orginial = it.key.processRawTagKey("val:")

                // 可空解析
                val split = orginial.split("?:")

                // 标准节，例如testAsISplitBy,
                val section = split.first()

                // 如果为空数值则使用以下默认值
                val default = if (split.size >= 2) split.last() else null

                // 处理标签
                val replace = section.evalKether(data.player, collect, cacheId = "${name}_kether").get() ?: default

                // 返回replace map
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
            val replace = itemDynamic[section].asListEnhanced()?.evalKether(data.player, collect, cacheId = "${name}_dynamic")?.get() ?: default
            if (replace != null) repl[it.key] = replace.toString()
        }
    }*/

    processTagPrefix(
        prefix = "kether",
        jsonKey = "tags.kether",
        dataSourceEmptyCheck = { false }, // kether业务没empty判定
        getReplaceValue = { parseResult, section, default, cleanedSection ->
            // 使用 parseResult 可以自定义业务逻辑
            // 例如给evalKether传递cacheId中带解析信息等
            cleanedSection.evalKether(data.player, collect, cacheId = "${name}_kether").get() ?: default
        },
        json = json,
        repl = repl
    )

    processTagPrefix(
        prefix = "val",
        jsonKey = "tags.val",
        dataSourceEmptyCheck = { itemVal?.isEmpty() ?: true },
        getReplaceValue = { parseResult, section, default, cleanedSection ->
            // 根据解析结果自定义逻辑，简化示例直接用itemVal查section
            itemVal?.get(cleanedSection) ?: default
        },
        json = json,
        repl = repl,
    )

    processTagPrefix(
        prefix = "var",
        jsonKey = "tags.var",
        dataSourceEmptyCheck = { itemVar?.isEmpty() ?: true },
        getReplaceValue = { parseResult, section, default, cleanedSection ->
            itemVar?.get(cleanedSection) ?: default
        },
        json = json,
        repl = repl,
    )

    processTagPrefix(
        prefix = "dynamic",
        jsonKey = "tags.dynamic",
        dataSourceEmptyCheck = { itemDynamic?.isEmpty() ?: true },
        getReplaceValue = { parseResult, section, default, cleanedSection ->
            itemDynamic?.get(cleanedSection)?.asListEnhanced()
                ?.evalKether(data.player, collect, cacheId = "${name}_dynamic")
                ?.get() ?: default
        },
        json = json,
        repl = repl,
    )
    return data.copy(repl = repl)
}
