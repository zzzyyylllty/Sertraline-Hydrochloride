package io.github.zzzyyylllty.sertraline.util

import io.github.zzzyyylllty.sertraline.config.asListEnhanded
import io.github.zzzyyylllty.sertraline.data.Action

class ComplexTypeHelper(val input: Any?) {
    fun getAsActions(): LinkedHashMap<String, List<Action>> {
        val content = input as Map<*,*>

        val actions = LinkedHashMap<String, List<Action>>()
        content.forEach {
            val rawList = it.value as List<LinkedHashMap<*,*>>
            val list = mutableListOf<Action>()
            rawList.forEach { it ->
                list.add(Action(
                    it["condition"] as List<String>,
                    it["kether"] as List<String>,
                    it["javaScript"] as List<String>,
                    it["fluxon"] as List<String>,
                ))
            }
            actions[it.key as String] = list
        }
        return actions
    }
}