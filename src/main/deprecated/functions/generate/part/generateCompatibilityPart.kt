package io.github.zzzyyylllty.functions.generate.part

import io.github.zzzyyylllty.data.SertralineItem
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.itemTagReader
import taboolib.platform.util.buildItem

fun generateCompatbilityPart(item: ItemStack,data: SertralineItem) : ItemStack {

    var returnItem = item
    val compData = data.compatibilityData ?: return returnItem
    buildItem(returnItem).itemTagReader {
        set("MMOITEMS_ITEM_TYPE", compData.mmoItemsComp.type)
        set("MMOITEMS_ITEM_ID", compData.mmoItemsComp.id)
        set("MMOITEMS_REVISION_ID", compData.mmoItemsComp.revid)
        set("REVISION_ID", compData.mmoItemsComp.revid)
        set("MMOITEMS_TIER", compData.mmoItemsComp.tier)

        write(buildItem(returnItem))
    }


    return returnItem
}
