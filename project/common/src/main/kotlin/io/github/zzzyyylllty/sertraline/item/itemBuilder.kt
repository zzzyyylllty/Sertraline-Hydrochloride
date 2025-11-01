package io.github.zzzyyylllty.sertraline.item

import io.github.projectunified.uniitem.all.AllItemProvider
import io.github.projectunified.uniitem.api.ItemKey
import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.Sertraline.itemManager
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.logger.severeS
import io.github.zzzyyylllty.sertraline.logger.warningS
import net.momirealms.craftengine.bukkit.api.CraftEngineItems
import net.momirealms.craftengine.bukkit.plugin.BukkitCraftEngine
import net.momirealms.craftengine.core.util.Key
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.module.lang.asLangText
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag
import taboolib.platform.util.buildItem


fun itemSource(input: Any?,player: Player?): ItemStack {
    val str = input.toString()
    val split = str.split(":").toMutableList()
    val key = split.first()
    split.removeFirst()
    devLog("str: $str | split: $split | key: $key")
    val item = try {
        if (!str.contains(":") || str.startsWith("minecraft:")) {
            devLog("Using vanilla item")
            buildItem(XMaterial.valueOf((if (split.isNotEmpty()) split[0] else if (str != "null") str else "GRASS_BLOCK").toUpperCase()))
        } else if (str.startsWith("craftengine:")) {
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
        devLog("ItemStack generation failed")
        e.printStackTrace()
        null
    }
    return item ?: run {
        devLog("Material is null, returning grass block")
        ItemStack(Material.GRASS_BLOCK)
    }
}



fun sertralineItemBuilder(template: ModernSItem,player: Player?,source: ItemStack? = null,amount: Int = 1): ItemStack {
    val itemSource = source ?: itemSource(template.data["xbuilder:material"] ?: template.data["minecraft:material"], player)
    val item = itemManager.processItem(template, itemSource, player)
    item.amount = amount

    val tag = item.getItemTag()
    tag.put("SERTRALINE_ID", template.key)
    tag.saveTo(item)
    return item
}
