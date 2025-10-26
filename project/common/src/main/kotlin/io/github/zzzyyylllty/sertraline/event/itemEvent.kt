package io.github.zzzyyylllty.sertraline.event

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.platform.type.BukkitProxyEvent

class ItemLoadEvent(val itemKey: String, val arguments: Map<String, Any?>, var itemData: Map<String, Any?>) : BukkitProxyEvent() {
}
class ItemGenerateEvent(val itemKey: String, val player: Player? = null, val amount: Int, val itemStack: ItemStack) : BukkitProxyEvent() {
}