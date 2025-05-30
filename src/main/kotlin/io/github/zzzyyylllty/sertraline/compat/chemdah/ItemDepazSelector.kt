package io.github.zzzyyylllty.connect.chemdah

import ink.ptms.chemdah.core.quest.selector.DataMatch
import ink.ptms.chemdah.core.quest.selector.Flags
import ink.ptms.chemdah.core.quest.selector.InferItem
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.function.item.getDepazId
import io.github.zzzyyylllty.sertraline.function.item.isDepazItem
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

/* https://wiki.ptms.ink/index.php?title=Chemdah_开发者文档:自定义物品选择器 */
class ItemDepazSelector(material: String, flags: List<Flags>, data: List<DataMatch>) : InferItem.Item(material, flags, data) {

    fun ItemStack.sertralineId(): String {
        return if (this.isDepazItem()) {
            this.getDepazId() ?: run {
                devLog("ID Is null")
                "@vanilla" }
        } else {
            devLog("Not depazItem")
            "@vanilla"
        }
    }

    override fun match(item: ItemStack): Boolean {
        return matchType(item.sertralineId()) && matchMetaData(item)
    }

}