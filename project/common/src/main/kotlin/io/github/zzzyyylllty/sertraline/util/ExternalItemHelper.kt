package io.github.zzzyyylllty.sertraline.util

import cn.gtemc.itembridge.api.ItemBridge
import cn.gtemc.itembridge.api.context.BuildContext
import cn.gtemc.itembridge.core.BukkitItemBridge
import io.github.zzzyyylllty.sertraline.logger.infoS
import io.github.zzzyyylllty.sertraline.logger.severeS
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack


object ExternalItemHelper {
    var itemBridge: ItemBridge<ItemStack?, Player?>? =
        BukkitItemBridge.builder()
            .detectSupportedPlugins(
                { p: String -> infoS("Hooked External item source: $p") },
                { p: String, e: Throwable -> severeS("Failed to hook External item " + p + ", because " + e.message) },
                { true }
            )
            .removeById("sertraline")
            .build()

    var itemBridgeAll: ItemBridge<ItemStack?, Player?>? =
        BukkitItemBridge.builder()
            .detectSupportedPlugins(
                { p: String -> infoS("Hooked External item source: $p") },
                { p: String, e: Throwable -> severeS("Failed to hook External item " + p + ", because " + e.message) },
                { true }
            )
//            .removeById("sertraline")
            .build()
    fun build(player: Player, plugin: String, name: String): ItemStack? {
        return try {
            itemBridgeAll?.build(plugin, name, player)?.get()
        } catch (e: NoSuchElementException) {
            null
        }
    }
    fun buildNoPlayer(plugin: String, name: String): ItemStack? {
        return try {
            itemBridgeAll?.build(plugin, name)?.get()
        } catch (e: NoSuchElementException) {
            null
        }
    }
}