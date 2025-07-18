package io.github.zzzyyylllty.sertraline.command.subCommands

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.Sertraline.packMap
import io.github.zzzyyylllty.sertraline.command.createModernHelper
import io.github.zzzyyylllty.sertraline.data.SertralineItem
import io.github.zzzyyylllty.sertraline.function.item.buildItem
import io.github.zzzyyylllty.sertraline.load.getKey
import io.github.zzzyyylllty.sertraline.logger.infoS
import io.github.zzzyyylllty.sertraline.logger.sendStringAsComponent
import io.github.zzzyyylllty.sertraline.logger.severeS
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.PermissionDefault
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.player
import taboolib.common.platform.command.subCommand
import taboolib.common.util.asList
import taboolib.module.configuration.util.asMap
import taboolib.platform.util.asLangText
import taboolib.platform.util.giveItem
import taboolib.platform.util.sendError

@CommandHeader(
    name = "sertralineitem",
    permission = "sertraline.command.debug",
    description = "DEBUG Command of DepazItems.",
    permissionMessage = "",
    permissionDefault = PermissionDefault.OP,
    newParser = false,
)
object DepazItemCommand {

    @CommandBody
    val main = mainCommand {
        createModernHelper()
    }

    @CommandBody
    val help = subCommand {
        createModernHelper()
    }


    @CommandBody
    val give = subCommand {
        dynamic("id") {
            execute<CommandSender> { sender, context, argument ->
                if (sender !is Player) {
                    sender.severeS(sender.asLangText("NotPlayer"))
                } else {
                    val id = context["id"]
                    val item = itemMap[id.getKey()]
                    if (item != null) {
                        giveSertralineItem(item, sender)
                    } else {
                        sender.severeS(sender.asLangText("ItemNotFound"))
                    }
                }
            }
            player("player") {
                execute<CommandSender> { sender, context, argument ->
                    val tabooPlayer = context.player("player")
                    // 转化为Bukkit的Player
                    val bukkitPlayer = tabooPlayer.castSafely<Player>()
                    val id = context["id"]
                    val item = itemMap[id.getKey()]
                    if (item != null) {
                        giveSertralineItem(item, sender, bukkitPlayer)
                    } else {
                        sender.severeS(sender.asLangText("ItemNotFound"))
                    }
                }
                dynamic("amount") {
                    execute<CommandSender> { sender, context, argument ->
                        val tabooPlayer = context.player("player")
                        // 转化为Bukkit的Player
                        val bukkitPlayer = tabooPlayer.castSafely<Player>()
                        val id = context["id"]
                        val item = itemMap[id.getKey()]
                        if (item != null) {
                            giveSertralineItem(item, sender, bukkitPlayer, context["amount"].toInt())
                        } else {
                            sender.severeS(sender.asLangText("ItemNotFound"))
                        }
                    }
                    suggestion<CommandSender>(uncheck = true) { sender, context ->
                        listOf("1","16","64","128")
                    }
                }
            }
            suggestion<CommandSender>(uncheck = true) { sender, context ->
                val list = mutableListOf<String>()
                for (key in itemMap.keys) {
                    list.add(key.serialize())
                }
                return@suggestion list
            }
        }
    }

}

fun giveSertralineItem(item: SertralineItem,sender: CommandSender,receiver: Player? = sender as Player,amount: Int = 1) {
    if (receiver == null) {
        sender.infoS(sender.asLangText("PlayerNotFound"))
        return
    }
    var message = sender.asLangText("ItemGiveSender", sender.name, item.buildItem(receiver).displayName(), amount)
    sender.infoS(message)
    receiver.sendStringAsComponent(receiver.asLangText("ItemGive", sender.name, item.buildItem(receiver).displayName(), amount))
    receiver.giveItem(item.buildItem(receiver),amount)
}