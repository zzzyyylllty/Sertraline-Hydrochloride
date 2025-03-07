package io.github.zzzyyylllty.functions.generate.part

import io.github.zzzyyylllty.data.SertralineItem
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.itemTagReader
import taboolib.platform.util.buildItem
import taboolib.platform.util.modifyLore


fun generateAttributePart(item: ItemStack,data: SertralineItem) : ItemStack {

    var returnItem = item
    val attrData = data.attribute
    val meta = item.itemMeta

    buildItem(returnItem).itemTagReader {
        set("DEPAZITEMS.ATTRIBUTES", attrData)

        write(buildItem(returnItem))
    }

    return returnItem
}
