package io.github.zzzyyylllty.sertraline.util

import java.util.Locale

val loc: Locale? by lazy { Locale.getDefault() }

fun String.toLowerCase(): String {
    return lowercase(loc ?: Locale.getDefault())
}
fun String.toUpperCase(): String {
    return uppercase(loc ?: Locale.getDefault())
}