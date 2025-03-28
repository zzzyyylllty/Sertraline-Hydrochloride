package io.github.zzzyyylllty.sertraline.function.error

import io.github.zzzyyylllty.sertraline.logger.severeL

fun throwNPEWithMessage(node: String, vararg args: Any) {
    severeL(node, args)
    throw NullPointerException()
}
