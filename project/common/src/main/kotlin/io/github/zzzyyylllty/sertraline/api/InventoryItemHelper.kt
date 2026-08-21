package io.github.zzzyyylllty.sertraline.api

import io.github.zzzyyylllty.sertraline.function.data.getSertralineId
import io.github.zzzyyylllty.sertraline.manager.PRIVATE_OWNER_TAG
import io.github.zzzyyylllty.sertraline.manager.ManagerRange
import io.github.zzzyyylllty.sertraline.manager.isPrivateItemId
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import taboolib.module.nms.getItemTag
import taboolib.platform.util.isNotAir

public enum class InventorySlotRange {
    STORAGE,
    ALL
}

private data class InventorySlot(
    val get: () -> ItemStack?,
    val set: (ItemStack?) -> Unit
)

internal fun countInventoryItems(
    id: String,
    player: Player,
    range: ManagerRange,
    slots: InventorySlotRange,
    verifyPrivateOwner: Boolean
): Int {
    return inventorySlots(player.inventory, slots)
        .mapNotNull { it.get() }
        .filter { stack -> matchesStack(stack, id, player, range, verifyPrivateOwner) }
        .sumOf { it.amount }
}

internal fun hasInventoryItems(
    id: String,
    player: Player,
    amount: Int,
    range: ManagerRange,
    slots: InventorySlotRange,
    verifyPrivateOwner: Boolean
): Boolean {
    return amount > 0 && countInventoryItems(id, player, range, slots, verifyPrivateOwner) >= amount
}

internal fun takeInventoryItems(
    id: String,
    player: Player,
    amount: Int,
    range: ManagerRange,
    slots: InventorySlotRange,
    verifyPrivateOwner: Boolean,
    exactly: Boolean
): Int {
    if (amount <= 0 || !canMatch(id, range)) return 0

    val matchingSlots = inventorySlots(player.inventory, slots).filter { slot ->
        slot.get()?.let { stack -> matchesStack(stack, id, player, range, verifyPrivateOwner) } == true
    }
    if (exactly && matchingSlots.sumOf { it.get()?.amount ?: 0 } < amount) return 0

    var remaining = amount
    for (slot in matchingSlots) {
        val stack = slot.get() ?: continue
        if (!matchesStack(stack, id, player, range, verifyPrivateOwner)) continue

        val taken = minOf(remaining, stack.amount)
        if (taken == stack.amount) {
            slot.set(null)
        } else {
            stack.amount -= taken
            slot.set(stack)
        }
        remaining -= taken
        if (remaining == 0) break
    }
    return amount - remaining
}

private fun canMatch(id: String, range: ManagerRange): Boolean {
    return if (isPrivateItemId(id)) {
        range != ManagerRange.PUBLIC
    } else {
        range != ManagerRange.PRIVATE
    }
}

private fun matchesStack(
    stack: ItemStack,
    id: String,
    player: Player,
    range: ManagerRange,
    verifyPrivateOwner: Boolean
): Boolean {
    if (!stack.isNotAir() || stack.getSertralineId() != id || !canMatch(id, range)) return false
    if (!isPrivateItemId(id) || !verifyPrivateOwner) return true

    val owner = stack.getItemTag(true)[PRIVATE_OWNER_TAG]?.asString()?.trim()
    return !owner.isNullOrEmpty() && owner.equals(player.uniqueId.toString(), ignoreCase = true)
}

private fun inventorySlots(inventory: PlayerInventory, range: InventorySlotRange): List<InventorySlot> {
    val slots = inventory.storageContents.indices.map { index ->
        InventorySlot({ inventory.getItem(index) }, { inventory.setItem(index, it) })
    }.toMutableList()
    if (range == InventorySlotRange.ALL) {
        slots += InventorySlot({ inventory.helmet }, { inventory.helmet = it })
        slots += InventorySlot({ inventory.chestplate }, { inventory.chestplate = it })
        slots += InventorySlot({ inventory.leggings }, { inventory.leggings = it })
        slots += InventorySlot({ inventory.boots }, { inventory.boots = it })
        slots += InventorySlot({ inventory.itemInOffHand }, { inventory.setItemInOffHand(it) })
    }
    return slots
}
