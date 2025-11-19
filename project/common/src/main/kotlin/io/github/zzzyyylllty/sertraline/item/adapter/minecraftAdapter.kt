package io.github.zzzyyylllty.sertraline.item.adapter

import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.impl.setComponentNMS
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.NMSItemTag.Companion.asBukkitCopy
import taboolib.module.nms.NMSItemTag.Companion.asNMSCopy

fun minecraftAdapter(item: ItemStack,sItem: ModernSItem,player: Player?): ItemStack {
    var item = item
    val filtered = sItem.data.filter {
        it.key.startsWith("minecraft:") && (it.value != null)
    }
    if (filtered.isEmpty()) return item
    var nmsItem = asNMSCopy(item)
    filtered.forEach {
        nmsItem.setComponentNMS(it.key, it.value!!)?.let { nmsItem = it }
    }
    return asBukkitCopy(nmsItem)
}

fun transferBooleanToByte(input: Any?): Any? {
    return when (input) {
        is Map<*, *> -> input.map { (k, v) ->
            k.toString() to transferBooleanToByte(v)
        }.toMap() // 直接使用 map 和 toMap

        is List<*> -> input.map { transferBooleanToByte(it) } // 使用 map

        is Boolean -> if (input) 1.toByte() else 0.toByte()

        else -> input
    }
}