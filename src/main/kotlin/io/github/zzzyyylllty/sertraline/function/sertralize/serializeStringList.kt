package io.github.zzzyyylllty.sertraline.function.sertralize

fun serializeStringList(input: Any?): List<String> {
    return if (input is ArrayList<*>?) {
        input as ArrayList<String>? ?: emptyList()
    } else {
        val list = (input as String).split("\n")
        list.subList(0, list.size - 1)
    }
}