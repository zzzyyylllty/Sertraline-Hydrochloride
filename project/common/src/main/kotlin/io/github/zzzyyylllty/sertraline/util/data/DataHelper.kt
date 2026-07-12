package io.github.zzzyyylllty.sertraline.util.data

import io.github.zzzyyylllty.sertraline.database.DatabaseManager
import io.github.zzzyyylllty.sertraline.database.PlayerCooldown
import io.github.zzzyyylllty.sertraline.database.PlayerProperty
import io.github.zzzyyylllty.sertraline.util.toBooleanTolerance
import io.github.zzzyyylllty.sertraline.util.serialize.CastHelper
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import taboolib.common.platform.event.SubscribeEvent
import java.time.Instant
import java.util.Date
import kotlin.math.round

object DataHelper {

    @SubscribeEvent
    fun onJoin(event: PlayerJoinEvent) {
        // PTC Object mappers handle database connections automatically.
        // No per-player DataContainer setup needed.
    }
}

@Suppress("UNUSED")
object DataUtil {

    // ──────────────────────────────────────────────
    //  Generic key-value player data
    // ──────────────────────────────────────────────

    fun savePlayerData(player: Player) {
        player.saveData()
    }

    fun resetAllData(player: Player) {
        val uuid = player.uniqueId.toString()
        DatabaseManager.propertyMapper.deleteWhere { "uuid" eq uuid }
    }

    fun getDataRaw(player: Player, dataID: String): String? {
        return DatabaseManager.propertyMapper.findOneByKey(
            PlayerProperty(player.uniqueId.toString(), dataID, "")
        )?.propValue
    }

    fun getDataSmart(player: Player, dataID: String): Any? {
        return getDataRaw(player, dataID)?.let { CastHelper.smartCast(it) }
    }

    fun getDataAsInt(player: Player, dataID: String): Int? {
        return getDataRaw(player, dataID)?.toIntOrNull()
    }

    fun getDataAsBoolean(player: Player, dataID: String): Boolean? {
        return getDataRaw(player, dataID)?.toBooleanTolerance()
    }

    fun getDataAsDouble(player: Player, dataID: String): Double? {
        return getDataRaw(player, dataID)?.toDoubleOrNull()
    }

    fun getDataAsLong(player: Player, dataID: String): Long? {
        return getDataRaw(player, dataID)?.toLongOrNull()
    }

    fun removeData(player: Player, dataID: String) {
        DatabaseManager.propertyMapper.deleteByKey(
            PlayerProperty(player.uniqueId.toString(), dataID, "")
        )
    }

    fun setData(player: Player, dataID: String, dataValue: Any) {
        val uuid = player.uniqueId.toString()
        val value = dataValue.toString()
        DatabaseManager.propertyMapper.insertOrUpdate(
            PlayerProperty(uuid, dataID, value)
        ) { "uuid" eq uuid; "propKey" eq dataID }
    }

    fun setDataIfNotExist(player: Player, dataID: String, dataValue: Any) {
        if (getDataRaw(player, dataID) == null) {
            setData(player, dataID, dataValue)
        }
    }

    fun getAllDataRaw(player: Player): Map<String, String> {
        return DatabaseManager.propertyMapper.findAll(player.uniqueId.toString())
            .associate { it.propKey to it.propValue }
    }

    // ──────────────────────────────────────────────
    //  Cooldown management
    // ──────────────────────────────────────────────

    fun resetAllCooldown(player: Player) {
        DatabaseManager.cooldownMapper.deleteWhere { "uuid" eq player.uniqueId.toString() }
    }

    fun getAllCooldownRaw(player: Player): Map<String, String> {
        return DatabaseManager.cooldownMapper.findAll(player.uniqueId.toString())
            .associate { it.cooldownId to it.expiry.toString() }
    }

    fun getAllCooldownLong(player: Player): Map<String, Long> {
        return DatabaseManager.cooldownMapper.findAll(player.uniqueId.toString())
            .associate { it.cooldownId to it.expiry }
    }

    fun getAllCooldownDate(player: Player): Map<String, Date> {
        return DatabaseManager.cooldownMapper.findAll(player.uniqueId.toString())
            .associate { it.cooldownId to Date.from(Instant.ofEpochMilli(it.expiry)) }
    }

    fun getCooldownRaw(player: Player, cooldownID: String): String? {
        return DatabaseManager.cooldownMapper.findOneByKey(
            PlayerCooldown(player.uniqueId.toString(), cooldownID, 0)
        )?.expiry?.toString()
    }

