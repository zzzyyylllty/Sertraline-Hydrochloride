package io.github.zzzyyylllty.sertraline.command.subCommands

import com.beust.klaxon.Klaxon
import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.function.internalMessage.sendInternalMessages
import org.bukkit.command.CommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.PermissionDefault
import taboolib.common.platform.command.subCommand
import taboolib.platform.util.asLangText

@CommandHeader(
    name = "sertralineitem",
    aliases = ["items","needyitemi","depazi"],
    permission = "sertraline.command.item",
    description = "DEBUG Command of DepazItems.",
    permissionMessage = "",
    permissionDefault = PermissionDefault.OP,
    newParser = false,
)
object DepazItemCommand {

    @CommandBody
    val give = subCommand {
        dynamic("id") {
            execute<CommandSender> { sender, context, argument ->
                val id = context["id"]
            }
        }
    }


    @CommandBody
    val getItemMap = subCommand {
        execute<CommandSender> { sender, context, argument ->
            var message = sender.asLangText("COMMAND_DEBUG_ITEM", itemMap.size)
            for (entry in itemMap.entries) {
                message = "$message<br><white>${entry.key} <gray>- ${entry.value}"
            }
            sender.sendInternalMessages(message)
        }
    }


}
