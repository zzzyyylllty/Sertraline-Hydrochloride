package io.github.zzzyyylllty.sertraline.item

import io.github.zzzyyylllty.sertraline.Sertraline
import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.Sertraline.tagManager
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.data.deserializeSItem
import io.github.zzzyyylllty.sertraline.manager.PRIVATE_OWNER_TAG
import io.github.zzzyyylllty.sertraline.manager.isPrivateItemId
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.getItemTag
import taboolib.platform.util.isAir

fun itemSerializer(itemStack: ItemStack, player: Player?): ModernSItem? {
    if (itemStack.type == Material.AIR || itemStack.isAir) return null
    val item = itemTemplateForStack(itemStack, player) ?: return null
    // Fast path: skip serialization → processing → deserialization when there are no placeholders and no dynamic content
    if (!item.hasPlaceholders && !item.hasDynamics) return item
    val json = item.serialize()
    return deserializeSItem(tagManager.processItem(json!!, item, itemStack, player))
}

/**
 * Resolve the template represented by an ItemStack. Private items use the owner
 * marker when present, so rebuilding an item does not accidentally use the
 * current player's private template.
 */
internal fun itemTemplateForStack(itemStack: ItemStack, player: Player?): ModernSItem? {
    val id = itemStack.getItemTag(true)["sertraline_id"]?.asString() ?: return null
    if (!isPrivateItemId(id)) return itemMap[id]

    val owner = privateOwnerUuid(itemStack)
    val uuid = owner ?: player?.uniqueId?.toString() ?: return null
    return Sertraline.manager.privateManager.getItem(
        uuid,
        id,
        io.github.zzzyyylllty.sertraline.manager.SubManagerType.TEMPORARY
    ) ?: Sertraline.manager.privateManager.getItem(
        uuid,
        id,
        io.github.zzzyyylllty.sertraline.manager.SubManagerType.PERSISTENT
    )
}

internal fun privateOwnerUuid(itemStack: ItemStack): String? {
    return itemStack.getItemTag(true)[PRIVATE_OWNER_TAG]?.asString()?.trim()?.takeIf { it.isNotEmpty() }
}
fun itemSerializer(item: ModernSItem,player: Player?): ModernSItem {
    // Fast path: return directly when there are no placeholders and no dynamic content; no serialization/deserialization needed
    if (!item.hasPlaceholders && !item.hasDynamics) return item
    val context = item.getDeepData("sertraline:context")
    // context may contain runtime objects such as Player/Event, which Gson cannot safely round-trip;
    // strip it before serialization (item is always a deep copy on the build path), while tag processing still reads the full context from the item argument
    val json = if (context != null) {
        item.setDeepData("sertraline:context", null)
        try { item.serialize() } finally { item.setDeepData("sertraline:context", context) }
    } else item.serialize()
    val result = deserializeSItem(tagManager.processItem(json!!, item, null, player))
    // context is lost after the round-trip; reattach it (for downstream sertralineAdapter lore placeholders)
    if (context != null) result.setDeepData("sertraline:context", context)
    return result
}
fun itemSerializer(id: String,player: Player?): ModernSItem? {
    val item = itemMap[id] ?: return null
    // Fast path: return directly when there are no placeholders and no dynamic content
    if (!item.hasPlaceholders && !item.hasDynamics) return item
    val json = item.serialize()
    return deserializeSItem(tagManager.processItem(json!!, item, null, player))
}