package io.github.zzzyyylllty.sertraline.debugMode

import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.Sertraline.devMode
import io.github.zzzyyylllty.sertraline.logger.warningL
import taboolib.common.PrimitiveIO
import taboolib.common.platform.function.info
import taboolib.common.platform.function.warning
import taboolib.module.lang.asLangText

fun devLog(langNode: String, vararg args: Any) {
    if (devMode) warningL(langNode, args)
}

fun devMode(b: Boolean) {
    devMode = b
}