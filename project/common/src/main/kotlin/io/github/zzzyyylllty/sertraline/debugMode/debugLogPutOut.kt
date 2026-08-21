package io.github.zzzyyylllty.sertraline.debugMode

import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.Sertraline.devMode
import io.github.zzzyyylllty.sertraline.logger.debugS
import io.github.zzzyyylllty.sertraline.logger.debugSSync

fun devLog(input: String) {
    devLogBypassCheck { input }
}

/**
 * 惰性求值版 devLog：字符串在 devMode 开启时才构造，
 * 避免热路径调用处即使 devMode 关闭也白白执行字符串插值。
 * debugS 自身已异步提交，这里不再额外包一层。
 */
fun devLogBypassCheck(input: () -> String) {
    // devMode 关闭时直接返回，避免无谓的字符串构造和日志提交
    if (!devMode) return
    debugS(input())
}

fun devLogSync(input: String) {
    debugSSync(input)
}

fun devMode(b: Boolean) {
    devMode = b
}