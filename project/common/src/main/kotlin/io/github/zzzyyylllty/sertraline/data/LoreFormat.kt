package io.github.zzzyyylllty.sertraline.data

import io.github.zzzyyylllty.sertraline.function.kether.evalKether
import io.github.zzzyyylllty.sertraline.function.kether.evalKetherBoolean
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import taboolib.platform.type.BukkitProxyEvent
import javax.script.CompiledScript

//data class LoreFormat(
//    val settings: LoreSetting,
//    val elements: List<LoreElement> = emptyList()
//)

interface AbstractLoreFormat {
    val settings: LoreSetting
}

data class LoreFormat(
    override val settings: LoreSetting,
    val elements: List<LoreElement> = emptyList()
) : AbstractLoreFormat

data class SwitchableLoreFormat(
    override val settings: LoreSetting,
    val preVariables: LinkedHashMap<String, CompiledScript> = linkedMapOf(),
    val forks: List<SwitchFork> = listOf<SwitchFork>(),
    /**
     * 非 null 时按字段覆盖分支目标 settings：仅显式声明的字段生效，未声明字段沿用分支目标自身值；
     * null = 完全沿用分支目标 settings。
     */
    val settingsOverride: LoreSettingOverride? = null,
) : AbstractLoreFormat

/** switchable 根对分支目标 settings 的部分覆盖；null 字段表示不覆盖（沿用分支目标自身值） */
data class LoreSettingOverride(
    val overwrite: Boolean? = null,
    val visual: Boolean? = null,
    val skipBlank: Boolean? = null,
)

data class SwitchFork(
    val condition: CompiledScript,
    val toLoreFormat: AbstractLoreFormat,
)


data class LoreSetting(
    val overwrite: Boolean = true,
    val visual: Boolean = false,
    val skipBlank: Boolean = true
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