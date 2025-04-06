package io.github.zzzyyylllty.sertraline.function.generate

import io.github.zzzyyylllty.sertraline.data.DepazItems
import io.github.zzzyyylllty.sertraline.logger.severeL
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import pers.neige.neigeitems.utils.ItemUtils.getName
import taboolib.module.nms.getI18nName

fun ItemStack.getDisplayNameOrRegName(): String {
    val mm = MiniMessage.miniMessage()
    return this.itemMeta?.displayName ?: this.getName()
}