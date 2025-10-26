package io.github.zzzyyylllty.sertraline.item

import io.github.projectunified.uniitem.all.AllItemProvider
import io.github.projectunified.uniitem.api.ItemKey
import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.logger.severeS
import net.momirealms.craftengine.bukkit.api.CraftEngineItems
import net.momirealms.craftengine.bukkit.plugin.BukkitCraftEngine
import net.momirealms.craftengine.core.util.Key
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.lang.asLangText


fun itemSource(input: Any?,player: Player?): ItemStack {
    val str = input.toString()
    val split = str.split(":").toMutableList()
    val key = split.first()
    split.removeFirst()
    val item = try {
        if (str.startsWith("craftengine:")) {
            if (player == null) CraftEngineItems.byId(Key.from(split.joinToString(":")))?.buildItemStack()
            else {
                val bukkitApi = BukkitCraftEngine.instance()
                CraftEngineItems.byId(Key.from(split.joinToString(":")))?.buildItemStack(bukkitApi.adapt(player))
            }
        } else {
            val provider = AllItemProvider()

            if (player != null) provider.item(ItemKey(key, split.joinToString(":")), player) else provider.item(ItemKey(key, split.joinToString(":")))
        }
    } catch (e: Exception) {
        severeS(console.asLangText("Error_External_ItemStack_Generation_Failed",str, e))
        null
    }
    return item ?: ItemStack(Material.STONE)
}



fun sertralineItemBuilder(template: ModernSItem,player: Player?,source: ItemStack? = itemSource(template.data["xbuilder:material"] ?: template.data["minecraft:material"], player)): ItemStack {
    return ItemStack(Material.STONE)
}
