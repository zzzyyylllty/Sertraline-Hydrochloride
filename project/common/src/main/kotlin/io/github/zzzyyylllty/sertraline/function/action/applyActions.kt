package io.github.zzzyyylllty.sertraline.function.action

import io.github.zzzyyylllty.sertraline.Sertraline.itemCache
import io.github.zzzyyylllty.sertraline.data.Action
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.function.data.getSavedData
import io.github.zzzyyylllty.sertraline.item.itemSerializer
import io.github.zzzyyylllty.sertraline.util.ComplexTypeHelper
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.submitAsync
import taboolib.module.nms.getItemTag

fun Player.applyActions(trigger: String, e: Event, ce: Cancellable?, i: ItemStack, abItem: ItemStack? = null) {
    devLog("Triggering action $trigger - ${e.eventName}")
    val player = this@applyActions
    val inv = player.inventory
    val id = i.getItemTag(true)["sertraline_id"]?.asString() ?: return
    if (((itemCache[id] as? Map<*,*>)?.get("actions") as? List<String>?)?.contains(trigger) != true) return
    var item : ModernSItem? = null
    val allActions = ((itemCache[id] as? Map<*,*>)?.get("preloadActions") as? LinkedHashMap<String, List<Action>>?) ?: run {
        devLog("Can't get preload actions for $trigger, fallback to complextypehelper to get as actions.")
        item = itemSerializer(id, player) ?: return
        ComplexTypeHelper(
            item.data["sertraline:actions"]
        ).getAsActions()
    }
    val actions = allActions?.get(trigger) ?: return
    actions.forEach {
        if (it.async ?: true) submitAsync {
            if (item == null) item = itemSerializer(id, player)
            it.runAction(player, getSavedData(item, i, true, player).collect(), i, e, ce, item!!, abItem)
        } else {
            if (item == null) item = itemSerializer(id, player)
            it.runAction(player, getSavedData(item, i, true, player).collect(), i, e, ce, item!!, abItem)
        }
    }
}