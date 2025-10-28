package io.github.zzzyyylllty.sertraline.config

import io.github.zzzyyylllty.sertraline.config.asListEnhanded
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.util.minimessage.mmLegacyUtil
import io.github.zzzyyylllty.sertraline.util.minimessage.mmUtil
import io.github.zzzyyylllty.sertraline.util.minimessage.toComponent
import net.kyori.adventure.text.Component

public class AdapterUtil(val input: Map<String, Any?>?) {
    fun getString(location: String): String? {
        return input?.get(location)?.toString()
    }
    fun getTextComponent(location: String): Component? {
        return (input?.get(location)?.toString())?.toComponent()
    }
    fun getTextComponentList(location: String): List<Component>? {
        val get = input?.get(location) ?: return null
        val list = get as? List<*> ?: listOf(get.toString())
        val retList : MutableList<Component> = mutableListOf()
        list.asListEnhanded()?.forEach { retList.add(it.toString().toComponent()) }
        return retList
    }
    fun getInt(location: String): Int? {
        return input?.get(location)?.toString()?.toInt()
    }

    fun getLong(location: String): Long? {
        return input?.get(location)?.toString()?.toLong()
    }
    fun getDeep(location: String): Any? {
        val list = location.split(".").toMutableList()
        devLog("ConfigUtil getDeep List: $list")
        val current = input?.get(list[0])
        devLog("ConfigUtil getDeep Current: $current")
        return if (current is Map<*, *> && list.size > 1) {
            list.removeFirst()
            getDeep(list.joinToString("."))
        } else {
            current
        }
    }
}

fun Any?.asListEnhanded() : List<String>? {
    if (this == null) return null
    val list = mutableListOf<String>()
    val thisList = if (this is List<*>) this else listOf(this)
    for (string in thisList) {
        if (string == null) continue
        list.addAll(string.toString().split("\n"))
    }
    if (list.last() == "") list.removeLast()
    return list
}

