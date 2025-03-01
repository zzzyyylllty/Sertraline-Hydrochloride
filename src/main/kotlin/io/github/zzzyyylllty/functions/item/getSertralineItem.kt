package io.github.zzzyyylllty.functions.item

import io.github.zzzyyylllty.SertralineHydrochloride
import io.github.zzzyyylllty.data.SertralineItem
import org.bukkit.inventory.ItemStack
import pers.neige.neigeitems.utils.ItemUtils.getNbt

fun stlItem(item: ItemStack): SertralineItem? {
    return SertralineHydrochloride.items[item.getNbt().getString("DEPAZITEMS.SERTRALINE.ID")]
}

fun isStlItem(item: ItemStack): Boolean {
    return (item.getNbt().getString("DEPAZITEMS.SERTRALINE.ID") != null)
}
