package io.github.zzzyyylllty.sertraline.function.stats

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.data.Attribute
import io.github.zzzyyylllty.sertraline.data.AttributeSources.*
import io.github.zzzyyylllty.sertraline.data.DepazItems
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.function.item.getDepazItemNBTOrFail
import io.github.zzzyyylllty.sertraline.function.item.getDepazItemOrFail
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

/**
 * Refresh stat for player.
 *
fun Player.refreshStat() {
    val player = this
    val inv = player.inventory
    submitAsync {
        devLog("DEBUG_STAT_REFRESH", player.name)
        val slotEnabled = config.getBoolean("attribute.slot-condition")
        if (slotEnabled) for (slot in 0..40) {
            val i = inv.getItem(slot)
            if (i.isDepazItemInList()) {
                for (atb in i.getDepazItemNBTOrFail()) {
                    if (player.getSlots(atb.requireSlot).contains(slot)) player.applyAtb(atb)
                }
            }
        } else {
            val slotList : List<String> = (config.getStringList("attribute.require-enabled-slot") ?: listOf<String>("36","37","38","39","ANY_HAND"))
            for (slot in player.getSlots(slotList)) {
                val i = inv.getItem(slot)
                if (i.isDepazItemInList()) {
                    for (atb in i.getDepazItemNBTOrFail().attributes) {
                        if (player.getSlots(atb.requireSlot).contains(slot)) player.applyAtb(atb)
                    }
                }
            }
        }
    }
}

fun Player.applyAtb(attribute: Attribute) {
    val playerData: MMOPlayerData = MMOPlayerData.get(this)
    val statMap: StatMap = playerData.statMap
    val uuid = attribute.uuid
    val typedValue = relativeOrFlat(attribute.amount)
    when (attribute.type) {
        MYTHIC_LIB -> StatModifier(
            uuid,
            "sertraline",
            attribute.attr,
            typedValue.value.toDouble(),
            ModifierType.valueOf(typedValue.type.name),
            EquipmentSlot.valueOf(attribute.mythicLibEquipSlot),
            ModifierSource.valueOf(attribute.source)).register(playerData)
    }
}*/