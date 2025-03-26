package io.github.zzzyyylllty.sertraline.function.generate

import io.github.zzzyyylllty.sertraline.data.DepazItems
import io.github.zzzyyylllty.sertraline.logger.severeL
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.getI18nName

fun ItemStack.getDisplayNameOrRegName(): String {
    return this.itemMeta?.displayName ?: this.type.getI18nName()
}