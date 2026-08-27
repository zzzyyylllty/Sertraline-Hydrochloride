package io.github.zzzyyylllty.sertraline.debugMode

import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.Sertraline.devMode
import io.github.zzzyyylllty.sertraline.logger.debugS
import io.github.zzzyyylllty.sertraline.logger.debugSSync

fun devLog(input: String) {
    devLogBypassCheck { input }
}

/**
 * Lazy-evaluated devLog: the string is only constructed while devMode is enabled,
 * avoiding wasted string interpolation on hot paths even when devMode is off.
 * debugS already submits asynchronously, so no extra wrapper is added here.
 */
fun devLogBypassCheck(input: () -> String) {
    // Return early when devMode is off, to avoid needless string construction and log submission
    if (!devMode) return
    debugS(input())
}

fun devLogSync(input: String) {
    debugSSync(input)
}

fun devMode(b: Boolean) {
    devMode = b
}