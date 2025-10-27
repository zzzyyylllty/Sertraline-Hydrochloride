package io.github.zzzyyylllty.sertraline.util.serialize

inline fun <reified T> isListOfType(list: List<*>): Boolean {
    return list.all { it is T }
}