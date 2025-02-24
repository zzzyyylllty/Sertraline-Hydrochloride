package io.github.zzzyyylllty.connect.chemdah

import org.bukkit.inventory.ItemStack

/* https://wiki.ptms.ink/index.php?title=Chemdah_开发者文档:自定义物品选择器 */
class ItemDepazSelector(material: String, flags: List, data: Map) : InferItem.Item(material, flags, data) {

    fun ItemStack.zaphkielId(): String {
        val itemStream = ZaphkielAPI.read(this)
        return if (itemStream.isExtension()) itemStream.getZaphkielName() else "@vanilla"
    }

    override fun match(item: ItemStack): Boolean {
        return matchType(item.zaphkielId()) && matchMetaData(item)
    }

    override fun matchMetaData(item: ItemStack, itemMeta: ItemMeta, key: String, value: String): Boolean {
        return if (key.startsWith("data.")) {
            ZaphkielAPI.read(item).getZaphkielData()[key.substring("data.".length)]?.asString().equals(value, true)
        } else {
            super.matchMetaData(item, itemMeta, key, value)
        }
    }

}