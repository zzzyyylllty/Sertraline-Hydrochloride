package io.github.zzzyyylllty.sertraline.function.update

import io.github.zzzyyylllty.sertraline.Sertraline
import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.itemExpectedRevision
import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.item.sertralineItemBuilderInternal
import io.github.zzzyyylllty.sertraline.logger.severeS
import io.github.zzzyyylllty.sertraline.manager.PRIVATE_OWNER_TAG
import io.github.zzzyyylllty.sertraline.manager.SubManagerType
import io.github.zzzyyylllty.sertraline.manager.isPrivateItemId
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.getItemTag
import taboolib.platform.util.isAir

/**
 * Gets the expected revision number of an item
 * Priority: explicit value > auto tracking
 */
fun getExpectedRevision(itemId: String, player: Player? = null, ownerUuid: String? = null): Int {
    val normalizedId = if (isPrivateItemId(itemId)) {
        io.github.zzzyyylllty.sertraline.manager.normalizePrivateItemId(itemId)
    } else itemId
    val template = if (isPrivateItemId(normalizedId)) {
        val uuid = ownerUuid ?: player?.uniqueId?.toString()
        if (uuid == null) null else {
            Sertraline.manager.privateManager.getItem(uuid, normalizedId, SubManagerType.TEMPORARY)
                ?: Sertraline.manager.privateManager.getItem(uuid, normalizedId, SubManagerType.PERSISTENT)
        }
    } else {
        itemMap[normalizedId]
    }
    val explicit = template?.getDeepData("sertraline:revision-id")
    if (explicit is Number) return explicit.toInt()
    return itemExpectedRevision[itemId] ?: itemExpectedRevision[normalizedId] ?: 0
}

/**
 * Checks whether a single item needs updating, and rebuilds/replaces it if so.
 *
 * Uses the atomic slot.setItem replacement; addItem/removeItem combos are forbidden,
 * to avoid an item-duplication exploit.
 *
 * @return true if the item was updated
 */
fun checkAndUpdateItem(player: Player, itemStack: ItemStack?, slot: Int): Boolean {
    if (itemStack == null || itemStack.isAir) return false
    if (!config.getBoolean("item-update.enabled", true)) return false

    val tag = itemStack.getItemTag(true)
    val sID = tag["sertraline_id"]?.asString() ?: return false
    val ownerUuid = tag[PRIVATE_OWNER_TAG]?.asString()?.trim()?.takeIf { it.isNotEmpty() }
    val expectedRevision = getExpectedRevision(sID, ownerUuid = ownerUuid)
    if (expectedRevision <= 0) return false

    val currentRevision = tag["sertraline_revision"]?.asInt() ?: 0
    if (currentRevision >= expectedRevision) return false

    devLog("Updating item $sID (rev $currentRevision -> $expectedRevision) for ${player.name}")

    val newItem = try {
        sertralineItemBuilderInternal(sID, player, source = itemStack, amount = itemStack.amount, rebuild = itemStack)
    } catch (e: Exception) {
        severeS("Failed to rebuild item $sID (rev $currentRevision -> $expectedRevision) for ${player.name} at slot $slot: ${e.message}")
        e.printStackTrace()
        return false
    } ?: return false
    newItem.amount = itemStack.amount

    // atomic replacement; addItem/removeItem combos are forbidden
    player.inventory.setItem(slot, newItem)

    return true
}

/**
 * Checks all of a player's inventory items (including held and armor slots).
 * Used at login or for periodic scans.
 */
fun updatePlayerItems(player: Player) {
    val inv = player.inventory
    // main inventory + hotbar slots 0-35 (storageContents guarantees no armor/offhand)
    inv.storageContents.forEachIndexed { index, item ->
        if (item == null || item.isAir) return@forEachIndexed
        checkAndUpdateItem(player, item, index)
    }
    // armor slots 36-39
    inv.armorContents.forEachIndexed { index, item ->
        if (item == null || item.isAir) return@forEachIndexed
        val armorSlot = 36 + index // 0→36(boots), 1→37, 2→38, 3→39(helmet)
        checkAndUpdateItem(player, item, armorSlot)
    }
    // offhand slot 40
    val offHand = inv.itemInOffHand
    if (!offHand.isAir) {
        checkAndUpdateItem(player, offHand, 40)
    }
}
