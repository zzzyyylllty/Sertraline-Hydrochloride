/*@file:RuntimeDependency(
    value = "com.beust:klaxon:5.5",
    test = "com.beust.klaxon.Klaxon"
)*/
package io.github.zzzyyylllty.sertraline.function.item

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.parseObject
import com.alibaba.fastjson2.toJSONString
import com.beust.klaxon.Klaxon
import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.data.AttributeInst
import io.github.zzzyyylllty.sertraline.data.DepazItemInst
import io.github.zzzyyylllty.sertraline.data.DepazItemUnsolvedInst
import io.github.zzzyyylllty.sertraline.data.DepazItems
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.function.kether.evalKether
import io.github.zzzyyylllty.sertraline.function.kether.evalKetherString
import io.github.zzzyyylllty.sertraline.logger.warningS
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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.commons.lang3.mutable.Mutable
import taboolib.module.lang.asLangText


fun DepazItemInst.buildItem() : ItemStack {

    val depaz = this

    item.itemTagReader {
        set("SERTRALINE_ID", depaz.id)
        set("SERTRALINE_ATTRIBUTE", depaz.attributes.toJSONString())
        set("SERTRALINE_DATA", data.toJSONString())
        write(item)
    }
    return item
}

// 未写入 NBT
fun DepazItems.buildInstance(p: Player) : DepazItemUnsolvedInst {

    val depaz = this
    val instAttrs = mutableListOf<AttributeInst>()
    val sender = p as CommandSender

    val mm = MiniMessage.miniMessage()
    val compLore : MutableList<Component> = mutableListOf()
    val legacy = LegacyComponentSerializer.legacyAmpersand()

    val solvedItem = resolveItemStack(originalItem.material, p)

    if (originalItem.materialLoreEnabled) solvedItem?.lore()?.forEach {
        compLore.add(it.decorationIfAbsent(TextDecoration.ITALIC,TextDecoration.State.FALSE))
    }
    originalItem.lore.forEach {
        val comp = mm.deserialize(legacy.serialize(legacy.deserialize(it.replace("§", "&"))))
        compLore.add(comp.decorationIfAbsent(TextDecoration.ITALIC,TextDecoration.State.FALSE))
    }

    val buildedItem = buildItem(solvedItem ?: ItemStack(Material.STONE)) {
        customModelData = originalItem.model
    }

    buildedItem.itemTagReader {
        for (nbtp in depaz.originalItem.nbt) {
            for (nbtSingle in nbtp) {
                set(nbtSingle.key, nbtSingle.value)
            }
        }
        write(buildedItem)
    }

    if (originalItem.name != null) {
        val meta = buildedItem.itemMeta
        val name = mm.deserialize("<white>${originalItem.name}")
            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
        meta.displayName(name)
        buildedItem.setItemMeta(meta)
    }



    buildedItem.lore(compLore)

    for (attr in depaz.attributeParts) {
        // Attribute Chance
        if (attr.chance.evalKether(sender).get().toString().toDouble() > random(0.0,99.9)) instAttrs.add(
            AttributeInst(
                attributeSources = attr.attributeSources,
                attr = attr.attr,
                definer = attr.definer,
                uuid = attr.uuid,
                source = attr.source,
                mythicLibEquipSlot = attr.mythicLibEquipSlot,
                requireSlot = attr.requireSlot
            )
        )
    }

    return DepazItemUnsolvedInst(
        id = depaz.id,
        item = buildedItem,
        attributes = instAttrs,
        data = data
    )
}


// 未写入 NBT
fun DepazItemUnsolvedInst.solveInst(p: Player) : DepazItemInst {

    var atbjson = this.attributes.toJSONString()
    devLog("encoded ATTRIBUTE json: $atbjson")

    var i = 0
    while (atbjson.contains("<kether:.?>".toRegex())) {
        i++
        atbjson = atbjson.replace("<kether:(.?)>".toRegex(), "$1".evalKetherString(p) ?: throw NullPointerException())
        if (i > 10) {
            warningS(console.asLangText("ITEM_ATTRIBUTE_LIMITED_KETHER"))
            break
        }
    }
    devLog("kethered json: $atbjson")
    val jsonUtils = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
        allowStructuredMapKeys = true
        allowSpecialFloatingPointValues = true
    }

    val atb = jsonUtils.decodeFromString<kotlin.collections.MutableList<AttributeInst>>(atbjson)
    //Klaxon().parse<MutableList<*>>(atbjson)

    val inst = DepazItemInst(
        id = this.id,
        item = this.item,
        attributes = atb,
        data = data
    )
    devLog("inst: $inst")
    return inst
}