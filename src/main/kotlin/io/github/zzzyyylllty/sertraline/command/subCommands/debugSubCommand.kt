package io.github.zzzyyylllty.sertraline.command.subCommands

import com.beust.klaxon.Klaxon
import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.Sertraline.templateMap
import io.github.zzzyyylllty.sertraline.function.internalMessage.sendInternalMessages
import org.bukkit.command.CommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.PermissionDefault
import taboolib.common.platform.command.subCommand
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.util.asMap
import taboolib.platform.util.asLangText
import java.io.File
import java.util.LinkedHashMap
import kotlin.String

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
    val getItem = subCommand {
        dynamic("id") {
            execute<CommandSender> { sender, context, argument ->
                val id = context["id"]
                var message = sender.asLangText("COMMAND_DEBUG_ITEM", Klaxon().toJsonString(itemMap[id]))
                sender.sendInternalMessages(message)
            }
        }
    }


    @CommandBody
    val getItemMap = subCommand {
        execute<CommandSender> { sender, context, argument ->
            var message = sender.asLangText("COMMAND_DEBUG_ITEMMAP", itemMap.size)
            for (entry in itemMap.entries) {
                message = "$message<br><white>${entry.key} <gray>- ${entry.value}"
            }
            sender.sendInternalMessages(message)
        }
    }


    @CommandBody
    val getTemplateMap = subCommand {
        execute<CommandSender> { sender, context, argument ->
            var message = sender.asLangText("COMMAND_DEBUG_TEMPLATEMAP", templateMap.size)
            for (entry in templateMap.entries) {
                val str = entry.value.asMap()
                message = "$message<br><white>${entry.key} <gray>- $str"
            }
            sender.sendInternalMessages(message)
        }
    }


    @CommandBody
    val getConfig = subCommand {
        execute<CommandSender> { sender, context, argument ->
            sender.sendInternalMessages(config.toString())
        }
    }


}
