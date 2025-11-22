package io.github.zzzyyylllty.sertraline.util.dependencies

import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.item.itemSerializer
import io.github.zzzyyylllty.sertraline.util.DependencyHelper
import io.github.zzzyyylllty.sertraline.util.dependencies.MMOUtil.mmoAttributeCalculate
import io.lumine.mythic.lib.api.player.EquipmentSlot
import io.lumine.mythic.lib.api.player.MMOPlayerData
import io.lumine.mythic.lib.player.modifier.ModifierSource
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submitAsync

data class ItemBound(
    val sItem: ModernSItem,
    val bItem: ItemStack
)



object AttributeUtil {
    fun refreshAttributes(player: Player) {
        submitAsync {
            val inv = player.inventory
            val itemList = LinkedHashMap<String, ItemBound>()

            // 主手和副手
            inv.itemInMainHand.let { bItem -> itemSerializer(bItem, player)?.let { itemList["mainhand"] = ItemBound(it, bItem) } }
            inv.itemInOffHand.let { bItem -> itemSerializer(bItem, player)?.let { itemList["offhand"] = ItemBound(it, bItem) } }

            // 护甲
            inv.helmet?.let { bItem -> itemSerializer(bItem, player)?.let { itemList["helmet"] = ItemBound(it, bItem) } }
            inv.chestplate?.let { bItem -> itemSerializer(bItem, player)?.let { itemList["chestplate"] = ItemBound(it, bItem) } }
            inv.leggings?.let { bItem -> itemSerializer(bItem, player)?.let { itemList["leggings"] = ItemBound(it, bItem) } }
            inv.boots?.let { bItem -> itemSerializer(bItem, player)?.let { itemList["boots"] = ItemBound(it, bItem) } }


            if (DependencyHelper.mmLib) {
                val playerData = MMOPlayerData.getOrNull(player)
                if (playerData != null) {
                    val statMap = playerData.statMap
                    for (instance in statMap.instances) {
                        instance.removeIf { key -> key.startsWith("sertraline_item") }
                    }


                    // 主手和副手
                    itemList.get("mainhand")?.let { (sItem, bItem) ->
                        mmoAttributeCalculate(
                            sItem,
                            playerData,
                            ModifierSource.MAINHAND_ITEM,
                            EquipmentSlot.MAIN_HAND,
                            "mainhand",
                            bItem.type.name
                        )
                    }
                    itemList.get("offhand")?.let { (sItem, bItem) ->
                        mmoAttributeCalculate(
                            sItem,
                            playerData,
                            ModifierSource.OFFHAND_ITEM,
                            EquipmentSlot.OFF_HAND,
                            "offhand",
                            bItem.type.name
                        )
                    }

                    // 护甲
                    itemList.get("helmet")?.let { (sItem, bItem) ->
                        mmoAttributeCalculate(
                            sItem,
                            playerData,
                            ModifierSource.ARMOR,
                            EquipmentSlot.HEAD,
                            "helmet",
                            bItem.type.name
                        )
                    }
                    itemList.get("chestplate")?.let { (sItem, bItem) ->
                        mmoAttributeCalculate(
                            sItem,
                            playerData,
                            ModifierSource.ARMOR,
                            EquipmentSlot.CHEST,
                            "chestplate",
                            bItem.type.name
                        )
                    }
                    itemList.get("leggings")?.let { (sItem, bItem) ->
                        mmoAttributeCalculate(
                            sItem,
                            playerData,
                            ModifierSource.ARMOR,
                            EquipmentSlot.LEGS,
                            "leggings",
                            bItem.type.name
                        )
                    }
                    itemList.get("boots")?.let { (sItem, bItem) ->
                        mmoAttributeCalculate(
                            sItem,
                            playerData,
                            ModifierSource.ARMOR,
                            EquipmentSlot.FEET,
                            "boots",
                            bItem.type.name
                        )
                    }
                }
            }
        }
    }
}
