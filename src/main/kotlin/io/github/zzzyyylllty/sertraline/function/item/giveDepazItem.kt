package io.github.zzzyyylllty.sertraline.function.item

import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.function.generate.getDisplayNameOrRegName
import io.github.zzzyyylllty.sertraline.logger.infoS
import io.github.zzzyyylllty.sertraline.logger.sendStringAsComponent
import io.github.zzzyyylllty.sertraline.logger.severeL
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.asLangText
import taboolib.platform.util.giveItem
import kotlin.math.floor

fun Player?.giveDepazItem(id: String,amount: Int = 1,playerName: String,silent: Boolean) {
    if (this == null) {
        severeL("PLAYER_NOT_FOUND", playerName)
        return
    }
    this.giveDepazItem(id, amount, silent)
}
fun Player.giveDepazItem(id: String,amount: Int = 1,silent: Boolean) {
    val sender = this
    var item = itemMap[id]?.buildInstance(sender)?.buildItem() ?: run {
        sender.infoS(sender.asLangText("ITEM_NOT_FOUND", id))
        throw NullPointerException()
    }
    this.giveItem(item,amount)
    if (silent) sender.sendStringAsComponent(sender.asLangText("ITEM_GIVE", amount, item.getDisplayNameOrRegName(), id))
}
