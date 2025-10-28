package io.github.zzzyyylllty.sertraline.data

import io.github.zzzyyylllty.sertraline.function.kether.evalKether
import io.github.zzzyyylllty.sertraline.function.kether.evalKetherBoolean
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack

data class ModernSItem(
    val key: String,
    val data: Map<String, Any?> = mapOf(),
    val config: Map<String, Any?> = mapOf()
)

data class SertralineData(
    val type: String = "unidentified",
    val tier: String = "unidentified",
    val vals: LinkedHashMap<String, Any> = linkedMapOf(),
    val vars: LinkedHashMap<String, Any> = linkedMapOf(),
    val dynamics: LinkedHashMap<String, List<String>> = linkedMapOf(),
    val material: Material = Material.STONE
)

data class Action(
    var condition: List<String>? = null,
    var kether: List<String>? = null,
    var javaScript: List<String>? = null
) {
    fun runAction(player: Player, data: HashMap<String, Any?>, i: ItemStack?, e: Event?, sqlI: ModernSItem) {
        val parsedData = data.toMutableMap()
        parsedData["@SertralineItem"] = sqlI
        parsedData["@SertralineItemStack"] = i
        parsedData["@SertralineEvent"] = e
        if (condition?.evalKetherBoolean(player, parsedData) ?: true) {
            kether?.evalKether(player, parsedData)
        }
    }
}