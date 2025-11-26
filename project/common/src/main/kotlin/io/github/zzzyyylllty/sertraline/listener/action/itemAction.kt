package io.github.zzzyyylllty.sertraline.listener.action

import io.github.zzzyyylllty.sertraline.util.ActionHelper.throttleAction
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemBreakEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.SubscribeEvent


data class ThrottleActionLink(
    val uuid: String,
    val str: String,
)
data class ThrottleActionParam(
    val p: Player,
    val e: Event,
    val ce: Cancellable?,
    val bItem : ItemStack? = null,
    val abItem : ItemStack? = null
)

@SubscribeEvent
fun onInteract(e: PlayerInteractEvent) {

    val uuid = e.player.uniqueId.toString()
    val param = ThrottleActionParam(e.player, e, e, e.item)

    throttleAction(ThrottleActionLink(uuid, "onClick"), param)
    if (e.action.isRightClick) {
        throttleAction(ThrottleActionLink(uuid, "onRightClick"), param)
    } else if (e.action.isLeftClick) {
        throttleAction(ThrottleActionLink(uuid, "onLeftClick"), param)
    }
}
@SubscribeEvent
fun onLogin(e: PlayerLoginEvent) {
    throttleAction(ThrottleActionLink(e.player.uniqueId.toString(), "onLogin"), ThrottleActionParam(e.player, e, null)) // todo
}

@SubscribeEvent
fun onPreAttack(e: PrePlayerAttackEntityEvent) {
    throttleAction(ThrottleActionLink(e.player.uniqueId.toString(), "onPreAttack"), ThrottleActionParam(e.player, e, e, e.player.activeItem))

}

@SubscribeEvent
fun onAttack(e: EntityDamageByEntityEvent) {
    val player = e.damageSource.causingEntity as? Player ?: return
    var item: ItemStack? = null
    val dEntity = e.damageSource.directEntity
    if (dEntity is AbstractArrow) {
        item = dEntity.weapon
    } else if (dEntity is Player) {
        item = dEntity.inventory.itemInMainHand
    }
    throttleAction(ThrottleActionLink(player.uniqueId.toString(), "onAttack"), ThrottleActionParam(player, e, e, item))
}

@SubscribeEvent
fun onConsume(e: PlayerItemConsumeEvent) {

    throttleAction(ThrottleActionLink(e.player.uniqueId.toString(), "onConsume"), ThrottleActionParam(e.player, e, e, e.item, e.replacement))
}

@SubscribeEvent
fun onBreak(e: PlayerItemBreakEvent) {
    throttleAction(ThrottleActionLink(e.player.uniqueId.toString(), "onBreak"), ThrottleActionParam(e.player, e, null, e.brokenItem))
}
@SubscribeEvent
fun onDrop(e: PlayerDropItemEvent) {
    throttleAction(ThrottleActionLink(e.player.uniqueId.toString(), "onDrop"), ThrottleActionParam(e.player, e, e, e.itemDrop.itemStack))
}
@SubscribeEvent
fun onPickup(e: PlayerPickupItemEvent) {
    throttleAction(ThrottleActionLink(e.player.uniqueId.toString(), "onPickUp"), ThrottleActionParam(e.player, e, e, e.item.itemStack))
}
@SubscribeEvent
fun onClickInventory(e: InventoryClickEvent) {
    if (e.whoClicked is Player) {
        val player = e.whoClicked as Player
        throttleAction(
            ThrottleActionLink(player.uniqueId.toString(), "onInventoryClick"),
            ThrottleActionParam(player, e, e, e.currentItem, e.cursor)
        )
    }
}
@SubscribeEvent
fun onSwap(e: PlayerSwapHandItemsEvent) {
    throttleAction(ThrottleActionLink(e.player.uniqueId.toString(), "onSwap@Main"), ThrottleActionParam(e.player, e, e, e.mainHandItem, e.offHandItem))
    throttleAction(ThrottleActionLink(e.player.uniqueId.toString(), "onSwap@Off"), ThrottleActionParam(e.player, e, e, e.offHandItem, e.mainHandItem))
}