package io.github.zzzyyylllty.sertraline.attribute

import io.github.zzzyyylllty.chotentech.attribute.api.ChoTenAttributeAPI
import io.github.zzzyyylllty.chotentech.attribute.data.AttributeModifier
import io.github.zzzyyylllty.chotentech.attribute.data.AttributeSlot
import io.github.zzzyyylllty.chotentech.attribute.data.ModifierSource
import io.github.zzzyyylllty.chotentech.attribute.data.ModifierType
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.util.DependencyHelper
import io.github.zzzyyylllty.sertraline.util.serialize.generateUUID
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class ChotenAttributeProvider : AttributeProvider {

    override val name: String = "choten"

    private val appliedModifiers = ConcurrentHashMap<UUID, MutableSet<UUID>>()

    companion object {
        val available: Boolean by lazy {
            Bukkit.getPluginManager().getPlugin("ChoTenTech") != null
        }

        private val chotenFilter = setOf("id", "source", "slot", "allowed")

        private fun isSlotAllowed(actSource: String, allowed: List<String>): Boolean {
            return if (actSource.contains("hand")) {
                allowed.contains("hand") || allowed.contains(actSource)
            } else {
                allowed.contains(actSource)
            }
        }

        private fun parseModifierSource(raw: String?): ModifierSource {
            return raw?.let { runCatching { ModifierSource.valueOf(it.uppercase()) }.getOrNull() }
                ?: ModifierSource.ARMOR
        }

        private fun parseAttributeSlot(raw: String): AttributeSlot {
            return when (raw) {
                "mainhand" -> AttributeSlot.MAINHAND
                "offhand" -> AttributeSlot.OFFHAND
                "helmet" -> AttributeSlot.HEAD
                "chestplate" -> AttributeSlot.CHEST
                "leggings" -> AttributeSlot.LEGS
                "boots" -> AttributeSlot.FEET
                else -> AttributeSlot.OTHER
            }
        }

        private fun solveModifier(key: Any?, value: Any?): ChotenAttributeValue {
            val keyStr = key.toString()
            val str = value.toString()

            val modifierType = when (str.last()) {
                '%', 'c', 'm' -> ModifierType.PERCENT
                'a', 's' -> ModifierType.MULTIPLY
                else -> ModifierType.ADD
            }

            val numericValue = if (modifierType == ModifierType.ADD) {
                str.toDoubleOrNull() ?: 1.0
            } else {
                str.dropLast(1).toDoubleOrNull() ?: 1.0
            }

            return ChotenAttributeValue(keyStr, numericValue, modifierType)
        }

        private data class ChotenAttributeValue(
            val atbID: String,
            val atbValue: Double = 1.0,
            val atbType: ModifierType = ModifierType.ADD,
        )
    }

    override fun refreshAttributes(player: Player, itemList: Map<String, ItemBound>) {
        if (!available) return

        val api = ChoTenAttributeAPI.instance
        val playerUuid = player.uniqueId

        // 清除旧修饰符
        appliedModifiers[playerUuid]?.let { uuids ->
            for (modUuid in uuids) {
                api.removeModifier(playerUuid, modUuid)
            }
            uuids.clear()
        }

        val newModifiers = mutableSetOf<UUID>()

        itemList.forEach { (slot, itemBound) ->
            val sItem = itemBound.sItem
            val chotenData = sItem.data["choten"] as? Map<*, *> ?: return@forEach
            val chotenMutable = chotenData.toMutableMap()

            devLog("chotenData: $chotenData")

            val allowed = (chotenData["allowed"] as? List<String>)?.toMutableList() ?: mutableListOf()
            val normalizedAllowed = if (allowed.isEmpty()) {
                val autoState = when (val suffix = itemBound.bItem.type.name.lowercase().substringAfterLast("_")) {
                    "boots", "chestplate", "leggings", "helmet" -> suffix
                    else -> "mainhand"
                }
                mutableListOf(autoState)
            } else {
                allowed
            }

            if (!isSlotAllowed(slot, normalizedAllowed)) {
                devLog("Choten slot $slot not allowed, skipping.")
                return@forEach
            }

            val source = chotenData["source"]?.toString()?.let { parseModifierSource(it) }
                ?: when (slot) {
                    "mainhand" -> ModifierSource.MAINHAND
                    "offhand" -> ModifierSource.OFFHAND
                    else -> ModifierSource.ARMOR
                }

            val attributeSlot = parseAttributeSlot(slot)

            // 移除过滤字段
            chotenFilter.forEach { chotenMutable.remove(it) }

            chotenMutable.forEach { (key, value) ->
                val atb = solveModifier(key, value)
                val modifierUuid = (sItem.key + "_" + slot + "_" + atb.atbID).generateUUID()

                val modifier = AttributeModifier(
                    modifierUuid,
                    atb.atbID,
                    atb.atbValue,
                    atb.atbType,
                    source,
                    attributeSlot,
                    null
                )

                api.addModifier(playerUuid, modifier)
                newModifiers.add(modifierUuid)
            }
        }

        appliedModifiers.getOrPut(playerUuid) { mutableSetOf() }.addAll(newModifiers)
        api.updatePlayerAttributes(player)

        devLog("ChotenAttribute refresh complete for ${player.name}, applied ${newModifiers.size} modifiers")
    }
}
