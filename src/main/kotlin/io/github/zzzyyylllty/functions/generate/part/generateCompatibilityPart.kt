package io.github.zzzyyylllty.functions.generate.part

import com.mojang.datafixers.DataFixerBuilder
import com.mojang.datafixers.DataFixerUpper
import de.tr7zw.nbtapi.NBT
import de.tr7zw.nbtapi.iface.ReadWriteNBT
import de.tr7zw.nbtapi.plugin.NBTAPI
import de.tr7zw.nbtapi.utils.DataFixerUtil
import io.github.zzzyyylllty.SertralineHydrochloride.console
import io.github.zzzyyylllty.data.CompatibilityData
import io.github.zzzyyylllty.data.MMOItemsComp
import io.github.zzzyyylllty.data.SertralineItem
import io.github.zzzyyylllty.data.SertralineItemData
import io.github.zzzyyylllty.debugMode.debugLog
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.pluginVersion
import taboolib.common.platform.function.warning
import taboolib.module.lang.asLangText
import taboolib.module.nms.minecraftServerObject

fun generateCompatbilityPart(item: ItemStack,data: SertralineItem) : ItemStack {

    var returnItem = item
    val compData = data.compatibilityData ?: return returnItem

    val nbt : ReadWriteNBT = NBT.createNBTObject()

    NBT.modify(item, {
        nbt.setString("MMOITEMS_ITEM_TYPE", compData.mmoItemsComp.type)
        nbt.setString("MMOITEMS_ITEM_ID", compData.mmoItemsComp.id)
        nbt.setLong("MMOITEMS_REVISION_ID", compData.mmoItemsComp.revid)
        nbt.setString("MMOITEMS_TIER", compData.mmoItemsComp.tier)
    })

    // 更新 NBT
    val updatedNBT = DataFixerUtil.fixUpItemData(nbt, DataFixerUtil.VERSION1_20_4, DataFixerUtil.getCurrentVersion())

    return returnItem
}
/*
* testItem:
  compatibility:
    mmoitems: # 添加 MMOItems NBT以与大部分支持MI的插件自动进行兼容，使用该选项的同时如果下文属性使用生成MI的NBT，必须关闭MI的物品删除自动失效功能。
      type: 'CONSUMABLE'
      id: 'DEPAZ_PILLS'
      revid: 114514 # 如果你不想让你的物品被MI自动更新，关闭MI的自动更新功能或确保它不低于MI内同名同种物品。
      tier: RARE
* */