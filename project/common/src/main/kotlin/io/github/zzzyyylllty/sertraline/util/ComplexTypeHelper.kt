package io.github.zzzyyylllty.sertraline.util

import com.google.common.base.Strings
import io.github.zzzyyylllty.sertraline.data.Action
import org.bukkit.Color
import com.cryptomorin.xseries.XItemStack
import java.util.*

class ComplexTypeHelper(val input: Any?) {
    fun getAsActions(): Map<String, List<Action>>? {
        val content = input as? Map<*,*> ?: return null

        val actions = LinkedHashMap<String, List<Action>>()
        content.forEach {
            val rawList = it.value as? List<Map<*,*>>?
            val list = mutableListOf<Action>()
            rawList?.forEach { it ->
                list.add(Action(
                    it["condition"] as? List<String>?,
                    it["async"] as? Boolean?,
                    it["kether"] as? List<String>?,
                    it["javascript"] as? String?,
                    it["jexl"] as? String?,
                    it["fluxon"] as? String?,
                    it["graaljs"] as? String?
                    // it["kotlinscript"] as? String?,
                ))
            }
            actions[it.key as String] = list
        }
        return actions
    }
    fun parseColor(): Color? {
        var str = input.toString()
        if (Strings.isNullOrEmpty(str)) return null
        val rgb = str!!.replace(" ", "").split(',')
        if (rgb.size == 3) {
            return Color.fromRGB(
                    rgb[0].toInt(),
                    rgb[1].toInt(),
                    rgb[2].toInt(),
            )
        }
        // If we read a number that starts with 0x, SnakeYAML has already converted it to base-10
        try {
            return Color.fromRGB(str.toInt())
        } catch (ignored: NumberFormatException) {
        }
        // Trim any prefix, parseInt only accepts digits
        if (str.startsWith("#")) {
            str = str.substring(1)
        }
        return try {
            Color.fromRGB(str.toInt(16))
        } catch (e: NumberFormatException) {
            null
        }
    }


}

fun Any.toBooleanTolerance(): Boolean {
    return when (this) {
        is Boolean -> this
        is Int -> this > 0
        is String -> this.lowercase() == "true" || this == "1"
        is Double -> this > 0.0
        is Float -> this > 0.0
        is Byte -> (this == 1.toByte())
        is Short -> this > 0
        is Long -> this > 0
        else -> this.toString().toBooleanStrictOrNull() ?: false
    }
}