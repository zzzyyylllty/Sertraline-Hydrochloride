package io.github.zzzyyylllty.sertraline.listener.action

import io.github.zzzyyylllty.sertraline.util.ActionHelper.throttleAction
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityPickupItemEvent
import taboolib.common.platform.event.SubscribeEvent

// EntityPickupItemEvent 是 1.13+ 类，legacy12（v11200）编译面不存在；独立文件在 legacy12 构建中整体排除
@SubscribeEvent
fun onPickup(e: EntityPickupItemEvent) {
    val player = e.entity as? Player ?: return
    val uuid = player.uniqueId.toString()
    throttleAction(ThrottleActionLink(uuid, "onPickUp"), ThrottleActionParam(player, e, e, e.item.itemStack))
}
