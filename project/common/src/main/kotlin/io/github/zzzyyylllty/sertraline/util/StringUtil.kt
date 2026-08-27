package io.github.zzzyyylllty.sertraline.util

import java.util.Locale

private val loc: Locale by lazy { Locale.getDefault() }

fun String.toLowerCase(): String = lowercase(loc)
fun String.toUpperCase(): String = uppercase(loc)

// ── Java 8 compatible text utilities ──────────────────────────────────────────────
// Pitfall: String.isBlank/lines/repeat are Java 11 member methods; under JDK21 compilation a bare call resolves to the member,
// and Kotlin 2.2's JVM compilation surface cannot invoke these extensions by qualified name (kotlin.text.isBlank, etc.),
// so write equivalent implementations manually so both builds (Java 21 / Java 8) behave identically.

/** Java 8 safe isBlank (empty string or all whitespace) */
fun CharSequence.textIsBlank(): Boolean = all { it.isWhitespace() }

/** Java 8 safe lines(), consistent with kotlin.text.lines / java.lang.String.lines (without the trailing empty line) */
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

// NamespacedKey.fromString is a 1.16+ API and does not exist in the v11200 (1.12.2) compile surface; parse manually (returns null on invalid input, same semantics as fromString)
fun String.parseNamespacedKey(): org.bukkit.NamespacedKey? = try {
    val idx = indexOf(':')
    if (idx <= 0 || idx == length - 1) null
    else org.bukkit.NamespacedKey(substring(0, idx), substring(idx + 1))
} catch (_: IllegalArgumentException) {
    null
}