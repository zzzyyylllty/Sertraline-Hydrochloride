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
import taboolib.common.util.asList
import taboolib.module.configuration.util.asMap
import taboolib.platform.util.asLangText
import taboolib.platform.util.giveItem

@CommandHeader(
    name = "sertralinepack",
    aliases = ["itempack","needyitempack","depazpack"],
    permission = "sertraline.command.pack",
    description = "pack Command of DepazItems.",
    permissionMessage = "",
    permissionDefault = PermissionDefault.OP,
    newParser = false,
)
object DepazPackCommand {

    @CommandBody
    val main = mainCommand {
        createModernHelper()
    }

    @CommandBody
    val help = subCommand {
        createModernHelper()
    }

    @CommandBody
    val info = subCommand {
        dynamic("id") {
            execute<CommandSender> { sender, context, argument ->
                val id = context["id"]
                val pack = packMap[id]
                if (pack != null){
                    var message = sender.asLangText("InfoPack",pack.name, pack.namespace, pack.description, pack.authors.joinToString(","), pack.version, if (pack.enabled) "<green><bold>✔" else "<red><bold>❌")
                    sender.infoS(message)
                } else {
                    sender.infoS(sender.asLangText("InfoPackNotFound"))
                }
            }
            suggestion<CommandSender>(uncheck = true) { sender, context ->
                packMap.keys.asList()
            }
        }
    }

}
