package io.github.zzzyyylllty.functions.generate.part

import de.tr7zw.nbtapi.NBT
import de.tr7zw.nbtapi.iface.ReadWriteNBT
import de.tr7zw.nbtapi.utils.DataFixerUtil
import ink.ptms.chemdah.taboolib.module.chat.colored
import io.github.zzzyyylllty.data.SertralineItem
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import taboolib.module.nms.itemTagReader
import taboolib.platform.util.buildItem
import taboolib.platform.util.modifyLore


fun generateSertralinePart(item: ItemStack,data: SertralineItem) : ItemStack {

    var returnItem = item
    val sertData = data.sertralineData

    // Material
    returnItem.type = Material.valueOf(sertData?.material ?: "STONE")

    buildItem(returnItem).modifyLore {
        sertData?.lore?.forEach { line ->
            add(line)
            // TODO REPLACE WITH VARS
        }
        colored()
    }

    buildItem(returnItem).itemTagReader {
        set("DEPAZITEMS.SERTRALINE.UPDATE_ID", sertData?.updateId)
        set("DEPAZITEMS.SERTRALINE.FIXED_DATA", sertData?.fixedData)
        set("DEPAZITEMS.SERTRALINE.VARIABLES_DATA", sertData?.variablesData)

        write(buildItem(returnItem))
    }
/*
    val nbt : ReadWriteNBT = NBT.createNBTObject()

    NBT.modify(returnItem, {

        // Update id
        nbt.setDouble("SERTRALINE.UPDATE_ID", sertData?.updateId)
        nbt.setDouble("SERTRALINE.MODEL", sertData?.model)

        // Fixed / vars 存储
        sertData?.variablesData?.keys?.forEach { k ->
            nbt.setString("SERTRALINE.DATA_VARS.$k", sertData.variablesData[k].toString())
        }


    })
*/
    return returnItem
}
/*
* sertraline: # sertraline 属性
    name: '一个非常牛逼的物品'
    material: STONE
    nbts:
      - node: package
        value: needy.girl.overdose
    lore: # 描述，不保存在NBT。
      - '大家好啊，我是说的道理'
      - '今天来点大家想看的东西啊'
      - 'conditional|check papi %player_name% == "Mi_Yu"|啊米浴说的道理' # 当物品重载时，如果玩家名称为Mi_Yu，该条lore才会显示
      - 'madeonly,conditional|check papi %player_name% == "Mi_Yu"|啊米浴说的道理~~~' # 当物品被给予时，如果玩家名称为Mi_Yu，该条lore才会显示
      - 'lore-format|default' # 插入 default lore format.
    model: 10000 # Custom Model Data
    update-id: 1 # 类似 MMOItems Revision Id，储存于NBT。
    fixed:
      # 只有生成物品时会随机的量。
      # 储存在 NBT 内。
      - idef: attack
        type: value
        values: 1
      - idef: attack
        type: random
        values:
          min: 1
          max: 50
* */