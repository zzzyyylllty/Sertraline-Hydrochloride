package io.github.zzzyyylllty.sertraline.data

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

data class DepazItems(
    val id: String,
    val originalItem: ItemStack,
    val actions: LinkedHashMap<String, Action>
)
