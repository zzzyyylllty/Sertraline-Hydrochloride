package io.github.zzzyyylllty.sertraline.command

import ink.ptms.adyeshach.core.entity.type.minecraftVersion
import io.github.zzzyyylllty.sertraline.command.subCommands.DepazDebugCommand
import io.github.zzzyyylllty.sertraline.Sertraline.plugin
import io.github.zzzyyylllty.sertraline.command.subCommands.DepazApiCommand
import io.github.zzzyyylllty.sertraline.command.subCommands.DepazItemCommand
import io.github.zzzyyylllty.sertraline.function.load.reloadSertraline
import io.github.zzzyyylllty.sertraline.logger.infoL
import io.github.zzzyyylllty.sertraline.logger.infoS
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
            sender.infoS("<gradient:aqua:blue>Sertraline Hydrochloride</gradient> <#ccccff>$pluginVersion")
            sender.infoS("<gradient:#6600ff:#aa00aa>Running on:</gradient> <light_purple>${runningPlatform.name} - $minecraftVersion")
            sender.infoS("<#660099>Plugin by AkaCandyKAngel.")
            sender.infoS("<#660099>Use <blue>/sertraline help</blue> for help.")
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

    /*
    @CommandBody
    val flatHelp = subCommand {
        createTabooLegacyStyleCommandHelper()
    }
    */

    @CommandBody
    val api = DepazApiCommand

    @CommandBody
    val item = DepazItemCommand

    @CommandBody
    val debug = DepazDebugCommand

    @CommandBody
    val reload = subCommand {
        execute<CommandSender> { sender, context, argument ->
            reloadSertraline(sender)
        }
    }

}