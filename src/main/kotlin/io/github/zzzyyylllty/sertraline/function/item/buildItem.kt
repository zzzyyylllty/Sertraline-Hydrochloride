/*@file:RuntimeDependency(
    value = "com.beust:klaxon:5.5",
    test = "com.beust.klaxon.Klaxon"
)*/
package io.github.zzzyyylllty.sertraline.function.item

import com.alibaba.fastjson2.toJSONString
import io.github.zzzyyylllty.sertraline.data.AttributeInst
import io.github.zzzyyylllty.sertraline.data.DepazItemInst
import io.github.zzzyyylllty.sertraline.data.DepazItems
import io.github.zzzyyylllty.sertraline.function.kether.evalKether
import io.github.zzzyyylllty.sertraline.function.sertralize.serializeStringList
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.util.random
import taboolib.module.nms.itemTagReader
import taboolib.platform.util.buildItem


fun DepazItemInst.buildItem() : ItemStack {

    val depaz = this
    var list = mutableListOf<String>()

    for (atb in depaz.attributes) {
        list.add(atb.toJSONString())
    }

    item.itemTagReader {
        // val value = getString("自定义的节点.支持多节点", "默认值")
        set("SERTRALINE_ID", depaz.id)
        set("SERTRALINE_ATTRIBUTE", list)
        write(item)
    }
    return item
}

// 未写入 NBT
fun DepazItems.buildInstance(p: Player) : DepazItemInst {

    val depaz = this
    val instAttrs = mutableListOf<AttributeInst>()
    val sender = p as CommandSender

    val mm = MiniMessage.miniMessage()
    var compLore : MutableList<Component> = mutableListOf()
    val legacy = LegacyComponentSerializer.legacyAmpersand()

    originalItem.lore.forEach {
        val comp = mm.deserialize(legacy.serialize(legacy.deserialize(it.replace("§", "&"))))
        compLore.add(comp.decorationIfAbsent(TextDecoration.ITALIC,TextDecoration.State.FALSE))
    }

    val buildedItem = buildItem(resolveItemStack(originalItem.material, p) ?: ItemStack(Material.STONE)) {
        customModelData = originalItem.model
    }

    if (originalItem.name != null) {
        val meta = buildedItem.itemMeta
        var name = mm.deserialize("<white>${originalItem.name}")
            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
        meta.displayName(name)
        buildedItem.setItemMeta(meta)
    }

    buildedItem.lore(compLore)

    for (attr in depaz.attributeParts) {
        // Attribute Chance
        if (attr.chance.evalKether(sender).get().toString().toDouble() > random(0.0,99.9)) instAttrs.add(
            AttributeInst(
                type = attr.type,
                attr = attr.attr,
                definer = attr.definer,
                uuid = attr.uuid,
                source = attr.source,
                mythicLibEquipSlot = attr.mythicLibEquipSlot,
                requireSlot = attr.requireSlot
            )
        )
    }

    return DepazItemInst(
        id = depaz.id,
        item = buildedItem,
        attributes = instAttrs
    )
    
}