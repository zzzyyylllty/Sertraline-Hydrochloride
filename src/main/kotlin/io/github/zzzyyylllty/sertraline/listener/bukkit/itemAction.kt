package io.github.zzzyyylllty.sertraline.listener.bukkit

import com.willfp.eco.core.gui.player
import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.function.action.applyActions
import io.lumine.mythic.lib.api.event.PlayerAttackEvent
import io.papermc.paper.event.player.PlayerPickItemEvent
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemBreakEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerPickupArrowEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import taboolib.common.function.throttle
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync
import taboolib.platform.util.attacker


val throttleAction = throttle<ThrottleActionLink, ThrottleActionParam>(config.getLong("action.throttle-time", 500)){ link, data ->
    link.p.applyActions(link.str, data.e)
}


data class ThrottleActionLink(
    val p: Player,
    val str: String,
)
data class ThrottleActionParam(
    val e: Event,
    val i2 : ItemStack? = null,
    val islot : Int? = null
)

@SubscribeEvent
fun onInteract(e: PlayerInteractEvent) {
    submitAsync {
        throttleAction(ThrottleActionLink(e.player, "onInteract"), ThrottleActionParam(e, e.item, e.hand?.ordinal))
    }
}
@SubscribeEvent
fun onLeftClick(e: PlayerInteractEvent) {
    if (e.hand == EquipmentSlot.OFF_HAND) submitAsync {
        throttleAction(ThrottleActionLink(e.player, "onLeftClick"), ThrottleActionParam(e, e.item, e.hand?.ordinal))
    }
}
@SubscribeEvent
fun onRightClick(e: PlayerInteractEvent) {
    if (e.hand == EquipmentSlot.HAND) submitAsync {
        throttleAction(ThrottleActionLink(e.player, "onRightClick"), ThrottleActionParam(e, e.item, e.hand?.ordinal))
    }
}

@SubscribeEvent
fun onLogin(e: PlayerLoginEvent) {
    submitAsync {
        throttleAction(ThrottleActionLink(e.player, "onLogin"), ThrottleActionParam(e))
}
}

@SubscribeEvent
fun onPreAttack(e: PrePlayerAttackEntityEvent) {
    submitAsync {
        throttleAction(ThrottleActionLink(e.player, "onPreAttack"), ThrottleActionParam(e, e.player.activeItem, e.player.activeItemHand.ordinal))
    }
}

@SubscribeEvent
fun onAttack(e: EntityDamageByEntityEvent) {
    if (e.attacker is Player)
    submitAsync {
        val player = e.attacker as Player
        throttleAction(ThrottleActionLink(player, "onAttack"), ThrottleActionParam(e, player.activeItem, player.activeItemHand.ordinal))
    }
}

@SubscribeEvent
fun onConsume(e: PlayerItemConsumeEvent) {
    if(config.getBoolean("item.no-replacement-consume",false)) e.replacement = null
    submitAsync {
        throttleAction(ThrottleActionLink(e.player, "onConsume"), ThrottleActionParam(e, e.item, e.hand.ordinal))
    }
}

@SubscribeEvent
fun onBreak(e: PlayerItemBreakEvent) {
    submitAsync {
        throttleAction(ThrottleActionLink(e.player, "onBreak"), ThrottleActionParam(e, e.brokenItem))
    }
}
@SubscribeEvent
fun onDrop(e: PlayerDropItemEvent) {
    submitAsync {
        throttleAction(ThrottleActionLink(e.player, "onDrop"), ThrottleActionParam(e, e.itemDrop.itemStack))
    }
}
@SubscribeEvent
fun onPickup(e: PlayerPickupItemEvent) {
    submitAsync {
        throttleAction(ThrottleActionLink(e.player, "onPickup"), ThrottleActionParam(e, e.item.itemStack, e.remaining))
    }
}
@SubscribeEvent
fun onClickInventory(e: InventoryClickEvent) {
    submitAsync {
        throttleAction(ThrottleActionLink(e.player, "onClickInventory"), ThrottleActionParam(e, e.currentItem, e.slot))
    }
}
@SubscribeEvent
fun onSwap(e: PlayerSwapHandItemsEvent) {
    submitAsync {
        throttleAction(ThrottleActionLink(e.player, "onSwapMainHand"), ThrottleActionParam(e, e.mainHandItem, e.player.activeItemHand.ordinal))
        throttleAction(ThrottleActionLink(e.player, "onSwapOffHand"), ThrottleActionParam(e, e.offHandItem))
    }
}