package io.github.zzzyyylllty.sertraline.item.adapter

import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.impl.removeComponentNMS
import io.github.zzzyyylllty.sertraline.impl.setComponentNMS
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.NMSItemTag.Companion.asBukkitCopy
import taboolib.module.nms.NMSItemTag.Companion.asNMSCopy

fun minecraftAdapter(item: ItemStack,sItem: ModernSItem,player: Player?): ItemStack {
    val item = item
    val map = (sItem.getMajorData("minecraft") as? Map<String, Any?>) ?: return item
    if (map.isEmpty()) return item
    var nmsItem = asNMSCopy(item)

    devLog("sItem: $sItem")
    devLog("map: $map")
    map.forEach { (key, value) ->
        if (value != null) {
            devLog("Setting $key component.")
            nmsItem.setComponentNMS(key, value)?.let { nmsItem = it }
        } else {
            devLog("DataComponent $key is null. now removing component.")
            nmsItem.removeComponentNMS(key)
        }
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