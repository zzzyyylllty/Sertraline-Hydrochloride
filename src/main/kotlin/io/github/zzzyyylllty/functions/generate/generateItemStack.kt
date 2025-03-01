package io.github.zzzyyylllty.functions.generate

import io.github.zzzyyylllty.SertralineHydrochloride.console
import io.github.zzzyyylllty.data.SertralineItem
import io.github.zzzyyylllty.data.SertralinePartData
import io.github.zzzyyylllty.functions.generate.part.generateAttributePart
import io.github.zzzyyylllty.functions.generate.part.generateCompatbilityPart
import io.github.zzzyyylllty.functions.generate.part.generateSertralinePart
import io.github.zzzyyylllty.functions.load.part.resolveItemStack
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.severe
import taboolib.module.lang.asLangText

fun generateItemStack(data: SertralineItem, p: Player?): ItemStack {
    val resolved = resolveItemStack(data.sertralineData?.material.toString(), "Item ${data.sertralineData?.name}",p)
        ?: run {
            severe(console.asLangText("generate.itemstack.error", data.sertralineData?.name.toString()))
            return ItemStack(Material.STONE)
        }

    val compPart = generateCompatbilityPart(resolved, data)
    val stlPart = generateSertralinePart(compPart, data)
    val atbPart = generateAttributePart(stlPart, data)
    return atbPart
}
