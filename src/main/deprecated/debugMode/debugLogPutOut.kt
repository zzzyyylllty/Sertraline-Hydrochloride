package io.github.zzzyyylllty.debugMode

import io.github.zzzyyylllty.SertralineHydrochloride
import taboolib.common.PrimitiveIO
import taboolib.common.platform.function.warning

fun debugLog(vararg s: Any?) {
    if (SertralineHydrochloride.debug) s.filterNotNull().forEach { PrimitiveIO.debug(it) }
}

fun debugMode(b: Boolean) {
    SertralineHydrochloride.debug = b
}