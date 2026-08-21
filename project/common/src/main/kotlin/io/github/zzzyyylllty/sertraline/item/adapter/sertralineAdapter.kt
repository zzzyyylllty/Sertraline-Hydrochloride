package io.github.zzzyyylllty.sertraline.item.adapter

import io.github.zzzyyylllty.sertraline.compat.PlatformCompat
import io.github.zzzyyylllty.sertraline.config.AdapterUtil
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.util.loreformat.handleLoreFormat
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import com.cryptomorin.xseries.XItemStack
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag

fun sertralineAdapter(item: ItemStack, sItem: ModernSItem, player: Player?): ItemStack {

    val dataMap = (sItem.getDeepData("sertraline:vars") as Map<String, Any?>?)?.toMutableMap()
    val tag = item.getItemTag(true)
    // TODO: filterNbtSafe 注释待确认，当前 vars 不再包含 Player/Event（已独立为 context）
    tag["sertraline_data"] = dataMap
    //tag["sertraline_data"] = dataMap?.filterNbtSafe()

    // 写入类型到NBT
    val typeData = sItem.getDeepData("sertraline:type")
    if (typeData != null) {
        val typeId = when (typeData) {
            is io.github.zzzyyylllty.sertraline.data.Type -> typeData.id
            else -> typeData.toString()
        }
        tag["sertraline_type"] = typeId
    }

    // 写入品质到NBT
    val tierData = sItem.getDeepData("sertraline:tier")
    if (tierData != null) {
        val tierId = when (tierData) {
            is io.github.zzzyyylllty.sertraline.data.Tier -> tierData.id
            else -> tierData.toString()
        }
        tag["sertraline_tier"] = tierId
    }

    val item = item.setItemTag(tag, true)

    // lore format
    handleLoreFormat(sItem, player, PlatformCompat.getLore(item), false)?.let {
        PlatformCompat.setLore(item, it)
    }

    return item

}

/**
 * 过滤掉不可序列化为 NBT 的值，避免 ItemTag 写入时抛出 Unsupported nbt 异常。
 * 脚本/战利品 vars 中可能包含 Player、Event 等运行时对象，它们不需要持久化到物品 NBT。
 */
private fun Map<String, Any?>.filterNbtSafe(): Map<String, Any?> {
    return mapValues { (_, value) -> sanitizeNbtValue(value) }
        .filter { (_, value) -> value !== FILTERED_SENTINEL }
}

private val FILTERED_SENTINEL = Any()

private fun sanitizeNbtValue(value: Any?): Any? {
    when {
        value == null -> return null
        value is String -> return value
        value is Number -> return value
        value is Boolean -> return value
        value is ByteArray -> return value
        value is IntArray -> return value
        value is LongArray -> return value
        value is Map<*, *> -> {
            val sanitized = value.entries.associate { (k, v) ->
                k.toString() to sanitizeNbtValue(v)
            }.filter { (_, v) -> v !== FILTERED_SENTINEL }
            return sanitized
        }
        value is Collection<*> -> {
            val sanitized = value.map { sanitizeNbtValue(it) }.filter { it !== FILTERED_SENTINEL }
            return sanitized
        }
        value is Array<*> -> {
            val sanitized = value.map { sanitizeNbtValue(it) }.filter { it !== FILTERED_SENTINEL }
            return sanitized
        }
        value is Player -> return value.name
        // 其他不可序列化类型（Event、Entity 等）→ 丢弃
        else -> return FILTERED_SENTINEL
    }
}



