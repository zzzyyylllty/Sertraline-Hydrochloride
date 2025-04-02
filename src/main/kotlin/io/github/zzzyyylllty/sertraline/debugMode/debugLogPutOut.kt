package io.github.zzzyyylllty.sertraline.debugMode

import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.Sertraline.devMode
import io.github.zzzyyylllty.sertraline.logger.debugS
import io.github.zzzyyylllty.sertraline.logger.warningL
import taboolib.common.PrimitiveIO
import io.github.zzzyyylllty.sertraline.logger.infoS
import io.github.zzzyyylllty.sertraline.logger.warningS
import taboolib.module.lang.asLangText

fun devLog(input: String) {
    if (devMode) debugS(input)
}

fun devMode(b: Boolean) {
    devMode = b
}