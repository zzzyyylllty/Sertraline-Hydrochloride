package io.github.zzzyyylllty.sertraline.util.dependencies

import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.item.itemSerializer
import io.github.zzzyyylllty.sertraline.listener.sertraline.mmoFilter
import io.github.zzzyyylllty.sertraline.util.DependencyHelper
import io.github.zzzyyylllty.sertraline.util.serialize.toUUID
import io.github.zzzyyylllty.sertraline.util.toLowerCase
import io.lumine.mythic.lib.api.player.EquipmentSlot
import io.lumine.mythic.lib.api.player.MMOPlayerData
import io.lumine.mythic.lib.api.stat.StatMap
import io.lumine.mythic.lib.api.stat.modifier.StatModifier
import io.lumine.mythic.lib.player.modifier.ModifierSource
import io.lumine.mythic.lib.player.modifier.ModifierType
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.submitAsync

//val mmoDataCacheMap = LinkedHashMap<String, MMOPlayerData>() // UUID, MMOPlayerData
//val mmoStatCacheMap = LinkedHashMap<String, StatMap>() // UUID, StatMap
//
//@SubscribeEvent
//fun mmoAttributeInit(e: PlayerJoinEvent) {
//    submitAsync {
//        if (!DependencyHelper().isPluginInstalled("MythicLib")) return@submitAsync
//        val playerData = MMOPlayerData(e.player.uniqueId)
//        mmoDataCacheMap[e.player.uniqueId.toString()] = playerData
//        mmoStatCacheMap[e.player.uniqueId.toString()] = playerData.statMap
//    }
//}
//
//@SubscribeEvent
//fun mmoAttributeRelease(e: PlayerQuitEvent) {
//    submitAsync {
//        if (!DependencyHelper().isPluginInstalled("MythicLib")) return@submitAsync
//        mmoDataCacheMap.remove(e.player.uniqueId.toString())
//        mmoStatCacheMap.remove(e.player.uniqueId.toString())
//    }
//}
//@SubscribeEvent
//fun mmoAttributeReleaseKick(e: PlayerKickEvent) {
//    submitAsync {
//        if (!DependencyHelper().isPluginInstalled("MythicLib")) return@submitAsync
//        mmoDataCacheMap.remove(e.player.uniqueId.toString())
//        mmoStatCacheMap.remove(e.player.uniqueId.toString())
//    }
//}

fun refreshMMOAttribute(player: Player) {
    submitAsync {

        val playerData = MMOPlayerData(player.uniqueId)
        playerData.statMap.instances.forEach {
            it.removeIf("SertralineItem"::equals)
        }

        if (!DependencyHelper().isPluginInstalled("MythicLib")) return@submitAsync
        val inv = player.inventory

        // 主手和副手
        inv.itemInMainHand.let { bItem -> itemSerializer(bItem, player)?.let { mmoAttributeCalculate(it, player, ModifierSource.MAINHAND_ITEM, EquipmentSlot.MAIN_HAND, "mainhand", bItem.type.name) } }
        inv.itemInOffHand.let { bItem -> itemSerializer(bItem, player)?.let { mmoAttributeCalculate(it, player, ModifierSource.OFFHAND_ITEM, EquipmentSlot.OFF_HAND, "offhand", bItem.type.name) } }

        // 护甲
        inv.helmet?.let { bItem -> itemSerializer(bItem, player)?.let { mmoAttributeCalculate(it, player, ModifierSource.ARMOR, EquipmentSlot.HEAD, "helmet", bItem.type.name) } }
        inv.chestplate?.let { bItem -> itemSerializer(bItem, player)?.let { mmoAttributeCalculate(it, player, ModifierSource.ARMOR, EquipmentSlot.CHEST, "chestplate", bItem.type.name) } }
        inv.leggings?.let { bItem -> itemSerializer(bItem, player)?.let { mmoAttributeCalculate(it, player, ModifierSource.ARMOR, EquipmentSlot.LEGS, "leggings", bItem.type.name) } }
        inv.boots?.let { bItem -> itemSerializer(bItem, player)?.let { mmoAttributeCalculate(it, player, ModifierSource.ARMOR, EquipmentSlot.FEET, "boots", bItem.type.name) } }
    }
}



fun mmoAttributeCalculate(item: ModernSItem, player: Player, defSource: ModifierSource, defslot: EquipmentSlot, actSource: String, bItemMat: String, async: Boolean = true) {
    submit(async = async) {
        if (!DependencyHelper().isPluginInstalled("MythicLib")) return@submit


        val playerData = MMOPlayerData(player.uniqueId)

        playerData.let { cache ->
            // val mmoData = itemCache[item.key]?.get("mmo") as Map<String, Any?>? ?: item.data.filter { (key, value) -> key.startsWith("mmo:") }
            val mmoData = item.data.filter { (key, value) -> key.startsWith("mmo:") }.toMutableMap()

            devLog("mmoData: $mmoData")

            val allowed = (mmoData["mmo:allowed"] as? List<String>? ?: return@submit).toMutableList() // 由于做了特殊处理，这里为null只能是没有mmo相关的选项。直接返回

            val bItemMat = bItemMat.toLowerCase()

            if (allowed.isEmpty()) {
                // 自动判断
                val autoState =
                    if (bItemMat.endsWith("boots")) "boots"
                    else if (bItemMat.endsWith("chestplate")) "chestplate"
                    else if (bItemMat.endsWith("leggings")) "leggings"
                    else if (bItemMat.endsWith("helmet")) "helmet"
                    else "hand"
                allowed.add(autoState)
            }

            if (actSource.contains("hand") && allowed.contains("hand")) {
                // 对hand做特殊处理，如果符合就跳过判断
            } else {
                if (!allowed.contains(actSource)) {
                    devLog("actSource: $actSource not match. skipping attribute loading.")
                    return@submit
                }
            }


            val uuid = (mmoData["mmo:uuid"] ?: return@submit).toString().toUUID()
            val slot = mmoData["mmo:slot"]?.let { EquipmentSlot.valueOf(it.toString()) } ?: defslot
            val source = mmoData["mmo:source"]?.let { ModifierSource.valueOf(it.toString()) } ?: defSource

            for (key in mmoFilter) {
                mmoData.remove("mmo:$key")
            }

            for (data in mmoData) {
                val atb = solveStatModifier(data)
                devLog("atb modifier: $atb")
                val modifier = StatModifier(
                    uuid,
                    "SertralineItem",
                    atb.atbID,
                    atb.atbValue,
                    atb.atbType,
                    slot,
                    source,
                )

                devLog("registering modifier: $modifier")
                modifier.register(cache)
            }
        }
    }
}

fun solveStatModifier(input: Map.Entry<String, Any?>): MMOAttributeValue {

    val str = input.value.toString()

    val type = when (str.last()) {
        '%', 'c', 'm' -> ModifierType.RELATIVE
        'a', 's' -> ModifierType.ADDITIVE_MULTIPLIER
        else -> return MMOAttributeValue(
            input.key.removePrefix("mmo:"),
            str.toDoubleOrNull() ?: 1.0,
            ModifierType.FLAT
        )
    }

    return MMOAttributeValue(
        input.key.removePrefix("mmo:"),
        str.dropLast(1).toDoubleOrNull() ?: 1.0,
        type
    )
}

data class MMOAttributeValue(
    val atbID: String,
    val atbValue: Double = 1.0,
    val atbType: ModifierType = ModifierType.FLAT,
)