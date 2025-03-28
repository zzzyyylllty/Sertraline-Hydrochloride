package io.github.zzzyyylllty.sertraline.function.item

import ink.ptms.chemdah.taboolib.common.util.random
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
import taboolib.module.nms.getItemTag
import taboolib.module.nms.itemTagReader
import taboolib.platform.util.asLangText
import taboolib.platform.util.giveItem
import kotlin.collections.get

fun DepazItemInst.buildItem() : ItemStack {
    val item = this.originalItem
    item.itemTagReader {
        val value = getString("自定义的节点.支持多节点", "默认值")
        set("自定义的节点.支持多节点", "新的值 + $value")
        // 收尾方法 写了才算写入物品 不然不会写入 减少操作可能出现的失误
        write(item)
    }
    return item
}

// 未写入 NBT
fun DepazItems.buildInstance(p: Player) : DepazItemInst {
    val depaz = this
    val instAttrs = mutableListOf<AttributeInst>()
    for (attr in depaz.attributes) {
        if (attr.chance > random(0.0,99.9)) instAttrs.add(
            AttributeInst(
                type = attr.type,
                attr = attr.attr,
                definer = attr.definer,
                uuid = attr.uuid,
                amount = attr.amount.evalKether(p as CommandSender).toString(),
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