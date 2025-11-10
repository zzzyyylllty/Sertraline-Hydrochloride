package io.github.zzzyyylllty.sertraline.item

import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.Sertraline.tagManager
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.data.deserializeSItem
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.getItemTag

fun itemSerializer(itemStack: ItemStack,player: Player): ModernSItem? {
    val item = itemMap[itemStack.getItemTag(true)["sertraline_id"]?.asString() ?: return null] ?: return null
    val json = item.serialize()
    return tagManager.processItem(json!!, item, itemStack, player).let { deserializeSItem(it) }
}
fun itemSerializer(id: String,player: Player): ModernSItem? {
    val item = itemMap[id] ?: return null
    val json = item.serialize()
    return tagManager.processItem(json!!, item, null, player).let { deserializeSItem(it) }
}