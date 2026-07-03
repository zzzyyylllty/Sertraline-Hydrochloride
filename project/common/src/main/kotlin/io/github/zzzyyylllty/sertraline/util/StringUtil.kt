package io.github.zzzyyylllty.sertraline.util

import java.util.Locale

private val loc: Locale by lazy { Locale.getDefault() }

fun String.toLowerCase(): String = lowercase(loc)
fun String.toUpperCase(): String = uppercase(loc)