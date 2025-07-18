package io.github.zzzyyylllty.sertraline.load

import io.github.zzzyyylllty.sertraline.data.Key

fun String.getKey(): Key {
    val line = this
    val split = line.split(":")
    return Key(
        split[0], split[1]
    )
}