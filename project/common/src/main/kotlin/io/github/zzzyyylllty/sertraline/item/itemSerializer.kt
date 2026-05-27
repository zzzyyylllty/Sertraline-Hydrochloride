package io.github.zzzyyylllty.sertraline.item

import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.Sertraline.tagManager
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.data.deserializeSItem
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.getItemTag
import taboolib.platform.util.isAir

fun itemSerializer(itemStack: ItemStack,player: Player?): ModernSItem? {
    if (itemStack.isEmpty || itemStack.isAir) return null
    val item = itemMap[itemStack.getItemTag(true)["sertraline_id"]?.asString() ?: return null] ?: return null
    // 快速路径：无占位符且无动态内容时跳过序列化→处理→反序列化
    if (!item.hasPlaceholders && !item.hasDynamics) return item
    val json = item.serialize()
    return deserializeSItem(tagManager.processItem(json!!, item, itemStack, player))
}
fun itemSerializer(item: ModernSItem,player: Player?): ModernSItem {
    // 快速路径：无占位符且无动态内容时直接返回，无需序列化/反序列化
    if (!item.hasPlaceholders && !item.hasDynamics) return item
    val json = item.serialize()
    return deserializeSItem(tagManager.processItem(json!!, item, null, player))
}
fun itemSerializer(id: String,player: Player?): ModernSItem? {
    val item = itemMap[id] ?: return null
    // 快速路径：无占位符且无动态内容时直接返回
    if (!item.hasPlaceholders && !item.hasDynamics) return item
    val json = item.serialize()
    return deserializeSItem(tagManager.processItem(json!!, item, null, player))
}