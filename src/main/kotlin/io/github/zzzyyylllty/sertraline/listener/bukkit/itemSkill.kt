package io.github.zzzyyylllty.sertraline.listener.bukkit

import com.willfp.eco.core.gui.player
import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.function.skill.applySkills
import io.lumine.mythic.lib.api.event.PlayerAttackEvent
import io.papermc.paper.event.player.PlayerPickItemEvent
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent
import org.bukkit.entity.Player
import org.bukkit.event.Event
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


val throttleSkill = throttle<ThrottleSkillLink, ThrottleSkillParam>(config.getLong("Skill.throttle-time", 500)){ link, data ->
    link.p.applySkills(link.str, data.e)
}


data class ThrottleSkillLink(
    val p: Player,
    val str: String,
)
data class ThrottleSkillParam(
    val e: Event,
    val i2 : ItemStack? = null,
    val islot : Int? = null
)

@SubscribeEvent
fun onInteractSkill(e: PlayerInteractEvent) {
    submitAsync {
        throttleSkill(ThrottleSkillLink(e.player, "onInteract"), ThrottleSkillParam(e, e.item, e.hand?.ordinal))
    }
}
@SubscribeEvent
fun onLeftClickSkill(e: PlayerInteractEvent) {
    if (e.hand == EquipmentSlot.OFF_HAND) submitAsync {
        throttleSkill(ThrottleSkillLink(e.player, "onLeftClick"), ThrottleSkillParam(e, e.item, e.hand?.ordinal))
    }
}
@SubscribeEvent
fun onRightClickSkill(e: PlayerInteractEvent) {
    if (e.hand == EquipmentSlot.HAND) submitAsync {
        throttleSkill(ThrottleSkillLink(e.player, "onRightClick"), ThrottleSkillParam(e, e.item, e.hand?.ordinal))
    }
}

@SubscribeEvent
fun onLoginSkill(e: PlayerLoginEvent) {
    submitAsync {
        throttleSkill(ThrottleSkillLink(e.player, "onLogin"), ThrottleSkillParam(e))
}
}

@SubscribeEvent
fun onPreAttackSkill(e: PrePlayerAttackEntityEvent) {
    submitAsync {
        throttleSkill(ThrottleSkillLink(e.player, "onPreAttack"), ThrottleSkillParam(e, e.player.activeItem, e.player.activeItemHand.ordinal))
}
}

@SubscribeEvent
fun onConsumeSkill(e: PlayerItemConsumeEvent) {
    if(config.getBoolean("item.no-replacement-consume",false)) e.replacement = null
    submitAsync {
        throttleSkill(ThrottleSkillLink(e.player, "onConsume"), ThrottleSkillParam(e, e.item, e.hand.ordinal))
    }
}

@SubscribeEvent
fun onBreakSkill(e: PlayerItemBreakEvent) {
    submitAsync {
        throttleSkill(ThrottleSkillLink(e.player, "onBreak"), ThrottleSkillParam(e, e.brokenItem))
    }
}
@SubscribeEvent
fun onDropSkill(e: PlayerDropItemEvent) {
    submitAsync {
        throttleSkill(ThrottleSkillLink(e.player, "onDrop"), ThrottleSkillParam(e, e.itemDrop.itemStack))
    }
}
@SubscribeEvent
fun onPickupSkill(e: PlayerPickupItemEvent) {
    submitAsync {
        throttleSkill(ThrottleSkillLink(e.player, "onPickup"), ThrottleSkillParam(e, e.item.itemStack, e.remaining))
    }
}
@SubscribeEvent
fun onClickInventorySkill(e: InventoryClickEvent) {
    submitAsync {
        throttleSkill(ThrottleSkillLink(e.player, "onClickInventory"), ThrottleSkillParam(e, e.currentItem, e.slot))
    }
}
@SubscribeEvent
fun onSwapSkill(e: PlayerSwapHandItemsEvent) {
    submitAsync {
        throttleSkill(ThrottleSkillLink(e.player, "onSwapMainHand"), ThrottleSkillParam(e, e.mainHandItem, e.player.activeItemHand.ordinal))
        throttleSkill(ThrottleSkillLink(e.player, "onSwapOffHand"), ThrottleSkillParam(e, e.offHandItem))
    }
}