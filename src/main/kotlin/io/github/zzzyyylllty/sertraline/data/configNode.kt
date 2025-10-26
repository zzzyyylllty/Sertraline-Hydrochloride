package io.github.zzzyyylllty.sertraline.data

import io.github.zzzyyylllty.sertraline.debugMode.devLog
import kotlin.collections.Map.Entry

data class ConfigNode(
    val isMandatory: Boolean = false,
    val node: String,
    val cleanedNode: String = node,
    val flags: LinkedHashMap<NodeTag, String?>,
    val reqType: List<String>,
)

fun entryToNode(key: String, value: Any?): ConfigNode {

    val node = ConfigNode(
        isMandatory = key.endsWith("*"),
        node = key,
        cleanedNode = nodeCleaner(key),
        flags = flagHelper(key),
        reqType = TODO()
    )
    return node
}

enum class NodeTag{
    POSITIVE,
    VERSION,
    PERCENTAGE,
    OPTIONAL
}

fun flagHelper(arg: String): LinkedHashMap<NodeTag, String?> {
    val regex = """\(([^)]*)\)""".toRegex()
    val matchResult = regex.find(arg)

    if (matchResult != null) {
        val contentInParentheses = matchResult.groupValues[1]
        devLog("Content in parent theses: $contentInParentheses")
        val items = contentInParentheses.split(",").map {
            it.trim()
        }
        val tags = LinkedHashMap<NodeTag, String?>()
        items.forEach {
            val split = it.split(":")
            if (split.size >= 2) {
                tags.put(nodeTagHelper(split[0]),split[1])
            }
            else tags.put(nodeTagHelper(split[0]),null)
        }
        return tags
    } else {
        devLog("No Config Node Tags found.")
        return linkedMapOf()
    }
}

fun nodeTagHelper(arg: String) : NodeTag{
    return when (arg.toUpperCase()) {
        "POSITIVE" -> NodeTag.POSITIVE
        "VERSION","VER" -> NodeTag.VERSION
        "PERCENTAGE" -> NodeTag.PERCENTAGE
        "OPTIONAL" -> NodeTag.OPTIONAL
        else -> throw IllegalArgumentException("node $arg not found.")
    }
}

fun nodeCleaner(arg: String): String{
    val withoutParentheses = arg.replaceFirst(Regex("""^\([^)]*\)"""), "")
    return withoutParentheses.replaceFirst(Regex("""\*$"""), "")

}