package io.github.zzzyyylllty.sertraline.function.javascript

import com.github.retrooper.packetevents.protocol.dialog.input.Input
import io.github.zzzyyylllty.sertraline.item.adapter.transferBooleanToByte
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.ItemTag
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag

object ItemStackUtil {
    fun getItemTag(itemStack: ItemStack): ItemTag {
        return itemStack.getItemTag()
    }

    // 该方法 不会 不会 不会 改变原本物品!
    fun setItemTag(itemStack: ItemStack, tag: ItemTag): ItemStack {
        return itemStack.setItemTag(tag)
    }

    // 该方法会改变原本物品!
    fun setItemTagDirect(itemStack: ItemStack, tag: ItemTag): ItemStack {
        return tag.saveTo(itemStack)
    }

    // tb不支持 boolean NBT，需要包裹一层这个函数
    fun transferToByte(input: Any?): Any? {
        return transferBooleanToByte(input)
    }
}