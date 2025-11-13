package io.github.zzzyyylllty.sertraline.listener.action

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.function.action.applyActions
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.block.Action.*
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
import taboolib.common.function.throttle
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync
import taboolib.platform.util.attacker


val throttleAction = throttle<ThrottleActionLink, ThrottleActionParam>(config.getLong("action.throttle-time", 500)){ link, data ->
    if (data.i2 == null || data.i2.isEmpty) devLog("ItemStack is null or air or amount == 0,Skipping actions.") else data.p.applyActions(link.str, data.e, data.i2)
}


data class ThrottleActionLink(
    val uuid: String,
    val str: String,
)
data class ThrottleActionParam(
    val p: Player,
    val e: Event,
    val i2 : ItemStack? = null,
    val islot : Int? = null
)

@SubscribeEvent
fun onInteract(e: PlayerInteractEvent) {
    submitAsync {
        throttleAction(ThrottleActionLink(e.player.uniqueId.toString(), "onClick"), ThrottleActionParam(e.player, e, e.item, e.hand?.ordinal))
    }
}
@SubscribeEvent
fun onLeftClick(e: PlayerInteractEvent) {
    if (e.action.isLeftClick) submitAsync {
        throttleAction(ThrottleActionLink(e.player.uniqueId.toString(), "onLeftClick"), ThrottleActionParam(e.player, e, e.item, e.hand?.ordinal))
    }
}
@SubscribeEvent
fun onRightClick(e: PlayerInteractEvent) {
    if (e.action.isRightClick) submitAsync {
        throttleAction(ThrottleActionLink(e.player.uniqueId.toString(), "onRightClick"), ThrottleActionParam(e.player, e, e.item, e.hand?.ordinal))
    }
}
@SubscribeEvent
fun onPhysicalInteract(e: PlayerInteractEvent) {
    if (e.action == PHYSICAL) submitAsync {
        throttleAction(ThrottleActionLink(e.player.uniqueId.toString(), "onPhysical"), ThrottleActionParam(e.player, e, e.item, e.hand?.ordinal))
    }
}

@SubscribeEvent
fun onLogin(e: PlayerLoginEvent) {
    submitAsync {
        throttleAction(ThrottleActionLink(e.player.uniqueId.toString(), "onLogin"), ThrottleActionParam(e.player, e))
}
}

@SubscribeEvent
fun onPreAttack(e: PrePlayerAttackEntityEvent) {
    submitAsync {
        throttleAction(ThrottleActionLink(e.player.uniqueId.toString(), "onPreAttack"), ThrottleActionParam(e.player, e, e.player.activeItem, e.player.activeItemHand.ordinal))
    }
}

@SubscribeEvent
fun onAttack(e: EntityDamageByEntityEvent) {
    if (e.attacker is Player)
    submitAsync {
        val player = e.attacker as Player
        throttleAction(ThrottleActionLink(player.uniqueId.toString(), "onAttack"), ThrottleActionParam(player, e, player.activeItem, player.activeItemHand.ordinal))
    }
}

@SubscribeEvent
fun onConsume(e: PlayerItemConsumeEvent) {
    submitAsync {
        throttleAction(ThrottleActionLink(e.player.uniqueId.toString(), "onConsume"), ThrottleActionParam(e.player, e, e.item, e.hand.ordinal))
    }
}

@SubscribeEvent
fun onBreak(e: PlayerItemBreakEvent) {
    submitAsync {
        throttleAction(ThrottleActionLink(e.player.uniqueId.toString(), "onBreak"), ThrottleActionParam(e.player, e, e.brokenItem))
    }
}
@SubscribeEvent
fun onDrop(e: PlayerDropItemEvent) {
    submitAsync {
        throttleAction(ThrottleActionLink(e.player.uniqueId.toString(), "onDrop"), ThrottleActionParam(e.player, e, e.itemDrop.itemStack))
    }
}
@SubscribeEvent
fun onPickup(e: PlayerPickupItemEvent) {
    submitAsync {
        throttleAction(ThrottleActionLink(e.player.uniqueId.toString(), "onPickUp"), ThrottleActionParam(e.player, e, e.item.itemStack, e.remaining))
    }
}
@SubscribeEvent
fun onClickInventory(e: InventoryClickEvent) {
    if (e.whoClicked is Player)
    submitAsync {
        val player = e.whoClicked as Player
        throttleAction(ThrottleActionLink(player.uniqueId.toString(), "onInventoryClick"), ThrottleActionParam(player, e, e.currentItem, e.slot))
    }
}
@SubscribeEvent
fun onSwap(e: PlayerSwapHandItemsEvent) {
    submitAsync {
        throttleAction(ThrottleActionLink(e.player.uniqueId.toString(), "onSwap@Main"), ThrottleActionParam(e.player, e, e.mainHandItem, e.player.activeItemHand.ordinal))
        throttleAction(ThrottleActionLink(e.player.uniqueId.toString(), "onSwap@Off"), ThrottleActionParam(e.player, e, e.offHandItem))
    }
}