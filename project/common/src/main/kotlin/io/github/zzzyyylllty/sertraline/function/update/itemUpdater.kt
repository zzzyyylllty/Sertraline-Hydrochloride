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
 * 获取物品的预期修订版本号
 * 优先级: 显式指定 > 自动追踪
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
 * 检查单个物品是否需要更新，并在需要时重建替换。
 *
 * 使用原子性 slot.setItem 替换，禁止 addItem/removeItem 组合，
 * 避免产生刷物品漏洞。
 *
 * @return true 如果物品已更新
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

    // 原子替换，禁止 addItem/removeItem 组合
    player.inventory.setItem(slot, newItem)

    return true
}

/**
 * 检查玩家全部背包物品（含手持、盔甲栏）。
 * 用于登录时或定时扫描。
 */
fun updatePlayerItems(player: Player) {
    val inv = player.inventory
    // 主背包 + 快捷栏 slots 0-35 (storageContents 保证不含盔甲/副手)
    inv.storageContents.forEachIndexed { index, item ->
        if (item == null || item.isAir) return@forEachIndexed
        checkAndUpdateItem(player, item, index)
    }
    // 盔甲槽 36-39
    inv.armorContents.forEachIndexed { index, item ->
        if (item == null || item.isAir) return@forEachIndexed
        val armorSlot = 36 + index // 0→36(boots), 1→37, 2→38, 3→39(helmet)
        checkAndUpdateItem(player, item, armorSlot)
    }
    // 副手 slot 40
    val offHand = inv.itemInOffHand
    if (!offHand.isAir) {
        checkAndUpdateItem(player, offHand, 40)
    }
}
