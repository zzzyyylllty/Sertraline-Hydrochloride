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
    val json = item.serialize()
    return deserializeSItem(tagManager.processItem(json!!, item, itemStack, player))
}
fun itemSerializer(item: ModernSItem,player: Player?): ModernSItem? {
    val json = item.serialize()
    return deserializeSItem(tagManager.processItem(json!!, item, null, player))
}
fun itemSerializer(id: String,player: Player?): ModernSItem? {
    val item = itemMap[id] ?: return null
    val json = item.serialize()
    return deserializeSItem(tagManager.processItem(json!!, item, null, player))
}