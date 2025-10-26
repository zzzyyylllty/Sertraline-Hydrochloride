package io.github.zzzyyylllty.sertraline.debugMode

import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.Sertraline.devMode
import io.github.zzzyyylllty.sertraline.logger.debugS

fun devLog(input: String) {
    if (devMode) debugS(input)
}

fun devMode(b: Boolean) {
    devMode = b
}