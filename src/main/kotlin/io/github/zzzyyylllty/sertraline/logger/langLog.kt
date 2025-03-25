package io.github.zzzyyylllty.sertraline.logger

import io.github.zzzyyylllty.sertraline.Sertraline.console
import taboolib.common.platform.function.info
import taboolib.common.platform.function.severe
import taboolib.common.platform.function.warning
import taboolib.module.lang.asLangText


fun infoL(node: String,vararg args: Any) {
    info(console.asLangText(node,args))
}
fun severeL(node: String,vararg args: Any) {
    severe(console.asLangText(node,args))
}
fun warningL(node: String,vararg args: Any) {
    warning(console.asLangText(node,args))
}