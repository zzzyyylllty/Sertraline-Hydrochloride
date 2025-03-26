package io.github.zzzyyylllty.sertraline.function.error

import io.github.zzzyyylllty.sertraline.data.DepazItems
import io.github.zzzyyylllty.sertraline.logger.severeL
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun throwNPEWithMessage(node: String, vararg args: Any) {
    severeL(node, args)
    throw NullPointerException()
}
