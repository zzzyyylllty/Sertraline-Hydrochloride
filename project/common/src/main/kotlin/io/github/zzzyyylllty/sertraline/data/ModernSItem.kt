package io.github.zzzyyylllty.sertraline.data

import com.google.gson.reflect.TypeToken
import io.github.zzzyyylllty.sertraline.function.kether.evalKether
import io.github.zzzyyylllty.sertraline.function.kether.evalKetherBoolean
import io.github.zzzyyylllty.sertraline.util.jsonUtils
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Type

data class ModernSItem(
    val key: String,
    val data: Map<String, Any?> = mapOf(),
    val config: Map<String, Any?> = mapOf(),
) {
    fun serialize(): String? {
        return jsonUtils.toJson(this)
    }
}

fun deserializeSItem(string: String): ModernSItem? {
    return jsonUtils.fromJson(string, ModernSItem::class.java)
}

data class Action(
    var condition: List<String>? = null,
    var kether: List<String>? = null,
    var javaScript: List<String>? = null,
    var fluxon: List<String>? = null,
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