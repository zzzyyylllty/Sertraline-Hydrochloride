package io.github.zzzyyylllty.sertraline.function.item

import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.data.DepazItems
import io.github.zzzyyylllty.sertraline.function.error.throwNPEWithMessage
import io.github.zzzyyylllty.sertraline.function.generate.getDisplayNameOrRegName
import io.github.zzzyyylllty.sertraline.function.internalMessage.sendInternalMessages
import io.github.zzzyyylllty.sertraline.logger.severeL
import io.lumine.mythic.bukkit.utils.adventure.nbt.TagStringIO
import io.lumine.mythic.bukkit.utils.shadows.nbt.MojangsonParser
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.getItemTag
import taboolib.module.nms.itemTagReader
import taboolib.platform.util.asLangText
import taboolib.platform.util.giveItem
import kotlin.collections.get

fun ItemStack?.getDepazItem(): DepazItems? {
    var id : String? = null
    this.itemTagReader {
        id = getString("SERTRALINE_ID")
    }
    return itemMap[id]
}
fun ItemStack?.getDepazItemOrFail(): DepazItems {
    var id : String? = null
    this.itemTagReader {
        id = getString("SERTRALINE_ID")
    }
    val item = itemMap[id]
    if (item == null) throwNPEWithMessage("ITEM_NOT_FOUND", id.toString())
    return item!!
}
fun ItemStack?.getDepazItemNBTOrFail(): String? {
    return this?.getItemTag()?.getDeep("SERTRALINE_DATA")?.toJsonSimplified()
}

fun ItemStack?.isDepazItem(): Boolean {
    var idExists = false
    this.itemTagReader {
        idExists = (getString("SERTRALINE_ID")?.isEmpty() == true)
    }
    return idExists
}

fun ItemStack?.isDepazItemInList(): Boolean {
    var id : String? = null
    this.itemTagReader {
        id = getString("SERTRALINE_ID")
    }
    return itemMap[id] != null
}