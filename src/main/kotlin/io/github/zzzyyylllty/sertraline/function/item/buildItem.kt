package io.github.zzzyyylllty.sertraline.function.item

import com.beust.klaxon.Klaxon
import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.data.AttributeInst
import io.github.zzzyyylllty.sertraline.data.DepazItemInst
import io.github.zzzyyylllty.sertraline.data.DepazItems
import io.github.zzzyyylllty.sertraline.function.error.throwNPEWithMessage
import io.github.zzzyyylllty.sertraline.function.generate.getDisplayNameOrRegName
import io.github.zzzyyylllty.sertraline.function.internalMessage.sendInternalMessages
import io.github.zzzyyylllty.sertraline.function.kether.evalKether
import io.github.zzzyyylllty.sertraline.logger.severeL
import io.lumine.mythic.bukkit.utils.adventure.nbt.TagStringIO
import io.lumine.mythic.bukkit.utils.shadows.nbt.MojangsonParser
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.util.random
import taboolib.module.nms.getItemTag
import taboolib.module.nms.itemTagReader
import taboolib.platform.util.asLangText
import taboolib.platform.util.giveItem
import kotlin.collections.get

fun DepazItemInst.buildItem() : ItemStack {
    val item = this.originalItem
    val depaz = this
    item.itemTagReader {
        val value = getString("自定义的节点.支持多节点", "默认值")
        set("SERTRALINE_ID", depaz.id)
        set("SERTRALINE_DATA", Klaxon().toJsonString(depaz))
        write(item)
    }
    return item
}

// 未写入 NBT
fun DepazItems.buildInstance(p: Player) : DepazItemInst {
    val depaz = this
    val instAttrs = mutableListOf<AttributeInst>()
    val sender = p as CommandSender
    for (attr in depaz.attributes) {
        // Attribute Chance
        if (attr.chance.evalKether(sender).toString().toDouble() > random(0.0,99.9)) instAttrs.add(
            AttributeInst(
                type = attr.type,
                attr = attr.attr,
                definer = attr.definer,
                uuid = attr.uuid,
                amount =
                    if (config.getBoolean("attribute.kether-amount")) attr.amount.evalKether(sender).toString()
                    else attr.amount
                ,
                source = attr.source,
                mythicLibEquipSlot = attr.mythicLibEquipSlot,
                requireSlot = attr.requireSlot
            )
        )
    }

    return DepazItemInst(
        id = depaz.id,
        originalItem = depaz.originalItem,
        attributes = instAttrs
    )
    
}