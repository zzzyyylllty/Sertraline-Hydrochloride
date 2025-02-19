package io.github.zzzyyylllty.debugMode

import io.github.zzzyyylllty.DepazItems
import taboolib.common.platform.function.warning

fun debugLog(s: String) {
    if (DepazItems.debug) warning("[DEVMODE] $s")
}

fun debugMode(b: Boolean) {
    DepazItems.debug = b
}