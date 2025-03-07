package io.github.zzzyyylllty.functions.generate.part

import de.tr7zw.nbtapi.NBT
import de.tr7zw.nbtapi.iface.ReadWriteNBT
import de.tr7zw.nbtapi.utils.DataFixerUtil
import ink.ptms.chemdah.taboolib.module.chat.colored
import io.github.zzzyyylllty.data.SertralineItem
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import taboolib.module.nms.itemTagReader
import taboolib.platform.util.buildItem
import taboolib.platform.util.modifyLore


fun generateSertralinePart(item: ItemStack,data: SertralineItem) : ItemStack {

    var returnItem = item
    val sertData = data.sertralineData

    // Material
    returnItem.type = Material.valueOf(sertData?.material ?: "STONE")

    buildItem(returnItem).modifyLore {
        sertData?.lore?.forEach { line ->
            add(line)
            // TODO REPLACE WITH VARS
        }
        colored()
    }

    buildItem(returnItem).itemTagReader {
        set("DEPAZITEMS.SERTRALINE.UPDATE_ID", sertData?.updateId)
        set("DEPAZITEMS.SERTRALINE.ID", sertData?.id)
        set("DEPAZITEMS.SERTRALINE.FIXED_DATA", sertData?.fixedData)
        set("DEPAZITEMS.SERTRALINE.VARIABLES_DATA", sertData?.variablesData)

        write(buildItem(returnItem))
    }

    return returnItem
}