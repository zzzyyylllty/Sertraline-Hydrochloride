package io.github.zzzyyylllty.sertraline.util.dependencies

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.item.itemSerializer
import io.github.zzzyyylllty.sertraline.util.DependencyHelper
import io.github.zzzyyylllty.sertraline.util.dependencies.MMOUtil.mmoAttributeCalculate
import io.lumine.mythic.lib.api.player.EquipmentSlot
import io.lumine.mythic.lib.api.player.MMOPlayerData
import io.lumine.mythic.lib.player.modifier.ModifierSource
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submit
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


                    // 一次性删除所有旧修饰符（包括不带槽位后缀和带槽位后缀的）
                    submit {
                        for (instance in statMap.instances) {
                            instance.removeIf { key ->
                                key == "sertraline_item" || key.startsWith("sertraline_item_")
                            }
                        }
                        // 为每个有物品的槽位添加新修饰符
                        itemList.forEach { (slot, itemBound) ->
                            val sItem = itemBound.sItem
                            val bItem = itemBound.bItem
                            val (defSource, defSlot) = when (slot) {
                                "mainhand" -> Pair(ModifierSource.MAINHAND_ITEM, EquipmentSlot.MAIN_HAND)
                                "offhand" -> Pair(ModifierSource.OFFHAND_ITEM, EquipmentSlot.OFF_HAND)
                                else -> Pair(
                                    ModifierSource.ARMOR, when (slot) {
                                        "helmet" -> EquipmentSlot.HEAD
                                        "chestplate" -> EquipmentSlot.CHEST
                                        "leggings" -> EquipmentSlot.LEGS
                                        "boots" -> EquipmentSlot.FEET
                                        else -> EquipmentSlot.OTHER
                                    }
                                )
                            }
                            mmoAttributeCalculate(
                                sItem,
                                playerData,
                                defSource,
                                defSlot,
                                slot,
                                bItem.type.name,
                                async = false
                            )
                        }
                    }
                }
            }
        }
    }
}
