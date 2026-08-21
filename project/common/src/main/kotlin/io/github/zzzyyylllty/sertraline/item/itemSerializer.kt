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
    // 快速路径：无占位符且无动态内容时跳过序列化→处理→反序列化
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
    // 快速路径：无占位符且无动态内容时直接返回，无需序列化/反序列化
    if (!item.hasPlaceholders && !item.hasDynamics) return item
    val context = item.getDeepData("sertraline:context")
    // context 可能含 Player/Event 等运行时对象，Gson 无法安全 round-trip；
    // 序列化前剥离（item 在构建路径总是 deep copy），标签处理仍从 item 参数读取完整 context
    val json = if (context != null) {
        item.setDeepData("sertraline:context", null)
        try { item.serialize() } finally { item.setDeepData("sertraline:context", context) }
    } else item.serialize()
    val result = deserializeSItem(tagManager.processItem(json!!, item, null, player))
    // round-trip 后 context 丢失，重新挂回（供下游 sertralineAdapter 的 lore 占位符使用）
    if (context != null) result.setDeepData("sertraline:context", context)
    return result
}
fun itemSerializer(id: String,player: Player?): ModernSItem? {
    val item = itemMap[id] ?: return null
    // 快速路径：无占位符且无动态内容时直接返回
    if (!item.hasPlaceholders && !item.hasDynamics) return item
    val json = item.serialize()
    return deserializeSItem(tagManager.processItem(json!!, item, null, player))
}