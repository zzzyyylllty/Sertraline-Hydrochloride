package io.github.zzzyyylllty.nms.item

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.util.unsafeLazy
import taboolib.module.nms.nmsProxy

abstract class NMS12000 {
    abstract fun sendItem(player: Player,item: ItemStack)

    companion object {
        val INSTANCE by unsafeLazy {
            nmsProxy<NMS12000>()
        }
    }
}