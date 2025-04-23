/*@file:RuntimeDependency(
    value = "com.beust:klaxon:5.5",
    test = "com.beust.klaxon.Klaxon"
)*/
package io.github.zzzyyylllty.sertraline.function.item

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.TypeReference
import com.alibaba.fastjson2.toJSONString
import com.willfp.eco.core.items.toSNBT
import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.data.AttributeInst
import io.github.zzzyyylllty.sertraline.data.DepazItemInst
import io.github.zzzyyylllty.sertraline.data.DepazItems
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.function.kether.evalKether
import io.github.zzzyyylllty.sertraline.function.kether.evalKetherBoolean
import io.github.zzzyyylllty.sertraline.function.kether.evalKetherString
import io.github.zzzyyylllty.sertraline.function.sertralize.AnySerializer
import io.github.zzzyyylllty.sertraline.logger.severeS
import io.github.zzzyyylllty.sertraline.logger.warningS
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
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
import kotlinx.serialization.json.Json
import pers.neige.neigeitems.utils.ItemUtils.getNbt
import pers.neige.neigeitems.utils.ItemUtils.getNbtOrNull
import pers.neige.neigeitems.utils.ItemUtils.toNbt
import taboolib.module.lang.asLangText
import taboolib.module.nms.ItemTagType
import taboolib.module.nms.getItemTag
import java.util.UUID
import kotlin.collections.toMutableList
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.modules.SerializersModule
import javax.management.Attribute


fun DepazItemInst.buildItem() : ItemStack {

    val depaz = this
//    Before1.1
//    item.itemTagReader {
//        set("SERTRALINE_ID", depaz.id)
//        set("SERTRALINE_ATTRIBUTE", depaz.attributes.toJSONString())
//
//        set("SERTRALINE_DATA", data.toJSONString())
//        write(item)
//    }

    // 1.1 Start

    val parsedList : MutableList<LinkedHashMap<String, Any>> = mutableListOf()
        attributes.forEach { attr ->
        val jsonStr = attr.toJSONString()
        parsedList.add(JSON.parseObject<LinkedHashMap<String, Any>>(jsonStr, object : TypeReference<LinkedHashMap<String, Any>>() {}) as LinkedHashMap<String, Any>)
        }



    val itemTag = item.getItemTag()
    itemTag.putDeep("SERTRALINE_ID", depaz.id)
    itemTag.putDeep("SERTRALINE_ATTRIBUTE", parsedList)

    itemTag.saveTo(item)
    /*
    item.itemTagReader {
        devLog((itemTag.get("SERTRALINE_ATTRIBUTE") as List<*> as List<AttributeInst>).toString())
    }
    */

    return item
}

fun DepazItems.buildInstance(p: Player) : DepazItemInst {

    val depaz = this.solvePlaceholders(p)
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
        val model = originalItem.model ?:(if (solvedItem?.itemMeta?.hasCustomModelData() == true) solvedItem?.itemMeta?.customModelData else null)
        if (model != null) customModelData = model
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
        if (attr.conditionOnBuild.evalKetherBoolean(p) && attr.chance.evalKether(sender).get().toString().toDouble() > random(0.0,99.9)) instAttrs.add(
            AttributeInst(
                attributeSources = attr.attributeSources,
                attr = attr.attr,
                definer = attr.definer,
                uuid = attr.uuid,
                source = attr.source,
                mythicLibEquipSlot = attr.mythicLibEquipSlot,
                requireSlot = attr.requireSlot.toMutableList(),
                conditionOnEffect = attr.conditionOnEffect
            )
        )
    }

    return DepazItemInst(
        id = depaz.id,
        item = buildedItem,
        attributes = instAttrs,
        data = data
    )
}


// 未写入 NBT
fun DepazItems.solvePlaceholders(p: Player, inputData: LinkedHashMap<String, Any>? = null) : DepazItems {

    devLog("solving $this")

    val jsonUtils = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
        allowStructuredMapKeys = true
        allowSpecialFloatingPointValues = true
    }
    val parsedData = inputData ?: this.data

    devLog("ParsedData: $parsedData InputData: $inputData")

    var json = this.toJSONString()

    devLog("encoded ATTRIBUTE json: $json")

    var i = 0
    while (json.contains("<kether:.+?>".toRegex())) {
        i++
        val pattern = "<kether:(.+?)>".toRegex()

        val found = pattern.findAll(json)

        found.forEach { f ->
            val m = f.value
            val section = m.substring(8..(m.length-2))
            devLog("Founded kether shell module $m , $section")
            section.evalKetherString(p)
        }
        if (i > 10) {
            warningS(console.asLangText("ITEM_ATTRIBUTE_LIMITED_KETHER"))
            break
        }
    }
    /*
    while (json.contains("<data:.+?>".toRegex())) {
        i++
        val pattern = "<data:(.+?)>".toRegex()

        val found = pattern.findAll(json)

        found.forEach { f ->
            val m = f.value
            val section = m.substring(6..(m.length-2))
            devLog("Founded data module $m , $section")
            json = json.replace("$m", (parsedData[section] as String? ?: run {
                severeS(console.asLangText("ITEM_DATA_NOT_FOUND",section))
                "null"
            }))
        }
        if (i > 10) {
            warningS(console.asLangText("ITEM_ATTRIBUTE_LIMITED_KETHER"))
            break
        }
    }

     */
    devLog("kethered json: $json")

    val inst = jsonUtils.decodeFromString<DepazItems>(json)

    devLog("inst: $inst")
    return inst
}