package io.github.zzzyyylllty.sertraline.attribute

import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.item.itemSerializer
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.submitAsync

object AttributeManager {
    private val providers = LinkedHashMap<String, AttributeProvider>()

    fun register(provider: AttributeProvider) {
        providers[provider.name] = provider
        devLog("AttributeProvider registered: ${provider.name}")
    }

    fun unregister(name: String) {
        providers.remove(name)
    }

    fun unregisterAll() {
        providers.clear()
    }

    fun listProviders(): List<String> = providers.keys.toList()

    fun refreshAttributes(player: Player) {
        submitAsync {
            val inv = player.inventory
            val itemList = LinkedHashMap<String, ItemBound>()

            inv.itemInMainHand.let { bItem ->
                itemSerializer(bItem, player)?.let { itemList["mainhand"] = ItemBound(it, bItem) }
            }
            inv.itemInOffHand.let { bItem ->
                itemSerializer(bItem, player)?.let { itemList["offhand"] = ItemBound(it, bItem) }
            }
            inv.helmet?.let { bItem ->
                itemSerializer(bItem, player)?.let { itemList["helmet"] = ItemBound(it, bItem) }
            }
            inv.chestplate?.let { bItem ->
                itemSerializer(bItem, player)?.let { itemList["chestplate"] = ItemBound(it, bItem) }
            }
            inv.leggings?.let { bItem ->
                itemSerializer(bItem, player)?.let { itemList["leggings"] = ItemBound(it, bItem) }
            }
            inv.boots?.let { bItem ->
                itemSerializer(bItem, player)?.let { itemList["boots"] = ItemBound(it, bItem) }
            }

            submit {
                for (provider in providers.values) {
                    try {
                        provider.refreshAttributes(player, itemList)
                    } catch (e: Exception) {
                        devLog("AttributeProvider[${provider.name}] error: ${e.message}")
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}
