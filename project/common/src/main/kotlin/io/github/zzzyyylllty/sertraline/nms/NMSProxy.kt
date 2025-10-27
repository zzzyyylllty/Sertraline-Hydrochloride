package io.github.zzzyyylllty.sertraline.nms

import org.bukkit.entity.Player
import taboolib.common.util.unsafeLazy
import taboolib.module.nms.nmsProxy

abstract class NMS {
    abstract fun sendBossBar(player: Player, message: String, progress: Float, overlay: String, color: String)
    companion object {
        val INSTANCE by unsafeLazy {
            nmsProxy<NMS>()
        }
    }
}

