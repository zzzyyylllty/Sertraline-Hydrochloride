package io.github.zzzyyylllty.sertraline.attribute

import io.github.zzzyyylllty.sertraline.data.ModernSItem
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

data class ItemBound(
    val sItem: ModernSItem,
    val bItem: ItemStack
)

interface AttributeProvider {
    val name: String
    fun refreshAttributes(player: Player, itemList: Map<String, ItemBound>)
}
