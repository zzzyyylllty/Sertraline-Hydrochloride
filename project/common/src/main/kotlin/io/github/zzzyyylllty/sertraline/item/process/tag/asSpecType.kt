package io.github.zzzyyylllty.sertraline.item.process.tag

import io.github.zzzyyylllty.sertraline.util.toBooleanTolerance
import kotlin.collections.iterator
import kotlin.math.roundToInt

/**
 * 解析结果的数据类
 * @param prefix As 之前的内容
 * @param asType As 后的两个字符
 * @param splitByValue SplitBy 后的字符串（如果存在）
 */
data class ParseResult(
    val prefix: String,
    val asType: String,
    val splitByValue: String?
)


fun parseSection(section: String): ParseResult? {
    val asIndex = section.indexOf("As")
    if (asIndex == -1 || asIndex + 4 > section.length) return null

    val prefix = section.substring(0, asIndex)
    val twoChars = section.substring(asIndex + 2, asIndex + 4)

    val splitByIndex = section.indexOf("SplitBy", asIndex + 4)
    val splitByValue = if (splitByIndex != -1) {
        section.substring(splitByIndex + "SplitBy".length)
    } else null

    return ParseResult(prefix, twoChars, splitByValue)
}

// 通用处理方法
fun processTagPrefix(
    prefix: String,
    dataSourceEmptyCheck: () -> Boolean,
    getReplaceValue: (ParseResult?, String?, String?, String?) -> Any?,
    repl: List<String>,
    target: MutableMap<String, String?>,
) {
    if (dataSourceEmptyCheck()) return

    val repl = repl

    repl.forEach { key ->
        val original = key.processRawTagKey()
        val split = original.split("?:")
        val section = split.first()
        val default = if (split.size >= 2) split.last() else null

        // 解析section，尝试识别As和SplitBy结构
        val parseResult = parseSection(section)

        val replaceValue = getReplaceValue(parseResult, section, default, parseResult?.prefix ?: section)

        // 调用传入的处理逻辑得到替换值
        val replace = if (replaceValue != default) {
            parseResult?.let { asSpecTypeSolved(replaceValue, it) } ?: replaceValue
        } else {
            replaceValue
        }

        if (replace != null) target["$prefix:${key}"] = replace.toString()
    }
}

/**
 * 将 Item 文本标签智能解析为对应类型
 *
 * @input 要转换的值
 * */
fun asSpecTypeSolved(input: Any?, parseResult: ParseResult): Any? {
    return run {
        val type = parseResult.asType
        val sep = (parseResult.splitByValue).let { if (it == "BLANK") "" else it ?: "," }
        if (input == null) null else {
            when (type) {
                "St" -> input.toString()
                "In" -> input.toString().toDoubleOrNull()?.roundToInt()
                "Do" -> input.toString().toDoubleOrNull()
                "Fl" -> input.toString().toFloatOrNull()
                "By" -> input.toString().toByteOrNull()
                "Lo" -> input.toString().toLongOrNull()
                "Bo" -> input.toBooleanTolerance()
                "Bd" -> input.toString().toBigDecimal()
                "Li" -> (input as? List<*>?)?.joinToString(sep)
                "Ma" -> {
                    val split = sep.split("`")
                    (input as? Map<*, *>?)?.joinToStringExtended(split[0], if (split.size > 1) split[1] else ",")
                }

                else -> input.toString()
            } ?: input.toString()
        }
    }
}
fun Map<*,*>.joinToStringExtended(splitEntry: String, separator: String = ","): String {
    var buildStr = ""
    for (entry in this) {
        buildStr = buildStr + entry.key + splitEntry + entry.value + separator
    }
    return buildStr.removeSuffix(separator)
}