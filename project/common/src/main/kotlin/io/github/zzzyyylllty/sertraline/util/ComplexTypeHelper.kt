package io.github.zzzyyylllty.sertraline.util

import io.github.zzzyyylllty.sertraline.config.asListEnhanded
import io.github.zzzyyylllty.sertraline.data.Action

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
                    it["kether"] as? List<String>?,
                    it["javascript"] as? List<String>?,
                    it["fluxon"] as? List<String>?,
                ))
            }
            actions[it.key as String] = list
        }
        return actions
    }
}