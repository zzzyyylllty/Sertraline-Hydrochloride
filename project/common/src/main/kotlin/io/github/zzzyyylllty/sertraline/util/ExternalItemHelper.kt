package io.github.zzzyyylllty.sertraline.util

import cn.gtemc.itembridge.api.ItemBridge
import cn.gtemc.itembridge.api.Provider
import cn.gtemc.itembridge.api.context.BuildContext
import cn.gtemc.itembridge.api.context.ContextKey
import cn.gtemc.itembridge.core.BukkitItemBridge
import io.github.zzzyyylllty.sertraline.Sertraline
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
            .register(SertralinePrivateIBProvider)
            .build()

    fun build(player: Player?, plugin: String, id: String): ItemStack? {
        return try {
            itemBridgeAll?.build(plugin, id, player)?.get()
        } catch (e: NoSuchElementException) {
            null
        }
    }

    fun buildNoPlayer(plugin: String, id: String): ItemStack? {
        return try {
            itemBridgeAll?.build(plugin, id)?.get()
        } catch (e: NoSuchElementException) {
            null
        }
    }
}

private object SertralinePrivateIBProvider : Provider<ItemStack?, Player?> {
    override fun plugin(): String {
        return "sertralineprivate"
    }

    private val ITEM_AMOUNT: ContextKey<Int> = ContextKey.of(Int::class.java, "item_amount")
    private val SERTRALINE_DATA: ContextKey<Map<*, *>> = ContextKey.of(Map::class.java, "sertraline_data")

    override fun buildOrNull(
        id: String,
        player: Player?,
        context: BuildContext
    ): ItemStack? {
        val amount = context.getOrDefault(ITEM_AMOUNT, 1)
        @Suppress("UNCHECKED_CAST")
        val vars = context.getOrNull(SERTRALINE_DATA) as? Map<String, Any?>
        return if (vars != null) {
            val overrideData = contextToMap(context)?.filterKeys { it != "sertraline_data" }
            Sertraline.api().buildDataItem(id, player, null, amount, overrideData, vars)
        } else {
            val overrideData = contextToMap(context)
            Sertraline.api().buildItem(id, player, null, amount, overrideData)
        }
    }

    override fun idOrNull(item: ItemStack): String? {
        return Sertraline.api().getId(item)
    }

    override fun `is`(item: ItemStack): Boolean {
        return Sertraline.api().isValidItem(item)
    }

    override fun has(id: String): Boolean {
        return Sertraline.api().isRegisteredItem(id)
    }
}

/** Converts [BuildContext] to a Map for passing as overrideData to [SertralineAPI.buildItem]. */
private fun contextToMap(context: BuildContext): Map<String, Any?>? {
    val data = context.contextData() ?: return null
    if (data.isEmpty()) return null
    val result = LinkedHashMap<String, Any?>(data.size)
    for ((key, supplier) in data) {
        if (key != null && supplier != null) {
            result[key.key()] = supplier.get()
        }
    }
    return result
}
