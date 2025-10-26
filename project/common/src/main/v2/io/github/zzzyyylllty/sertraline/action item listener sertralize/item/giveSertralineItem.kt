package io.github.zzzyyylllty.sertraline.function.item

import buildItem
import io.github.zzzyyylllty.sertraline.data.SertralineItem
import io.github.zzzyyylllty.sertraline.logger.infoS
import io.github.zzzyyylllty.sertraline.logger.sendStringAsComponent
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.platform.util.asLangText
import taboolib.platform.util.giveItem


fun giveSertralineItem(item: SertralineItem,sender: CommandSender,receiver: Player? = sender as Player,amount: Int = 1) {
    if (receiver == null) {
        sender.infoS(sender.asLangText("PlayerNotFound"))
        return
    }
    val mm = MiniMessage.miniMessage()
    var message = sender.asLangText("ItemGiveSender", sender.name, mm.serialize(item.buildItem(receiver).displayName()), amount)
    sender.infoS(message)
    receiver.sendStringAsComponent(receiver.asLangText("ItemGive", sender.name, mm.serialize(item.buildItem(receiver).displayName()), amount))
    receiver.giveItem(item.buildItem(receiver),amount)
}