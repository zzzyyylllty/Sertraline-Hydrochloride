package io.github.zzzyyylllty.sertraline.util

import java.util.Locale

private val loc: Locale by lazy { Locale.getDefault() }

fun String.toLowerCase(): String = lowercase(loc)
fun String.toUpperCase(): String = uppercase(loc)

// ── Java 8 兼容文本工具 ──────────────────────────────────────────────
// 陷阱：String.isBlank/lines/repeat 是 Java 11 成员方法，JDK21 编译下裸调用解析到成员，
// 且 Kotlin 2.2 的 JVM 编译面无法以限定名（kotlin.text.isBlank 等）调用这些扩展，
// 故手写等价实现，两种构建（Java 21 / Java 8）行为一致。

/** Java 8 安全版 isBlank（空串或全空白） */
fun CharSequence.textIsBlank(): Boolean = all { it.isWhitespace() }

/** Java 8 安全版 lines()，与 kotlin.text.lines / java.lang.String.lines 行为一致（不含结尾空行） */
fun CharSequence.textLines(): List<String> {
    if (isEmpty()) return emptyList()
    val result = ArrayList<String>()
    var start = 0
    var i = 0
    while (i < length) {
        when (this[i]) {
            '\n' -> {
                result.add(subSequence(start, i).toString())
                start = i + 1
            }
            '\r' -> {
                result.add(subSequence(start, i).toString())
                start = i + 1
                if (start < length && this[start] == '\n') start++
            }
        }
        i++
    }
    if (start < length) result.add(subSequence(start, length).toString())
    return result
}

// NamespacedKey.fromString 是 1.16+ API，v11200（1.12.2）编译面不存在；手动解析（非法输入返回 null，语义同 fromString）
fun String.parseNamespacedKey(): org.bukkit.NamespacedKey? = try {
    val idx = indexOf(':')
    if (idx <= 0 || idx == length - 1) null
    else org.bukkit.NamespacedKey(substring(0, idx), substring(idx + 1))
} catch (_: IllegalArgumentException) {
    null
}