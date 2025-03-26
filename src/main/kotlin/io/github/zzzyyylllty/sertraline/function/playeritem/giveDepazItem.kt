package io.github.zzzyyylllty.sertraline.function.playeritem

import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.function.generate.getDisplayNameOrRegName
import io.github.zzzyyylllty.sertraline.function.internalMessage.sendInternalMessages
import io.github.zzzyyylllty.sertraline.logger.severeL
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.platform.util.asLangText
import taboolib.platform.util.giveItem

fun Player?.giveDepazItem(id: String,amount: Int = 1,playerName: String) {
    if (this == null) {
        severeL("PLAYER_NOT_FOUND", playerName)
        return
    }
    this.giveDepazItem(id, amount)
}
fun Player.giveDepazItem(id: String,amount: Int = 1) {
    val sender = this
    var item = itemMap[id]?.originalItem ?: run {
        sender.sendInternalMessages(sender.asLangText("ITEM_NOT_FOUND", id))
        throw NullPointerException()
    }
    item.amount = amount
    this.giveItem(item) // TODO
    sender.sendInternalMessages(sender.asLangText("ITEM_GIVE", amount, id, item.getDisplayNameOrRegName()))
}