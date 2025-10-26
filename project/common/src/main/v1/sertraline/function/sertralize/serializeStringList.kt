package io.github.zzzyyylllty.sertraline.function.sertralize

import kotlin.math.max
import kotlin.math.min

fun serializeStringList(input: Any?): List<String> {
    return if (input is ArrayList<*>?) {
        input as ArrayList<String>? ?: emptyList()
    } else {
        val list = (input as String).split("\n")
        if (list.size - 1 > 0) list.subList(0, list.size - 1) else list
    }
}