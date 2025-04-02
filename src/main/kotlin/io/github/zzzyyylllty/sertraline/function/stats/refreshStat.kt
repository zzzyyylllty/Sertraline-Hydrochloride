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
import taboolib.common.function.debounce
import taboolib.common.function.throttle
import taboolib.common.platform.function.submitAsync
import taboolib.module.lang.asLangText
import taboolib.platform.util.bukkitPlugin
import java.util.*
import kotlin.String

/**
 * Throttle Stat refresh.
 * wait 1000 ms / update
 *
 * https://taboolib.feishu.cn/wiki/C2oDwYpa7i9AyykaiKMc3PbNndu
 * */


val debounceRefreshStat = debounce<Player, List<String>>(config["attribute.debounce-time"] as Long? ?: 500) { player , slotinput ->
    player.refreshStat(slotinput)
}


/**
 * Reapply stat for player.
 */
fun Player.refreshStat(slotinput: List<String>) {

    val pl = this
    val playerData: MMOPlayerData = MMOPlayerData.get(this)
    submitAsync {
        val statMap = playerData.getStatMap()
        val slots = pl.getSlots(slotinput)
        for (slot in slots) {
            for (instance in statMap.instances) {
                instance.removeIf { key -> key.startsWith("sertraline") }
            }
        }
        player?.reapplyStat(slots)
    }
}

/**
* @param [slotinput] will affected slots
* */
fun Player.reapplyStat(slotinput: List<Int>) {
    val player = this
    val inv = player.inventory
    submitAsync {
        devLog(console.asLangText("DEBUG_STAT_REFRESH", player.player?.name ?:"Unknown"))
        val applySlot = slotinput
        val slotList = mutableListOf<Int>()
        devLog("SLOTS: ${getSlots(config.getStringList("attribute.require-enabled-slot"))}")
        devLog("INPUTSLOTS: ${applySlot}")
        for (singleApplySlot in getSlots(config.getStringList("attribute.require-enabled-slot"))) {
            if (applySlot.contains(singleApplySlot)) slotList.add(singleApplySlot)
        }
        for (slot in slotList) {
            val i = inv.getItem(slot) ?: continue
            if (i.isDepazItemInList()) {
                for (atb in i.getDepazItemInst().attributes) {
                    if (player.getSlots(atb.requireSlot).contains(slot)) player.applyAtb(atb, slot)
                }
            }
        }
    }
}

fun Player.applyAtb(attribute: AttributeInst, slot: Int) {
    devLog("APPLYING ATTRIBUTE $attribute")
    val playerData: MMOPlayerData = MMOPlayerData.get(this)
    val statMap: StatMap = playerData.statMap
    val uuid = UUID.fromString(attribute.uuid) ?: UUID.randomUUID()
    for (single in attribute.attr) {
        val typedValue = relativeOrFlat(single.value)
        when (attribute.type) {
            MYTHIC_LIB -> StatModifier(
                uuid,
                attribute.definer.replace("<slot>", slot.toString()),
                single.key,
                typedValue.value.toDouble(),
                ModifierType.valueOf(typedValue.type.name),
                EquipmentSlot.valueOf(attribute.mythicLibEquipSlot),
                ModifierSource.valueOf(attribute.source)).register(playerData)
        }
    }
}