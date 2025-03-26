package io.github.zzzyyylllty.sertraline.command

import ink.ptms.adyeshach.core.entity.type.minecraftVersion
import io.github.zzzyyylllty.sertraline.command.subCommands.DepazDebugCommand
import io.github.zzzyyylllty.sertraline.Sertraline.plugin
import io.github.zzzyyylllty.sertraline.command.subCommands.DepazApiCommand
import io.github.zzzyyylllty.sertraline.command.subCommands.DepazItemCommand
import io.github.zzzyyylllty.sertraline.function.internalMessage.sendInternalMessages
import io.github.zzzyyylllty.sertraline.logger.infoL
import io.github.zzzyyylllty.sertraline.logger.severeL
import org.bukkit.command.CommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.PermissionDefault
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.pluginVersion
import taboolib.common.platform.function.runningPlatform
import taboolib.expansion.createHelper
import taboolib.platform.util.asLangText

/**
 * Usage: /sertraline
 *          ├── about
 *          ├── api
 *          │   ├── minimessage <content>
 *          │   ├── eval <script>
 *          │   ├── evalByPlayer <player> <script>
 *          ├── debug
 *          │   ├── getItem <id>
 *          │   ├── getItemMap
 *          │   ├── diagnose
 *          │   └── getUserData <player>
 *          ├── item
 *          │      ├── give [player] [amount] [silent]
 *          │      └── member <user>
 *          │              ├── permission <permissionGroup>
 *          │              ├── kick
 *          │              ├── mute <time>
 *          │              ├── title
 *          │              │   ├── special <Title>
 *          │              │   └── special (clear)
 *          │              └── blacklist

 *
 * */

@CommandHeader(
    name = "sertraline",
    aliases = ["depazitem","depazitems","depaz","di"],
    permission = "sertraline.command.main",
    description = "Main Command of DepazItems.",
    permissionMessage = "",
    permissionDefault = PermissionDefault.OP,
    newParser = false,
)
object SertralineMainCommand {

    @CommandBody
    val about = subCommand {
        execute<CommandSender> { sender, context, argument -> //
            sender.sendInternalMessages("<gradient:aqua:blue>Sertraline Hydrochloride</gradient> <#ccccff>$pluginVersion")
            sender.sendInternalMessages("<gradient:#6600ff:#aa00aa>Running on Platform:</gradient> <light_purple>${runningPlatform.name} - $minecraftVersion")
            sender.sendInternalMessages("<#660099>Plugin by AkaCandyKAngel.")
            sender.sendInternalMessages("<#660099>Use <blue>/sertraline help</blue> for help.")
        }
    }
    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody
    val help = subCommand {
        createHelper()
    }


    @CommandBody
    val api = DepazApiCommand

    @CommandBody
    val item = DepazItemCommand

    @CommandBody
    val debug = DepazDebugCommand

    @CommandBody
    val reload = subCommand {
        execute<CommandSender> { sender, context, argument ->
            infoL("INTERNAL_INFO_RELOADING")
            sender.sendInternalMessages(sender.asLangText("INTERNAL_INFO_RELOADING"))
            try {
                plugin.reloadCustomConfig()
                sender.sendInternalMessages(sender.asLangText("INTERNAL_INFO_RELOADED"))
            } catch (e: Throwable) {
                sender.sendInternalMessages(sender.asLangText("INTERNAL_SEVERE_RELOAD_ERROR"))
                e.printStackTrace()
            }
        }
    }

}