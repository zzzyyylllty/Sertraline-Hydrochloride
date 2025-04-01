package io.github.zzzyyylllty.sertraline.logger

import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.function.internalMessage.infoS
import io.github.zzzyyylllty.sertraline.function.internalMessage.severeS
import io.github.zzzyyylllty.sertraline.function.internalMessage.warningS
import taboolib.module.lang.asLangText


fun infoL(node: String,vararg args: Any) {
    infoS(console.asLangText(node,args))
}
fun severeL(node: String,vararg args: Any) {
    severeS(console.asLangText(node,args))
}
fun warningL(node: String,vararg args: Any) {
    warningS(console.asLangText(node,args))
}