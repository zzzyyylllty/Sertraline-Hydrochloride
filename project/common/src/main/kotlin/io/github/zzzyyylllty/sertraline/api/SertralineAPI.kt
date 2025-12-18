package io.github.zzzyyylllty.sertraline.api

import io.github.zzzyyylllty.sertraline.Sertraline
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.function.data.getSertralineId
import io.github.zzzyyylllty.sertraline.item.rebuild
import io.github.zzzyyylllty.sertraline.item.rebuildLore
import io.github.zzzyyylllty.sertraline.item.rebuildName
import io.github.zzzyyylllty.sertraline.item.rebuildUnsafe
import io.github.zzzyyylllty.sertraline.item.sertralineItemBuilder
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

public class SertralineAPIImpl: SertralineAPI {
    public val INSTANCE = SertralineAPIImpl()
}

public interface SertralineAPI {

    /**
     * Get Sertraline Item Object
     * */
    public fun getItem(s: String): ModernSItem? {
        return Sertraline.itemMap[s]
    }

    /**
     * Build a item for player.
     * @param [sItem] - Sertraline ID
     * @param [player] - Player
     * @param [source] - Item Build Source. null for auto.
     * @param [amount] - Item amount.
     * @param [overrideData] - Override SItem Data.
     * */
    public fun buildItem(sItem: String, player: Player?,source: ItemStack? = null,amount: Int = 1,overrideData: Map<String, Any?>? = null): ItemStack? {
        return sertralineItemBuilder(sItem, player, source, amount, overrideData)
    }

    /**
     * Get Sertraline ID, null = not exist
     * */
    public fun getId(itemStack: ItemStack): String? {
        return itemStack.getSertralineId()
    }

    /**
     * Is this item is Sertraline Item?
     * */
    public fun isVaildItem(itemStack: ItemStack): Boolean {
        return itemStack.getSertralineId() != null
    }

    /**
     * Is this item registered on items?
     * `true` - This item is in items
     * `false` - This item is not exist
     * */
    public fun isRegisteredItem(itemStack: ItemStack): Boolean {
        return Sertraline.itemMap[itemStack.getSertralineId()] != null
    }

    /**
     * Is this item registered on items?
     * `true` - This item is in items
     * `false` - This item is not exist
     * */
    public fun isRegisteredItem(s: String): Boolean {
        return Sertraline.itemMap[s] != null
    }


    /**
     * Re-Generate a lore for an Sertraline ItemStack.
     * Will change input item.
     * */
    fun rebuildLore(itemStack: ItemStack,player: Player?) {
        itemStack.rebuildLore(player)
    }

    /**
     * Re-Generate a display name for an Sertraline ItemStack.
     * Will change input item.
     * */
    fun rebuildName(itemStack: ItemStack,player: Player?) {
        itemStack.rebuildName(player)
    }

    /**
     * Re-Generate whole Sertraline ItemStack and return.
     * Will **NOT** change input item.
     * */
    fun rebuild(itemStack: ItemStack,player: Player?): ItemStack {
        return itemStack.rebuild(player)
    }

    /**
     * Re-Generate whole for an Sertraline ItemStack and write to original Item by **itemMeta**.
     * Will change input item.
     *
     * This is Unsafe method because it will lose 3 of 76 DataComponent in 1.21.4.
     * */
    fun rebuildUnsafe(itemStack: ItemStack,player: Player?) {
        itemStack.rebuildUnsafe(player)
    }
}