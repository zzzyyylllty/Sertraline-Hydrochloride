package io.github.zzzyyylllty.sertraline.function.javascript

import com.github.retrooper.packetevents.protocol.dialog.input.Input
import io.github.zzzyyylllty.sertraline.item.adapter.transferBooleanToByte
import io.github.zzzyyylllty.sertraline.item.rebuild
import io.github.zzzyyylllty.sertraline.item.rebuildBypassKeepData
import io.github.zzzyyylllty.sertraline.item.rebuildLore
import io.github.zzzyyylllty.sertraline.item.rebuildName
import io.github.zzzyyylllty.sertraline.item.rebuildUnsafe
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.ItemTag
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag

object ItemStackUtil {
    fun getItemTag(itemStack: ItemStack): ItemTag {
        return itemStack.getItemTag()
    }

    // This method does NOT change the original item!
    fun setItemTag(itemStack: ItemStack, tag: ItemTag): ItemStack {
        return itemStack.setItemTag(tag)
    }

    // This method will change the original item!
    fun setItemTagDirect(itemStack: ItemStack, tag: ItemTag): ItemStack {
        return tag.saveTo(itemStack)
    }

    // TabooLib does not support boolean NBT; wrap it with this function
    fun transferToByte(input: Any?): Any? {
        return transferBooleanToByte(input)
    }

    fun rebuildLore(itemStack: ItemStack,player: Player?) {
        itemStack.rebuildLore(player)
    }

    fun rebuildName(itemStack: ItemStack,player: Player?) {
        itemStack.rebuildName(player)
    }

    fun rebuild(itemStack: ItemStack,player: Player?): ItemStack {
        return itemStack.rebuild(player)
    }

    fun rebuildBypassKeepData(itemStack: ItemStack,player: Player?): ItemStack {
        return itemStack.rebuildBypassKeepData(player)
    }

    fun rebuildUnsafe(itemStack: ItemStack,player: Player?) {
        itemStack.rebuildUnsafe(player)
    }

    // -- path / JSON helpers --
    // Fallback across versions at TabooLib's NMS layer; paths use "." for levels, e.g. "sertraline:vars.foo".
    // Named with a suffix (Value/String) to avoid GraalJS ambiguity between same-named overloads.

    /** Reads an NBT value by a "."-separated path, returning null if it does not exist. */
    fun getItemTagValue(itemStack: ItemStack, path: String): Any? {
        return itemStack.getItemTag().getDeep(path)?.unsafeData()
    }

    /** Writes NBT by path, returning a new item (does not change the original; same as setItemTag). */
    fun setItemTagValue(itemStack: ItemStack, path: String, value: Any?): ItemStack {
        val tag = itemStack.getItemTag()
        // use the set operator instead of putDeep so Boolean/UUID degrade to String and null removes
        tag.set(path, value)
        return itemStack.setItemTag(tag)
    }

    /** Writes NBT by path, directly changing the original item (same as setItemTagDirect). */
    fun setItemTagValueDirect(itemStack: ItemStack, path: String, value: Any?): ItemStack {
        val tag = itemStack.getItemTag()
        tag.set(path, value)
        return tag.saveTo(itemStack)
    }

    /** Removes NBT by path, returning a new item (does not change the original). */
    fun removeItemTagValue(itemStack: ItemStack, path: String): ItemStack {
        val tag = itemStack.getItemTag()
        tag.removeDeep(path)
        return itemStack.setItemTag(tag)
    }

    /** Removes NBT by path, directly changing the original item. */
    fun removeItemTagValueDirect(itemStack: ItemStack, path: String): ItemStack {
        val tag = itemStack.getItemTag()
        tag.removeDeep(path)
        return tag.saveTo(itemStack)
    }

    /** Checks whether an NBT value exists at the path. */
    fun hasItemTagValue(itemStack: ItemStack, path: String): Boolean {
        return itemStack.getItemTag().getDeep(path) != null
    }

    /** Serializes the item's NBT into a JSON string (reversible; corresponds to setItemTagString). */
    fun getItemTagString(itemStack: ItemStack): String {
        return itemStack.getItemTag().toJson()
    }

    /** Overwrites the item's NBT with a JSON string, returning a new item (does not change the original). */
    fun setItemTagString(itemStack: ItemStack, json: String): ItemStack {
        return itemStack.setItemTag(ItemTag.fromJson(json))
    }

    /** Overwrites the item's NBT with a JSON string, directly changing the original item. */
    fun setItemTagStringDirect(itemStack: ItemStack, json: String): ItemStack {
        return ItemTag.fromJson(json).saveTo(itemStack)
    }

    /**
     * Returns whether two item stacks are the same item, via plain ItemStack equality.
     * Every Sertraline item carries a sertraline identity (material + NBT), so a direct
     * `==` is a valid "same item" check. Useful inside compare-filter conditions, e.g.
     *   `ItemStackUtil.isSelf(currentItem, compareItem)`
     */
    fun isSelf(a: ItemStack, b: ItemStack): Boolean {
        return a == b
    }
}
