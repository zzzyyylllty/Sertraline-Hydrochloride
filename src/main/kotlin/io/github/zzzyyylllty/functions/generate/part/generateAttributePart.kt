package io.github.zzzyyylllty.functions.generate.part

import de.tr7zw.nbtapi.NBT
import de.tr7zw.nbtapi.iface.ReadWriteNBT
import ink.ptms.chemdah.taboolib.module.chat.colored
import io.github.zzzyyylllty.data.SertralineItem
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.itemTagReader
import taboolib.platform.util.buildItem
import taboolib.platform.util.modifyLore


fun generateAttributePart(item: ItemStack,data: SertralineItem) : ItemStack {

    var returnItem = item
    val attrData = data.attribute
    val meta = item.itemMeta

    buildItem(returnItem).itemTagReader {
        set("DEPAZITEMS.ATTRIBUTES", attrData)

        write(buildItem(returnItem))
    }

    return returnItem
}
/*
  attributes:
    - type: MYTHIC_LIB
      # 支持：MYTHIC_LIB(MMOITEMS) ATTRIBUTE_PLUS MYTHIC_MOBS SX_ATTRIBUTE_2 SX_ATTRIBUTE_3
      attr: ATTACK_DAMAGE
      idef: null
      # 游戏内标识符号，若存在相同标识符号的相同属性在继承时会被大的一方覆盖，不储存在 NBT。
      # 不填写默认为物品名称_idef，例如 test_item_idef
      # 例如 靴子血量和裤子血量都为 ingame: abc，而靴子血量属性数值为114.0，裤子血量为514.0，实际只会计算514.0而不是叠加
      override: false
      # 替换父类所有相同种类相同attr的属性。默认true。
      chance: 100.0 # 100% 出现该属性
      amount: "<depaz.fixed.attack>" # 攻击调用val.attack中的数值
* */