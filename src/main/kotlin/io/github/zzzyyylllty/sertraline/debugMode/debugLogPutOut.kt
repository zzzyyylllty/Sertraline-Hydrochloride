package io.github.zzzyyylllty.sertraline.debugMode

import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.Sertraline.devMode
import taboolib.common.PrimitiveIO
import taboolib.common.platform.function.info
import taboolib.common.platform.function.warning
import taboolib.module.lang.asLangText

fun devLog(node: String,vararg args: Any) {
    if (devMode) warning(console.asLangText(node,args))
}

fun devMode(b: Boolean) {
    devMode = b
}