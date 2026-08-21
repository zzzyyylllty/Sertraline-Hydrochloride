package io.github.zzzyyylllty.sertraline.function.data

import io.github.zzzyyylllty.sertraline.config.asListEnhanced
import io.github.zzzyyylllty.sertraline.data.ItemData
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.function.kether.evalKether
import io.github.zzzyyylllty.sertraline.util.ItemTagUtil.parseMapNBT
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.getItemTag


fun getSavedData(item: ModernSItem?,itemStack: ItemStack?,evalDynamic: Boolean,player: Player?): ItemData {

    val itemVal = item?.getDeepData("sertraline:vals") as Map<String, Any?>?
    val itemVar = mutableMapOf<String, Any?>()
    ((itemStack?.getItemTag(true)["sertraline_data"]?.parseMapNBT()))?.let {
        (it).let { it ->
            devLog("SavedData: $it")
            itemVar.putAll(it)
        }
    } ?: run {
        (item?.getDeepData("sertraline:vars") as Map<String, Any?>?)?.let { itemVar.putAll(it) }
    }
    val itemDynamic = (item?.getDeepData("sertraline:dynamics") as Map<String, Any?>?)
    // context 是运行时独立数据，不持久化到 NBT，仅存在于构建时模板
    val itemContext = (item?.getDeepData("sertraline:context") as Map<String, Any?>?)

    val data = ItemData(itemVal, itemVar, itemDynamic, itemContext, item?.key)

    if (evalDynamic && itemDynamic != null) {
        val newDynamic = mutableMapOf<String, Any>()
        for (entry in itemDynamic) {
            entry.value.asListEnhanced()?.evalKether(player, data.collect())?.get()?.let { newDynamic[entry.key] = it }
        }
        return ItemData(itemVal, itemVar, newDynamic, itemContext, item.key)
    }
    return data
}

fun ItemStack.getSertralineId(): String? {

    val tag = this.clone().getItemTag(true)
    return tag["sertraline_id"]?.asString()
}

/**
 * context 值转字符串：Player/Entity 返回其 name，避免输出 CraftPlayer{name=...} 之类的类描述。
 */
fun Any?.contextValue(): String? = when (this) {
    is Player -> name
    is Entity -> name
    is String -> this
    is Number -> toString()
    is Boolean -> toString()
    else -> toString()
}