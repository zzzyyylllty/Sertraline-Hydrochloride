package io.github.zzzyyylllty.sertraline.listener

import io.github.zzzyyylllty.sertraline.data.CraftingStationDataManager
import io.github.zzzyyylllty.sertraline.gui.CraftingStationManager
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync
import taboolib.expansion.releaseDataContainer
import taboolib.expansion.setupDataContainer

/**
 * 监听玩家加入/退出，处理合成站会话持久化。
 */
object CraftingStationListener {

    @SubscribeEvent
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player

        submitAsync {
            // 确保数据容器已初始化
            player.setupDataContainer()

            // 尝试恢复未完成的合成会话
            CraftingStationDataManager.restoreSession(player)
        }
    }

    @SubscribeEvent
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player

        // 必须在主线程同步执行：取消内存会话
        CraftingStationManager.cancelPlayerSession(player.uniqueId.toString())
        // releaseDataContainer 是快速同步操作，异步包装会延迟持久化
        player.releaseDataContainer()
    }
}
