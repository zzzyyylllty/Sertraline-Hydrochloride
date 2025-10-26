package io.github.zzzyyylllty.sertraline.config

import io.github.zzzyyylllty.sertraline.util.serialize.parseToMap
import java.io.File

fun multiExtensionLoader(file: File): Map<String, Any?>? {

    val extension = file.extension.toLowerCase()
    val format = when (extension) {
        "yml" -> "yaml"
        else -> extension
    }
    return parseToMap(file.readText(), format)
}
