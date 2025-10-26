package io.github.zzzyyylllty.sertraline.load

@SuppressWarnings("UNCHECKED_CAS")
fun serializeStringList(input: Any?): List<String> {
    return if (input is List<*>?) {
        input as List<String>? ?: emptyList()
    } else {
        val list = (input as String).split("\n")
        if (list.size - 1 > 0) list.subList(0, list.size - 1) else list
    }
}