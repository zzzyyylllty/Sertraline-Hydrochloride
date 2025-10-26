package io.github.zzzyyylllty.sertraline.util.component

import org.bukkit.inventory.ItemStack

interface ComponentHolder {
    fun addCustomData(item: ItemStack, key: String, value: String)
    fun getCustomData(item: ItemStack, key: String): String?
}