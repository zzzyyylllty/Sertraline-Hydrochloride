package io.github.zzzyyylllty.sertraline.listener.action

import io.github.zzzyyylllty.sertraline.util.ActionHelper.throttleAction
import org.bukkit.event.player.PlayerRiptideEvent
import taboolib.common.platform.event.SubscribeEvent

// PlayerRiptideEvent 是 1.13+ 类，legacy12（v11200）编译面不存在；独立文件在 legacy12 构建中整体排除
@SubscribeEvent
fun onShootTrident(e: PlayerRiptideEvent) {
    val player = e.player
    val uuid = player.uniqueId.toString()
    val param = ThrottleActionParam(player, e, null, e.item)
    throttleAction(ThrottleActionLink(uuid, "onShootTrident"), param)
}
