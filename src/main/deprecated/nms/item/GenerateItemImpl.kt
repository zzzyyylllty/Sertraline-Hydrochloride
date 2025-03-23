package io.github.zzzyyylllty.nms.item

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.util.unsafeLazy
import taboolib.module.nms.nmsProxy
import taboolib.platform.util.giveItem

class NMS12000Impl : NMS12000() {

    override fun sendItem(player: Player, item: ItemStack) {
        player.giveItem(item)
    }
}