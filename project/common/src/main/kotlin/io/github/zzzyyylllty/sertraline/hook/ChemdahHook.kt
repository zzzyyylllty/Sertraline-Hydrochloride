package io.github.zzzyyylllty.sertraline.hook

import ink.ptms.chemdah.api.event.InferItemHookEvent
import ink.ptms.chemdah.core.quest.selector.DataMatch
import ink.ptms.chemdah.core.quest.selector.Flags
import ink.ptms.chemdah.core.quest.selector.InferItem
import ink.ptms.chemdah.taboolib.module.kether.action.transform.CheckType
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.function.data.getSertralineId
import io.github.zzzyyylllty.sertraline.util.ItemTagUtil.parseMapNBT
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import taboolib.common.LifeCycle
import taboolib.common.platform.Ghost
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.kether.inferType
import taboolib.module.nms.getItemTag

@Ghost
@SubscribeEvent
fun chemdahItemHook(e: InferItemHookEvent) {
    when (e.id.lowercase()) {
        "sertraline", "depaz" -> {
            e.itemClass = ItemSertraline::class.java
        }
    }
}

class ItemSertraline(material: String, flags: List<Flags>, data: List<DataMatch>) : InferItem.Item(material, flags, data) {

    fun ItemStack.sID(): String {
        return this.getSertralineId() ?: "@vanilla"
    }

    override fun match(item: ItemStack): Boolean {
        return matchType(item.sID()) && matchMetaData(item)
    }

    override fun matchMetaData(item: ItemStack, itemMeta: ItemMeta?, dataMatch: DataMatch): Boolean {
        val key = dataMatch.key
        val matchValue = dataMatch.value
        val type = dataMatch.type
        return if (key.startsWith("var.")) {
            val tag = item.clone().getItemTag(true)
            val vars = tag["sertraline_data"]?.parseMapNBT()
            devLog("key: $key, type: $type")
            val value = vars?.get(key.substring("var.".length))
            when (type) {
                CheckType.EQUALS -> value == matchValue
                CheckType.EQUALS_NOT -> value != matchValue
                CheckType.EQUALS_NO_INFER -> value != matchValue
                CheckType.EQUALS_MEMORY -> value === matchValue
                CheckType.EQUALS_IGNORE_CASE -> value.toString().equals(matchValue.toString(), true)
                CheckType.GT -> value.toString().toDouble() > matchValue.toString().toDouble()
                CheckType.GTE -> value.toString().toDouble() >= matchValue.toString().toDouble()
                CheckType.LT -> value.toString().toDouble() < matchValue.toString().toDouble()
                CheckType.LTE -> value.toString().toDouble() < matchValue.toString().toDouble()
                CheckType.CONTAINS -> value.toString().contains(matchValue.toString())
                CheckType.IN -> (matchValue as? List<*>?)?.contains(value) ?: false
            }
        } else {
            super.matchMetaData(item, itemMeta, dataMatch)
        }
    }
}