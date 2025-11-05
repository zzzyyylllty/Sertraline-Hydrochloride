package io.github.zzzyyylllty.sertraline.item.adapter

import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.reflect.setComponent
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.giveItem
import java.util.function.Consumer

fun minecraftAdapter(item: ItemStack,sItem: ModernSItem,player: Player?): ItemStack {
    var item = item
    val filtered = sItem.data.filter {
        it.key.startsWith("minecraft:") && (it.value != null)
    }
    if (filtered.isEmpty()) return item
    filtered.forEach {
        item = item.setComponent(it.key, it.value!!)
    }
    devLog("Item: $item")
    return item
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