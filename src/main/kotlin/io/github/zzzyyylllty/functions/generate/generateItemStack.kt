package io.github.zzzyyylllty.functions.generate

import io.github.zzzyyylllty.SertralineHydrochloride.console
import io.github.zzzyyylllty.data.SertralinePartData
import io.github.zzzyyylllty.functions.load.part.resolveItemStack
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.severe
import taboolib.module.lang.asLangText

fun generateItemStack(data: SertralinePartData, p: Player?): ItemStack {
    return resolveItemStack(data.material.toString(), "Item ${data.name}",p)
        ?: run {
            severe(console.asLangText("generate.itemstack.error", data.name.toString()))
            return ItemStack(Material.STONE)
        }
}
