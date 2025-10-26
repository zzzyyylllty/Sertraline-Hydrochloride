package io.github.zzzyyylllty.sertraline.function.adapter

import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.papermc.paper.datacomponent.DataComponentType
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.WrittenBookContent
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.ItemTag
import taboolib.module.nms.setItemTag

fun minecraftAdapter(sItem: ModernSItem, player: Player, itemStack: ItemStack): ItemStack {
    val item = itemStack
    return item
}