    fun getCooldownLong(player: Player, cooldownID: String): Long? {
        return DatabaseManager.cooldownMapper.findOneByKey(
            PlayerCooldown(player.uniqueId.toString(), cooldownID, 0)
        )?.expiry
    }

    fun getCooldownDate(player: Player, cooldownID: String): Date? {
        return getCooldownLong(player, cooldownID)?.let {
            Date.from(Instant.ofEpochMilli(it))
        }
    }

    fun getCooldownLeftLong(player: Player, cooldownID: String): Long? {
        val expiry = getCooldownLong(player, cooldownID) ?: return null
        return expiry - Instant.now().toEpochMilli()
    }

    fun getCooldownLeftDate(player: Player, cooldownID: String): Date? {
        return getCooldownLeftLong(player, cooldownID)?.let {
            Date.from(Instant.ofEpochMilli(it))
        }
    }

    fun setCooldown(player: Player, cooldownID: String, second: Double) {
        val uuid = player.uniqueId.toString()
        val expiry = round(Instant.now().toEpochMilli() + second * 1000).toLong()
        DatabaseManager.cooldownMapper.insertOrUpdate(
            PlayerCooldown(uuid, cooldownID, expiry)
        ) { "uuid" eq uuid; "cooldownId" eq cooldownID }
    }

    fun setCooldownMill(player: Player, cooldownID: String, tick: Int) {
        val uuid = player.uniqueId.toString()
        val expiry = Instant.now().toEpochMilli() + tick
        DatabaseManager.cooldownMapper.insertOrUpdate(
            PlayerCooldown(uuid, cooldownID, expiry)
        ) { "uuid" eq uuid; "cooldownId" eq cooldownID }
    }

    fun extendCooldown(player: Player, cooldownID: String, second: Double) {
        val uuid = player.uniqueId.toString()
        val current = getCooldownLong(player, cooldownID) ?: Instant.now().toEpochMilli()
        val expiry = round(current + second * 1000).toLong()
        DatabaseManager.cooldownMapper.insertOrUpdate(
            PlayerCooldown(uuid, cooldownID, expiry)
        ) { "uuid" eq uuid; "cooldownId" eq cooldownID }
    }

    fun extendCooldownMill(player: Player, cooldownID: String, tick: Int) {
        val uuid = player.uniqueId.toString()
        val current = getCooldownLong(player, cooldownID) ?: Instant.now().toEpochMilli()
        val expiry = current + tick
        DatabaseManager.cooldownMapper.insertOrUpdate(
            PlayerCooldown(uuid, cooldownID, expiry)
        ) { "uuid" eq uuid; "cooldownId" eq cooldownID }
    }

    fun reduceCooldown(player: Player, cooldownID: String, second: Double) {
        val uuid = player.uniqueId.toString()
        val current = getCooldownLong(player, cooldownID) ?: Instant.now().toEpochMilli()
        val expiry = round(current - second * 1000).toLong()
        DatabaseManager.cooldownMapper.insertOrUpdate(
            PlayerCooldown(uuid, cooldownID, expiry)
        ) { "uuid" eq uuid; "cooldownId" eq cooldownID }
    }

    fun reduceCooldownTick(player: Player, cooldownID: String, tick: Int) {
        val uuid = player.uniqueId.toString()
        val current = getCooldownLong(player, cooldownID) ?: Instant.now().toEpochMilli()
        val expiry = current - tick
        DatabaseManager.cooldownMapper.insertOrUpdate(
            PlayerCooldown(uuid, cooldownID, expiry)
        ) { "uuid" eq uuid; "cooldownId" eq cooldownID }
    }

    fun resetCooldown(player: Player, cooldownID: String) {
        DatabaseManager.cooldownMapper.deleteByKey(
            PlayerCooldown(player.uniqueId.toString(), cooldownID, 0)
        )
    }

    fun isInCooldown(player: Player, cooldownID: String): Boolean {
        val expiry = getCooldownLong(player, cooldownID) ?: return false
        return expiry >= Instant.now().toEpochMilli()
    }

    fun cleanupCooldown(player: Player) {
        val uuid = player.uniqueId.toString()
        val now = Instant.now().toEpochMilli()
        DatabaseManager.cooldownMapper.deleteWhere {
            "uuid" eq uuid
            "expiry" lte now
        }
    }
}
