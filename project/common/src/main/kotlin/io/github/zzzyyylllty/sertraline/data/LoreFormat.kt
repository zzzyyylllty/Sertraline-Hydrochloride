package io.github.zzzyyylllty.sertraline.data

import io.github.zzzyyylllty.sertraline.function.kether.evalKether
import io.github.zzzyyylllty.sertraline.function.kether.evalKetherBoolean
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack

data class LoreFormat(
    val settings: LoreSetting,
    val elements: List<LoreElement> = emptyList()
)

data class LoreSetting(
    val overwrite: Boolean = true,
    val visual: Boolean = true
)

data class LoreElement(
    val content: String = "",
    val key: String?,
    val lineMode: LineMode? = LineMode.ANY,
    val lineRequire: List<String>? = listOf(),
)

enum class LineMode{
    ANY,
    ALL,
    NOT,
    NOT_ALL
}