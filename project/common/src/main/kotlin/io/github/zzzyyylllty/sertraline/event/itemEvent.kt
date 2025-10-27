package io.github.zzzyyylllty.sertraline.event

import io.github.zzzyyylllty.sertraline.data.ModernSItem
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.platform.type.BukkitProxyEvent

class ItemLoadEvent(val itemKey: String, val arguments: Map<String, Any?>, var itemData: MutableMap<String, Any?>) : BukkitProxyEvent() {
}