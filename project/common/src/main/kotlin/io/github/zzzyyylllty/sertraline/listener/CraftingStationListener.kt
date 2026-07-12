package io.github.zzzyyylllty.sertraline.listener

import io.github.zzzyyylllty.sertraline.data.CraftingStationDataManager
import io.github.zzzyyylllty.sertraline.gui.CraftingStationManager
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync

/**
 * 监听玩家加入/退出，处理合成站会话持久化。
 */
object CraftingStationListener {

    @SubscribeEvent
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player

        submitAsync {
            // 尝试恢复未完成的合成会话
            CraftingStationDataManager.restoreSession(player)
        }
    }

    @SubscribeEvent
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player

        // 取消内存会话；持久化数据已由 PTC Object 自动写入
        CraftingStationManager.cancelPlayerSession(player.uniqueId.toString())
    }
}
