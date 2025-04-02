package io.github.zzzyyylllty.sertraline.function.item

import org.bukkit.entity.Player

/**
 * Get slots from STRING List.
 *
 * BOOT 36
 * ...
 * HELMET 39
 * OFFHAND 40
 * */
fun Player.getSlots(list: List<String>): List<Int> {
    var intList = mutableListOf<Int>()
    for (string in list) {
        if (string.startsWith("SLOT")) intList.add(string.split("_").toString().toInt()) else
            when (string) {
                "MAIN_HAND" -> intList.add(this.inventory.heldItemSlot)
                "OFF_HAND" -> intList.add(40)
                "HAND","ANY_HAND" -> intList.addAll(listOf(40, this.inventory.heldItemSlot))
                "EQUIP","EQUIPMENT" -> intList.addAll(listOf(36,37,38,39))
                "BOOTS","BOOT","FEET" -> intList.add(36)
                "LEGGINGS","LEG","LEGS" -> intList.add(37)
                "CHESTPLATE","CHEST" -> intList.add(38)
                "HELMET","HEAD" -> intList.add(39)
                "ANY_SLOT" -> for (i in 0..40) intList.add(i)
                "UNIVERSAL" -> intList.addAll(listOf(36,37,38,39,40, this.inventory.heldItemSlot))
            }
    }
    return intList
}
