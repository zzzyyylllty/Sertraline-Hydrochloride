package io.github.zzzyyylllty.sertraline.command.subCommands

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.Sertraline.packMap
import io.github.zzzyyylllty.sertraline.command.createModernHelper
import io.github.zzzyyylllty.sertraline.function.item.buildItem
import io.github.zzzyyylllty.sertraline.load.getKey
import io.github.zzzyyylllty.sertraline.logger.infoS
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.PermissionDefault
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.module.configuration.util.asMap
import taboolib.platform.util.asLangText
import taboolib.platform.util.giveItem

@CommandHeader(
    name = "sertralinedebug",
    aliases = ["itemdebug","needyitemdebug","depazdebug"],
    permission = "sertraline.command.debug",
    description = "DEBUG Command of DepazItems.",
    permissionMessage = "",
    permissionDefault = PermissionDefault.OP,
    newParser = false,
)
object DepazDebugCommand {

    @CommandBody
    val main = mainCommand {
        createModernHelper()
    }

    @CommandBody
    val help = subCommand {
        createModernHelper()
    }

    @CommandBody
    val getPack = subCommand {
        dynamic("id") {
            execute<CommandSender> { sender, context, argument ->
                val id = context["id"]
                var message = sender.asLangText("CommandDebugGetPack", packMap[id].toString())
                sender.infoS(message)
            }
        }
    }
    @CommandBody
    val getItem = subCommand {
        dynamic("id") {
            execute<CommandSender> { sender, context, argument ->
                val id = context["id"]
                var message = sender.asLangText("CommandDebugGetItem", itemMap[id.getKey()].toString())
                sender.infoS(message)
            }
        }
    }

    @CommandBody
    val getPackMap = subCommand {
        execute<CommandSender> { sender, context, argument ->
            var message = sender.asLangText("CommandDebugGetPackMap", packMap.toString())
            sender.infoS(message)
        }
    }
    @CommandBody
    val getItemMap = subCommand {
        execute<CommandSender> { sender, context, argument ->
            var message = sender.asLangText("CommandDebugGetPackMap", itemMap.toString())
            sender.infoS(message)
        }
    }

    @CommandBody
    val give = subCommand {
        dynamic("id") {
            execute<CommandSender> { sender, context, argument ->
                val id = context["id"]
                (sender as Player).giveItem(itemMap[id.getKey()]?.buildItem(sender))
            }
        }
    }

    @CommandBody
    val getConfig = subCommand {
        execute<CommandSender> { sender, context, argument ->
            sender.infoS(config.toString())
        }
    }

}
