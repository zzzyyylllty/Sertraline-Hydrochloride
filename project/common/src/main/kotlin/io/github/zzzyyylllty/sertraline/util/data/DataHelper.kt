package io.github.zzzyyylllty.sertraline.util.data

import io.github.zzzyyylllty.sertraline.util.serialize.CastHelper
import io.github.zzzyyylllty.sertraline.util.toBooleanTolerance
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync
import taboolib.expansion.getDataContainer
import taboolib.expansion.releaseDataContainer
import taboolib.expansion.setupDataContainer
import java.time.Instant
import java.util.Date
import kotlin.math.round

object DataHelper {
    @SubscribeEvent
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player

        // 初始化缓存优先容器
        submitAsync {
            player.setupDataContainer()
        }
    }

    @SubscribeEvent
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player

        // 释放数据容器
        submitAsync {
            DataUtil.cleanupCooldown(player)
            player.releaseDataContainer()
        }
    }
}

@Suppress("UNUSED")
object DataUtil {


    fun savePlayerData(player: Player) {
        player.saveData()
    }

    fun resetAllData(player: Player) {
        val container = player.getDataContainer()
        container.source.clear()
    }

    fun getDataRaw(player: Player, dataID: String): String? {
        val container = player.getDataContainer()
        return container[dataID]
    }

    fun getDataSmart(player: Player, dataID: String): Any? {
        val container = player.getDataContainer()
        return container[dataID]?.let { CastHelper.smartCast(it) }
    }

    fun getDataAsInt(player: Player, dataID: String): Int? {
        val container = player.getDataContainer()
        return container[dataID]?.toIntOrNull()
    }

    fun getDataAsBoolean(player: Player, dataID: String): Boolean? {
        val container = player.getDataContainer()
        return container[dataID]?.toBooleanTolerance()
    }

    fun getDataAsDouble(player: Player, dataID: String): Double? {
        val container = player.getDataContainer()
        return container[dataID]?.toDoubleOrNull()
    }

    fun getDataAsLong(player: Player, dataID: String): Long? {
        val container = player.getDataContainer()
        return container[dataID]?.toLongOrNull()
    }

    fun removeData(player: Player, dataID: String) {
        val container = player.getDataContainer()
        container.delete(dataID)
    }

    fun setData(player: Player, dataID: String, dataValue: Any) {
        val container = player.getDataContainer()
        container[dataID] = dataValue
    }

    fun setDataIfNotExist(player: Player, dataID: String, dataValue: Any) {
        val container = player.getDataContainer()
        if (container[dataID] == null) container[dataID] = dataValue
    }

    fun resetAllCooldown(player: Player) {
        val container = player.getDataContainer()
        for (key in container.keys().filter { key -> key.startsWith("cooldown.") }) {
            container.delete(key)
        }
    }

    fun getAllCooldownRaw(player: Player): Map<String, String> {
        val container = player.getDataContainer()
        return container.source.filter { (key, value) -> key.startsWith("cooldown.") }
    }
    
    fun getAllDataRaw(player: Player): Map<String, String> {
        val container = player.getDataContainer()
        return container.source
    }

    fun getAllCooldownLong(player: Player): Map<String, Long> {
        val container = player.getDataContainer()
        val map = mutableMapOf<String, Long>()
        for (element in container.source.filter { (key, value) -> key.startsWith("cooldown.") }) {
            element.value.toLongOrNull()?.let { map.put(element.key, it) }
        }
        return map
    }

    fun getAllCooldownDate(player: Player): Map<String, Date> {
        val container = player.getDataContainer()
        val map = mutableMapOf<String, Date>()
        for (element in container.source.filter { (key, value) -> key.startsWith("cooldown.") }) {
            element.value.toLongOrNull()?.let { map.put(element.key, Date.from(Instant.ofEpochMilli(it))) }
        }
        return map
    }

    fun getCooldownRaw(player: Player, cooldownID: String): String? {
        val container = player.getDataContainer()
        return container["cooldown.$cooldownID"]
    }

    fun getCooldownLong(player: Player, cooldownID: String): Long? {
        val container = player.getDataContainer()
        return container["cooldown.$cooldownID"]?.toLongOrNull()
    }

    fun getCooldownDate(player: Player, cooldownID: String): Date? {
        val container = player.getDataContainer()
        return container["cooldown.$cooldownID"]?.toLongOrNull()?.let { Date.from(Instant.ofEpochMilli(it)) }
    }

    fun getCooldownLeftLong(player: Player, cooldownID: String): Long? {
        val container = player.getDataContainer()
        return container["cooldown.$cooldownID"]?.toLongOrNull()?.let { it - Instant.now().toEpochMilli() }
    }

    fun getCooldownLeftDate(player: Player, cooldownID: String): Date? {
        val container = player.getDataContainer()
        return container["cooldown.$cooldownID"]?.toLongOrNull()?.let { Date.from(Instant.ofEpochMilli(it - Instant.now().toEpochMilli())) }
    }

    fun setCooldown(player: Player, cooldownID: String, second: Double) {
        val container = player.getDataContainer()
        val current = Instant.now().toEpochMilli()
        container["cooldown.$cooldownID"] = round(current + second * 1000).toLong()
    }

    fun setCooldownMill(player: Player, cooldownID: String, tick: Int) {
        val container = player.getDataContainer()
        val current = Instant.now().toEpochMilli()
        container["cooldown.$cooldownID"] = current + tick
    }

    fun extendCooldown(player: Player, cooldownID: String, second: Double) {
        val container = player.getDataContainer()
        val current = container["cooldown.$cooldownID"]?.toLongOrNull() ?: Instant.now().toEpochMilli()
        container["cooldown.$cooldownID"] = round(current + second * 1000).toLong()
    }

    fun extendCooldownMill(player: Player, cooldownID: String, tick: Int) {
        val container = player.getDataContainer()
        val current = container["cooldown.$cooldownID"]?.toLongOrNull() ?: Instant.now().toEpochMilli()
        container["cooldown.$cooldownID"] = current + tick
    }

    fun reduceCooldown(player: Player, cooldownID: String, second: Double) {
        val container = player.getDataContainer()
        val current = container["cooldown.$cooldownID"]?.toLongOrNull() ?: Instant.now().toEpochMilli()
        container["cooldown.$cooldownID"] = round(current - second * 1000).toLong()
    }

    fun reduceCooldownTick(player: Player, cooldownID: String, tick: Int) {
        val container = player.getDataContainer()
        val current = container["cooldown.$cooldownID"]?.toLongOrNull() ?: Instant.now().toEpochMilli()
        container["cooldown.$cooldownID"] = current - tick
    }

    fun resetCooldown(player: Player, cooldownID: String) {
        val container = player.getDataContainer()
        container.delete("cooldown.$cooldownID")
    }

    fun isInCooldown(player: Player, cooldownID: String): Boolean {
        val container = player.getDataContainer()
        return (container["cooldown.$cooldownID"]?.toLongOrNull() ?: 0L) >= Instant.now().toEpochMilli()
    }

    fun cleanupCooldown(player: Player) {
        val container = player.getDataContainer()
        container.source.filter { (key, value) -> key.startsWith("cooldown.") }.forEach {
            if ((container["cooldown.${it.key}"]?.toLongOrNull() ?: 0L) <= Instant.now().toEpochMilli()) container.delete(it.key)
        }
    }

}