package io.github.zzzyyylllty.sertraline.config

import io.github.zzzyyylllty.sertraline.util.serialize.parseToMap
import java.io.File

fun multiExtensionLoader(file: File): Map<String, Any?>? {

    val format = when (val extension = file.extension.toLowerCase()) {
        "yml" -> "yaml"
        "tml" -> "toml"
        else -> extension
    }
    return parseToMap(file.readText(), format)
}
