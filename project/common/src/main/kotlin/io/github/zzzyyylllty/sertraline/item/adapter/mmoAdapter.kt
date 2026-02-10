package io.github.zzzyyylllty.sertraline.item.adapter

import io.github.zzzyyylllty.sertraline.config.AdapterUtil
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.util.loreformat.handleLoreFormat
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag
import java.util.LinkedHashMap

fun mmoAdapter(item: ItemStack, sItem: ModernSItem, player: Player?): ItemStack {

    val fix = (sItem.getDeepData("mmo:fix-attack-speed") as? Boolean?)
    if (fix == null) {
        devLog("mmo build setting is null or empty, skipping adapting.")
        return item
    }
    val meta = item.itemMeta
    meta.addAttributeModifier(
        Attribute.ATTACK_SPEED,
        AttributeModifier(
            NamespacedKey.fromString("mmoitems:decoy")!!,
            0.0,
            AttributeModifier.Operation.ADD_NUMBER
        )
    )
    item.setItemMeta(meta)

    return item

}



