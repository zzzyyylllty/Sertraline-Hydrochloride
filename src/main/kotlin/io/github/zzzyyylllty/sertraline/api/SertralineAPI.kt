package io.github.zzzyyylllty.sertraline.api

import io.github.zzzyyylllty.sertraline.Sertraline
import io.github.zzzyyylllty.sertraline.data.DepazItemInst
import io.github.zzzyyylllty.sertraline.data.DepazItems
import io.github.zzzyyylllty.sertraline.function.item.buildInstance
import io.github.zzzyyylllty.sertraline.function.item.buildItem
import io.github.zzzyyylllty.sertraline.function.item.getDepazId
import io.github.zzzyyylllty.sertraline.function.item.getDepazItem
import io.github.zzzyyylllty.sertraline.function.item.getDepazItemInst
import io.github.zzzyyylllty.sertraline.function.item.isDepazItem
import io.github.zzzyyylllty.sertraline.function.item.isDepazItemInList
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.function.*

public interface SertralineAPI {

    /**
     * Get Sertraline Item Object
     * */
    public fun getItem(s: String): DepazItems? {
        return Sertraline.itemMap[s]
    }

    /**
     * ItemObject -> ItemInstance
     * Instance: builded item, can parse to ItemStack
     * */
    public fun build(depazItems: DepazItems,p: Player): DepazItemInst? {
        return depazItems.buildInstance(p)
    }

    /**
     * parse Instance to ItemStack
     * */
    public fun deSerializeInst(inst: DepazItemInst): ItemStack? {
        return inst.buildItem()
    }

    /**
     * parse ItemStack to Instance
     * */
    public fun serializeInst(itemStack: ItemStack): DepazItemInst? {
        return itemStack.getDepazItemInst()
    }

    /**
     * Get Sertraline ID, null = not exist
     * */
    public fun getId(itemStack: ItemStack): String? {
        return itemStack.getDepazId()
    }

    /**
     * Is this item is Sertraline Item?
     * */
    public fun isVaildItem(itemStack: ItemStack): Boolean {
        return itemStack.isDepazItem()
    }

    /**
     * Is this item registered on file?
     * `true` - This item is in file
     * `false` - This item is not exist
     * */
    public fun isRegisteredItem(itemStack: ItemStack): Boolean {
        return itemStack.isDepazItemInList()
    }
}