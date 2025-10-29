package io.github.zzzyyylllty.sertraline.util.minimessage

import io.github.zzzyyylllty.sertraline.item.adapter.transferBooleanToByte
import net.kyori.adventure.text.Component
import kotlin.collections.component1
import kotlin.collections.component2

fun String.toComponent(): Component {
    return mmUtil.deserialize(this)
}

fun List<String>.toComponent(): List<Component> {
    return this.map { mmUtil.deserialize(it) }
}

fun Any?.serializeComponent(): Any? {
    val input = this
    return when (input) {
        is Map<*, *> -> input.map { (k, v) ->
            k.toString() to v.serializeComponent()
        }.toMap() // 直接使用 map 和 toMap

        is List<*> -> input.map { it.serializeComponent() } // 使用 map

        is String -> input.toComponent()

        else -> input
    }
}