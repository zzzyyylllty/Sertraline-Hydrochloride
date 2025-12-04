package io.github.zzzyyylllty.sertraline.item.adapter

import io.github.zzzyyylllty.sertraline.config.AdapterUtil
import io.github.zzzyyylllty.sertraline.config.asListEnhanded
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.util.ComplexTypeHelper
import io.github.zzzyyylllty.sertraline.util.loreformat.performPlaceholders
import io.github.zzzyyylllty.sertraline.util.minimessage.toComponent
import io.github.zzzyyylllty.sertraline.util.toBooleanTolerance
import net.kyori.adventure.text.Component
import org.bukkit.Color
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.components.CustomModelDataComponent
import taboolib.library.xseries.XItemStack
import kotlin.math.roundToInt

fun xbuilderAdapter(item: ItemStack, sItem: ModernSItem, player: Player?): ItemStack {

    val configMap = (sItem.config["xbuilder"] as Map<String, Any?>?)?.toMutableMap() ?: return item

    val orgItemType = item.type

    for (entry in XItemStack.serialize(item)) {
        configMap.let { it[entry.key] = entry.value }
    }

    // 反序列化得到的XItemStack包装类型（可能是Wrapped Bukkit ItemStack或者继承）
    val xDeserialized = configMap.let { XItemStack.deserialize(it) }

    // 从反序列化结果拿到ItemMeta，准备合并
    val deserializedMeta = xDeserialized?.itemMeta

    // 使用原始item的meta作为基础，合并反序列化meta的书名与附加属性（名字、lore等）
    val originalMeta = item.itemMeta

    val c = AdapterUtil(sItem.data)
    val prefix = "xbuilder"
    val name = sItem.getDeepData("$prefix:name")?.toString().performPlaceholders(sItem, player)?.toComponent()
    val lore = run {
        val get = sItem.getDeepData("$prefix:lore")
        val list = get as? List<*> ?: listOf(get.toString())
        val retList : MutableList<Component> = mutableListOf()
        list.asListEnhanded()?.forEach { retList.add(it.performPlaceholders(sItem, player)!!.toComponent()) }
        retList
    }

    // 这里优先用反序列化meta的内容，但保留原始meta的自定义内容
    if (deserializedMeta != null) {
        // 合并显示名
        if (name != null) {
            deserializedMeta.displayName(name)
        }
        // 合并lore
        if (lore != null) {
            deserializedMeta.lore(lore)
        }

        // 如果反序列化meta包含其他附加属性，需要合并进originalMeta
        // 这里示范你可以把自定义的NBT或者标签保留给originalMeta（读写或反序列化）
        // 示例如果你需要，可自行扩展

        // 将合并后的meta重新设回原始item
        item.itemMeta = deserializedMeta
    } else {
        // 如果没有反序列化meta，尽量保留原始item的Meta并更新显示名和lore
        if (name != null) originalMeta.displayName(name)
        if (lore != null) originalMeta.lore(lore)
        item.itemMeta = originalMeta
    }
    // 保持原始item的Material类型，避免因XItemStack.deserialize导致材料改变
    item.type = orgItemType

    // 这里直接返回修改过meta的原始ItemStack，避免中途丢失自定义组件
    return item
}



