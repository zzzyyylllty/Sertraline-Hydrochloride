package io.github.zzzyyylllty.sertraline.config

import io.github.zzzyyylllty.sertraline.config.asListEnhanded
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.util.loreformat.performPlaceholders
import io.github.zzzyyylllty.sertraline.util.minimessage.mmLegacyUtil
import io.github.zzzyyylllty.sertraline.util.minimessage.mmUtil
import io.github.zzzyyylllty.sertraline.util.minimessage.toComponent
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

public class AdapterUtil(val input: Map<String, Any?>?) {
    fun getString(location: String): String? {
        return input?.get(location)?.toString()
    }
    fun getTextComponent(location: String, sItem: ModernSItem, player: Player?): Component? {
        return (input?.get(location)?.toString())?.performPlaceholders(sItem, player)?.toComponent()
    }
    fun getTextComponentList(location: String, sItem: ModernSItem,player: Player?): List<Component>? {
        val get = input?.get(location) ?: return null
        val list = get as? List<*> ?: listOf(get.toString())
        val retList : MutableList<Component> = mutableListOf()
        list.asListEnhanded()?.forEach { retList.add(it.toString().performPlaceholders(sItem, player)!!.toComponent()) }
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
        val current = input?.get(list[0])
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
    val thisList = if (this is List<*>) this else listOf(this)
    val list = mutableListOf<String>()
    for (string in thisList) {
        if (string == null) continue
        list.addAll(string.toString().split("\n","<br>", ignoreCase = true))
    }
    if (list.last() == "") list.removeLast()
    return list
}
