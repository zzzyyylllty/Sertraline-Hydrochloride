package io.github.zzzyyylllty.sertraline.debugMode

import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.Sertraline.devMode
import io.github.zzzyyylllty.sertraline.logger.debugS
import taboolib.common.platform.function.submitAsync

fun devLog(input: String) {
    submitAsync {
         if (devMode) debugS(input)
    }
}

fun devMode(b: Boolean) {
    devMode = b
}