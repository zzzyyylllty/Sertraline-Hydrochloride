package io.github.zzzyyylllty.debugMode

import io.github.zzzyyylllty.SertralineHydrochloride
import taboolib.common.platform.function.warning

fun debugLog(s: String) {
    if (SertralineHydrochloride.debug) warning("[DEVMODE] $s")
}

fun debugMode(b: Boolean) {
    SertralineHydrochloride.debug = b
}