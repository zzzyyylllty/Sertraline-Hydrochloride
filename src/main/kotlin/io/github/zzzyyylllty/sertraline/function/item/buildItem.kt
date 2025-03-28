/*@file:RuntimeDependency(
    value = "com.beust:klaxon:5.5",
    test = "com.beust.klaxon.Klaxon"
)*/
package io.github.zzzyyylllty.sertraline.function.item

import com.beust.klaxon.Klaxon
import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.data.AttributeInst
import io.github.zzzyyylllty.sertraline.data.DepazItemInst
import io.github.zzzyyylllty.sertraline.data.DepazItems
import io.github.zzzyyylllty.sertraline.function.kether.evalKether
import kotlinx.serialization.json.Json
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.env.RuntimeDependency
import taboolib.common.util.random
import taboolib.module.nms.itemTagReader
import java.util.UUID


fun DepazItemInst.buildItem() : ItemStack {
    val item = this.item
    val depaz = this
    item.itemTagReader {
        // val value = getString("自定义的节点.支持多节点", "默认值")
        set("SERTRALINE_ID", depaz.id)
        // set("SERTRALINE_DATA", Klaxon().toJsonString("test"))
        // set("SERTRALINE_DATA", Json.encodeToString(depaz))
        val jsonUtils = Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
            coerceInputValues = true
            encodeDefaults = true
            allowStructuredMapKeys = true
            allowSpecialFloatingPointValues = true
        }
        set("SERTRALINE_DATA", jsonUtils.encodeToString(DepazItemInst.serializer(), depaz))

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
        if (attr.chance.evalKether(sender).get().toString().toDouble() > random(0.0,99.9)) instAttrs.add(
            AttributeInst(
                type = attr.type,
                attr = attr.attr,
                definer = attr.definer,
                uuid = attr.uuid,
                amount =
                    if (config.getBoolean("attribute.kether-amount")) attr.amount.evalKether(sender).get().toString()
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
        item = depaz.originalItem,
        attributes = instAttrs
    )
    
}