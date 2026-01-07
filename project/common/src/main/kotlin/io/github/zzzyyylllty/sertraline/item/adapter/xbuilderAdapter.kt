package io.github.zzzyyylllty.sertraline.item.adapter

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.config.AdapterUtil
import io.github.zzzyyylllty.sertraline.config.asListEnhanced
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.util.ComplexTypeHelper
import io.github.zzzyyylllty.sertraline.util.loreformat.performPlaceholders
import io.github.zzzyyylllty.sertraline.util.minimessage.toComponent
import io.github.zzzyyylllty.sertraline.util.toBooleanTolerance
import net.kyori.adventure.text.Component
import org.bukkit.Color
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.components.CustomModelDataComponent
import com.cryptomorin.xseries.XItemStack
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
    val deserializedMeta = xDeserialized.itemMeta

    // 使用原始item的meta作为基础，合并反序列化meta的书名与附加属性（名字、lore等）
    val originalMeta = item.itemMeta

    val prefix = "xbuilder"
    val name = sItem.getDeepData("$prefix:name")?.toString()?.toComponent()
    val model = sItem.getDeepData("$prefix:item-model")?.toString()
    val lore = run {
        val get = sItem.getDeepData("$prefix:lore")
        val list = get.asListEnhanced() ?: return@run null
        val retList : MutableList<Component> = mutableListOf()
        list.asListEnhanced()?.forEach { it.performPlaceholders(sItem, player)?.toComponent()?.let { element -> retList.add(element) } }
        retList
    }

    // 这里优先用反序列化meta的内容，但保留原始meta的自定义内容
    if (deserializedMeta != null) {
        if (name != null) {
            deserializedMeta.displayName(name)
        }
        if (lore != null) {
            deserializedMeta.lore(lore)
        }
        if (model != null && config.getBoolean("fixes.xbuilder.reapply-item-model", true)) {
            deserializedMeta.itemModel = NamespacedKey.fromString(model)
        }
        item.itemMeta = deserializedMeta
    } else {
        // 如果没有反序列化meta，尽量保留原始item的Meta并更新显示名和lore
        if (name != null) originalMeta.displayName(name)
        if (lore != null) originalMeta.lore(lore)
        if (model != null && config.getBoolean("fixes.xbuilder.reapply-item-model", true)) originalMeta.itemModel = NamespacedKey.fromString(model)
        item.itemMeta = originalMeta
    }
    // 保持原始item的Material类型
    item.type = orgItemType

    // 返回修改过meta的ItemStack
    return item
}



