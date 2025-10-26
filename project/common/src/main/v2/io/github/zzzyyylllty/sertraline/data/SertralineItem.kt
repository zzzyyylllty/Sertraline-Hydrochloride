package io.github.zzzyyylllty.sertraline.data

import io.github.zzzyyylllty.sertraline.function.kether.evalKether
import io.github.zzzyyylllty.sertraline.function.kether.evalKetherBoolean

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack

// SertralineItem.kt
data class SertralineItem(
    val minecraftItem: SertralineMaterial,
    val sertralineMeta: SertralineMeta,
    val customMeta: HashMap<String, Any?> = hashMapOf(),
)

data class SertralineMeta(
    val key: Key,
    val parent: Key? = null,
    val actions: List<Action>? = null,
    val data: HashMap<String, Any?> = hashMapOf(),
) {
    override fun toString(): String {
        return "SertralineMeta(key=$key, dataKeys=${data.keys})"
    }
}

data class Action(
    var trigger: String = "onRightClick",
    var condition: List<String>? = null,
    var kether: List<String>? = null,
    var javaScript: List<String>? = null

) {
    fun runAction(player: Player, data: HashMap<String,  Any?>, i: ItemStack?, e: Event?, sqlI: SertralineItem) {
        val parsedData = data.toMutableMap()
        parsedData["@SertralineItem"] = sqlI
        parsedData["@SertralineItemStack"] = i
        parsedData["@SertralineEvent"] = e
        if (condition?.evalKetherBoolean(player, parsedData) ?: true) {
            kether?.evalKether(player, parsedData)
            
        }
    }
}

data class SertralineMaterial(
    val material: String? = "STONE",
    val displayName: String? = null,
    val lore: List<String>? = null,
    val model: Int? = null,
    val nbt: HashMap<String, Any?>? = hashMapOf(),
    val extra: HashMap<String, Any?> = hashMapOf()
)

data class Key(
    val namespace: String = "UNKNOWN",
    val name: String = "UNKNOWN",
) {
    fun serialize(): String = "$namespace:$name"
}

fun deSerializeKey(s: String): Key {
    val list = s.split(":")
    return Key(list[0], list.getOrElse(1) { "UNKNOWN" })
}