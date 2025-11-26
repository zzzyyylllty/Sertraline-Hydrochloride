package io.github.zzzyyylllty.sertraline.util.dependencies

import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.item.itemSerializer
import io.github.zzzyyylllty.sertraline.listener.sertraline.mmoFilter
import io.github.zzzyyylllty.sertraline.util.DependencyHelper
import io.github.zzzyyylllty.sertraline.util.serialize.generateUUID
import io.github.zzzyyylllty.sertraline.util.serialize.toUUID
import io.github.zzzyyylllty.sertraline.util.toLowerCase
import io.lumine.mythic.lib.MythicLib
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
//
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


object MMOUtil {

//    fun register() {
//        MythicLib.plugin.stats.registerStat()
//    }


    fun mmoAttributeCalculate(
        item: ModernSItem,
        playerData: MMOPlayerData,
        defSource: ModifierSource,
        defslot: EquipmentSlot,
        actSource: String,
        bItemMat: String,
        async: Boolean = true
    ) {
        submit(async = async) {

            playerData.let { cache ->
                val mmoData = item.data.filter { (key, value) -> key.startsWith("mmo:") }.toMutableMap()

                devLog("mmoData: $mmoData") // 添加日志

                val allowed = (mmoData["mmo:allowed"] as? List<String>? ?: return@submit).toMutableList()

                val bItemMat = bItemMat.toLowerCase()

                if (allowed.isEmpty()) {
                    val suffix = bItemMat.split("_").last()
                    val autoState =
                        when (suffix) {
                            "boots" -> "boots"
                            "chestplate" -> "chestplate"
                            "leggings" -> "leggings"
                            "helmet" -> "helmet"
                            else -> "hand"
                        }
                    allowed.add(autoState)
                }

                if (actSource.contains("hand") && allowed.contains("hand")) {

                } else {
                    if (!allowed.contains(actSource)) {
                        devLog("actSource: $actSource not match. skipping attribute loading.")
                        return@submit
                    }
                }

                devLog("Allowed list: $allowed")

                val idString = mmoData["mmo:id"]?.toString() ?: return@submit
                val slot = mmoData["mmo:slot"]?.let { EquipmentSlot.valueOf(it.toString()) } ?: defslot
                val source = mmoData["mmo:source"]?.let { ModifierSource.valueOf(it.toString()) } ?: defSource

                devLog("ID: $idString, Slot: $slot, Source: $source")

                for (key in mmoFilter) {
                    mmoData.remove("mmo:$key")
                }

                for (data in mmoData) {

                    val atb = solveStatModifier(data)
                    devLog("atb modifier: $atb")

                    val uuid = (idString + "_" + atb.atbID).generateUUID()

                    StatModifier(
                        uuid,
                        "sertraline_item",
                        atb.atbID,
                        atb.atbValue,
                        atb.atbType,
                        slot,
                        source,
                    ).register(playerData)

                    devLog("registering modifier: ${uuid.toString() + "," + "SertralineItem" + "," + atb.atbID + "," + atb.atbValue + "," + atb.atbType + "," + slot + "," + source}")
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
}