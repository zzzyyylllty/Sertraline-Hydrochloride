package io.github.zzzyyylllty.sertraline.function.item

import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.function.generate.getDisplayNameOrRegName
import io.github.zzzyyylllty.sertraline.function.internalMessage.sendInternalMessages
import io.github.zzzyyylllty.sertraline.logger.severeL
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.itemTagReader
import taboolib.platform.util.asLangText
import taboolib.platform.util.giveItem

fun ItemStack?.getDepazItem(): String? {
    var id : String? = null
    this.itemTagReader {
        id = getString("SERTRALINE_ID")
    }
    return id
}

fun ItemStack?.isDepazItem(): Boolean {
    var idExists = false
    this.itemTagReader {
        idExists = (getString("SERTRALINE_ID")?.isEmpty() == true)
    }
    return idExists
}