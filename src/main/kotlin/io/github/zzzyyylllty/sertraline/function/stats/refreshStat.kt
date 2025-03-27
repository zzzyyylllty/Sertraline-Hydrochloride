package io.github.zzzyyylllty.sertraline.function.stats

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.function.item.getDepazItem
import io.github.zzzyyylllty.sertraline.function.item.getDepazItemOrFail
import io.github.zzzyyylllty.sertraline.function.item.isDepazItem
import io.github.zzzyyylllty.sertraline.function.item.isDepazItemInList
import org.bukkit.entity.Player
import taboolib.common.platform.function.submitAsync

fun Player.refreshStat() {
    val inv = this.inventory
    submitAsync {
        devLog("")
        val slotEnabled = config.getBoolean("attribute.slot-condition")
        if (slotEnabled) for (slot in 0..40) {
            val i = inv.getItem(slot)
            if (i.isDepazItemInList()) {
                for (atb in i.getDepazItemOrFail().attributes) {
                    // TODO
                }
            }
        }
    }
}
