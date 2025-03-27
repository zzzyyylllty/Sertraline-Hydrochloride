package io.github.zzzyyylllty.sertraline.data

import org.bukkit.inventory.ItemStack

data class DepazItems(
    val id: String,
    val originalItem: ItemStack,
    val actions: MutableList<Action>,
    val attributes: MutableList<Attribute>
)
