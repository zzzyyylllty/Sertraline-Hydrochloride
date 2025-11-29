package io.github.zzzyyylllty.sertraline.util

import io.github.zzzyyylllty.sertraline.config.asListEnhanded
import io.github.zzzyyylllty.sertraline.data.Action
import java.math.BigDecimal
import kotlin.collections.component1
import kotlin.collections.component2

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