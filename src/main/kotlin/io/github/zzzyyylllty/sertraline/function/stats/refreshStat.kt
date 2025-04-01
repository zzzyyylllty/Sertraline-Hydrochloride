package io.github.zzzyyylllty.sertraline.function.stats

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.data.AttributeInst
import io.github.zzzyyylllty.sertraline.data.AttributeSources.*
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.function.item.getDepazItemInst
import io.github.zzzyyylllty.sertraline.function.item.getSlots
import io.github.zzzyyylllty.sertraline.function.item.isDepazItemInList
import io.github.zzzyyylllty.sertraline.function.item.relativeOrFlat
import io.lumine.mythic.lib.api.player.EquipmentSlot
import io.lumine.mythic.lib.api.player.MMOPlayerData
import io.lumine.mythic.lib.api.stat.StatMap
import io.lumine.mythic.lib.api.stat.modifier.StatModifier
import io.lumine.mythic.lib.player.modifier.ModifierSource
import io.lumine.mythic.lib.player.modifier.ModifierType
import org.bukkit.entity.Player
import taboolib.common.platform.function.submitAsync
import taboolib.module.lang.asLangText
import java.util.UUID

/**
 * Reapply stat for player.
 */

fun Player.refreshStat() {

}

fun Player.reapplyStat() {
    val player = this
    val inv = player.inventory
    submitAsync {
        devLog(console.asLangText("DEBUG_STAT_REFRESH", player.player?.name ?:"Unknown"))
        val slotEnabled = config.getBoolean("attribute.slot-condition")
        if (slotEnabled) for (slot in 0..40) {
            val i = inv.getItem(slot) ?: continue
            if (i.isDepazItemInList()) {
                for (atb in i.getDepazItemInst().attributes) {
                    if (player.getSlots(atb.requireSlot).contains(slot)) player.applyAtb(atb)
                }
            }
        } else {
            val slotList : List<String> = (config.getStringList("attribute.require-enabled-slot"))
            for (slot in player.getSlots(slotList)) {
                val i = inv.getItem(slot) ?: continue
                if (i.isDepazItemInList()) {
                    for (atb in i.getDepazItemInst().attributes) {
                        if (player.getSlots(atb.requireSlot).contains(slot)) player.applyAtb(atb)
                    }
                }
            }
        }
    }
}

fun Player.applyAtb(attribute: AttributeInst) {
    val playerData: MMOPlayerData = MMOPlayerData.get(this)
    val statMap: StatMap = playerData.statMap
    val uuid = UUID.fromString(attribute.uuid) ?: UUID.randomUUID()
    for (single in attribute.attr) {
        val typedValue = relativeOrFlat(single.value)
        when (attribute.type) {
            MYTHIC_LIB -> StatModifier(
                uuid,
                attribute.definer,
                single.key,
                typedValue.value.toDouble(),
                ModifierType.valueOf(typedValue.type.name),
                EquipmentSlot.valueOf(attribute.mythicLibEquipSlot),
                ModifierSource.valueOf(attribute.source)).register(playerData)
        }
    }

}