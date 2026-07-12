package io.github.zzzyyylllty.sertraline.data

import io.github.zzzyyylllty.sertraline.Sertraline
import io.github.zzzyyylllty.sertraline.database.CraftingSession
import io.github.zzzyyylllty.sertraline.database.DatabaseManager
import io.github.zzzyyylllty.sertraline.gui.CraftingStationManager
import io.github.zzzyyylllty.sertraline.logger.infoS
import io.github.zzzyyylllty.sertraline.logger.severeS
import io.github.zzzyyylllty.sertraline.logger.warningS
import io.github.zzzyyylllty.sertraline.util.minimessage.toComponent
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submit
import taboolib.platform.util.deserializeToItemStack
import taboolib.platform.util.giveItem
import taboolib.platform.util.serializeToByteArray
import java.util.Base64

/**
 * 合成站数据管理器。
 * 使用 PTC Object ORM（CraftingSession 表）持久化玩家进行中的合成会话，
 * 防止服务器关闭/玩家退出时材料丢失。
 */
object CraftingStationDataManager {

    private val base64Encoder = Base64.getEncoder()
    private val base64Decoder = Base64.getDecoder()

    /**
     * 保存玩家进行中的合成会话。
     */
    fun saveSession(player: Player, stationId: String, recipeId: String, startTime: Long, totalSeconds: Double, consumedItems: List<ItemStack>) {
        try {
            val uuid = player.uniqueId.toString()
            val itemsBlob = consumedItems.joinToString(";") { item ->
                base64Encoder.encodeToString(item.serializeToByteArray())
            }

            DatabaseManager.sessionMapper.insertOrUpdate(
                CraftingSession(uuid, stationId, recipeId, startTime, totalSeconds, itemsBlob)
            ) { "uuid" eq uuid }
        } catch (e: Exception) {
            severeS("Failed to save crafting session for ${player.name}: ${e.message}")
        }
    }

    /**
     * 读取玩家未完成的合成会话。
     * @return 会话数据，若不存在返回 null
     */
    fun loadSession(player: Player): PendingSession? {
        try {
            val uuid = player.uniqueId.toString()
            val session = DatabaseManager.sessionMapper.findById(uuid) ?: return null

            val items = if (session.consumedItemsBlob.isNotBlank()) {
                session.consumedItemsBlob.split(";").mapNotNull { encoded ->
                    try {
                        val bytes = base64Decoder.decode(encoded)
                        bytes.deserializeToItemStack()
                    } catch (e: Exception) {
                        warningS("Failed to deserialize consumed item for ${player.name}: ${e.message}")
                        null
                    }
                }
            } else emptyList()

            return PendingSession(session.stationId, session.recipeId, session.startTime, session.totalSeconds, items)
        } catch (e: Exception) {
            severeS("Failed to load crafting session for ${player.name}: ${e.message}")
            return null
        }
    }

    /**
     * 清除玩家的合成会话数据。
     */
    fun clearSession(player: Player) {
        try {
            DatabaseManager.sessionMapper.deleteById(player.uniqueId.toString())
        } catch (e: Exception) {
            warningS("Failed to clear crafting session for ${player.name}: ${e.message}")
        }
    }

    /**
     * 尝试恢复玩家未完成的合成。
     * - 若合成时间已到 → 给予产出物
     * - 若合成未完成 → 返还材料
     */
    fun restoreSession(player: Player) {
        val pending = loadSession(player) ?: return

        // 总是在主线程操作物品
        submit(async = false) {
            try {
                val station = Sertraline.craftingStations[pending.stationId]
                val recipe = station?.recipes?.values?.find { it.displayName == pending.recipeId }

                val now = System.currentTimeMillis()
                val elapsed = (now - pending.startTime) / 1000.0
                val isComplete = elapsed >= pending.totalSeconds

                if (isComplete && recipe != null) {
                    // 合成已完成 → 给予产出物
                    for (output in recipe.outputs) {
                        val stack = output.build(player)
                        player.giveItem(stack)
                    }
                    CraftingStationManager.fireLifecycleAgents(station!!, recipe, "onClaim", player)
                    player.sendMessage((Sertraline.config
                        .getString("messages.crafting-complete", "<green>Crafting complete!</green>")
                        ?: "<green>Crafting complete!</green>").toComponent()
                    )
                    infoS("Restored completed craft for ${player.name}: ${pending.recipeId}")
                } else {
                    // 合成未完成 → 返还材料
                    for (item in pending.consumedItems) {
                        player.inventory.addItem(item).values.forEach { leftover ->
                            player.world.dropItem(player.location, leftover)
                        }
                    }
                    if (recipe != null) {
                        CraftingStationManager.fireLifecycleAgents(station!!, recipe, "onCancel", player)
                        player.sendMessage("<yellow>Your crafting was interrupted. Materials returned.</yellow>".toComponent())
                    } else {
                        player.sendMessage("<yellow>Your crafting station/recipe no longer exists. Materials returned.</yellow>".toComponent())
                    }
                    infoS("Returned materials to ${player.name} for interrupted craft: ${pending.recipeId}")
                }
            } catch (e: Exception) {
                severeS("Failed to restore crafting session for ${player.name}: ${e.message}")
                // 兜底：尽量返还材料
                for (item in pending.consumedItems) {
                    try {
                        player.inventory.addItem(item).values.forEach { leftover ->
                            player.world.dropItem(player.location, leftover)
                        }
                    } catch (_: Exception) {}
                }
            } finally {
                clearSession(player)
            }
        }
    }

    /**
     * 待恢复的合成会话数据。
     */
    data class PendingSession(
        val stationId: String,
        val recipeId: String,
        val startTime: Long,
        val totalSeconds: Double,
        val consumedItems: List<ItemStack>
    )
}
