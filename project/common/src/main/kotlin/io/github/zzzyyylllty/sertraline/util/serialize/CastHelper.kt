package io.github.zzzyyylllty.sertraline.util.serialize

object CastHelper {
    fun smartCast(input: String): Any? {
        return when {
            input.matches(Regex("^-?\\d{1,10}$")) -> {
                try {
                    input.toInt()
                } catch (e: NumberFormatException) {
                    input // 默认为 String
                }
            }
            input.matches(Regex("^-?\\d+$")) -> {
                try {
                    input.toLong()
                } catch (e: NumberFormatException) {
                    input // 默认为 String
                }
            }
            input.matches(Regex("^-?\\d+\\.\\d+$")) -> {
                try {
                    input.toDouble()
                } catch (e: NumberFormatException) {
                    input // 默认为 String
                }
            }
            input.matches(Regex("^(true|false)$", RegexOption.IGNORE_CASE)) -> input.toBoolean()
            else -> input // 默认为 String
        }
    }
}
