package io.github.zzzyyylllty.sertraline.command

import io.github.zzzyyylllty.sertraline.Sertraline.reloadCustomConfig
import io.github.zzzyyylllty.sertraline.logger.fineS
import io.github.zzzyyylllty.sertraline.logger.infoS
import io.github.zzzyyylllty.sertraline.logger.sendStringAsComponent
import taboolib.common.platform.command.component.CommandComponentDynamic
import taboolib.common.platform.command.component.CommandComponentLiteral
import taboolib.module.lang.asLangText
import org.bukkit.command.CommandSender
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.PermissionDefault
import taboolib.common.platform.command.component.CommandComponent
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.pluginVersion
import taboolib.common.platform.function.runningPlatform
import taboolib.module.nms.MinecraftVersion.versionId

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
        execute<CommandSender> { sender, context, argument ->
            sender.infoS("<gradient:aqua:blue>Sertraline Hydrochloride</gradient> <#ccccff>$pluginVersion")
            sender.infoS("<gradient:#6600ff:#aa00aa>Running on:</gradient> <light_purple>${runningPlatform.name} - $versionId")
            sender.infoS("<#cc66ff>Plugin by AkaCandyKAngel.")
            sender.infoS("<#cc66ff>Use <blue>/sertraline help</blue> for help.")
        }
    }
    @CommandBody
    val main = mainCommand {
        createModernHelper()
    }

    @CommandBody
    val help = subCommand {
        createModernHelper()
    }

    /*
    @CommandBody
    val flatHelp = subCommand {
        createTabooLegacyStyleCommandHelper()
    }
    */

    @CommandBody
    val debug = DebugCommand

    @CommandBody
    val data = DataCommand

    @CommandBody
    val api = ApiCommand

    @CommandBody
    val item = ItemCommand

    @CommandBody
    val reload = subCommand {
        execute<CommandSender> { sender, context, argument ->
            sender.infoS("Reloading...")
            try {
                reloadCustomConfig(true)
                sender.fineS("Reloaded.")
            }
            catch (e: Exception) {

            }
        }
    }

}


fun CommandComponent.createModernHelper(checkPermissions: Boolean = true) {
    execute<ProxyCommandSender> { sender, context, _ ->
        val command = context.command
        val builder = StringBuilder("<gradient:yellow:aqua>Usage: /${command.name}<gradient>")
        var newline = false

        fun check(children: List<CommandComponent>): List<CommandComponent> {
            // 检查权限
            val filterChildren = if (checkPermissions) {
                children.filter { sender.hasPermission(it.permission) }
            } else {
                children
            }
            // 过滤隐藏
            return filterChildren.filter { it !is CommandComponentLiteral || !it.hidden }
        }

        fun space(space: Int): String {
            return (1..space).joinToString("") { " " }
        }

        fun print(compound: CommandComponent, index: Int, size: Int, offset: Int = 8, level: Int = 0, end: Boolean = false, optional: Boolean = false) {
            var option = optional
            var comment = 0
            when (compound) {
                is CommandComponentLiteral -> {
                    if (size == 1) {
                        builder.append(" ").append("<gradient:#66ccff:#ffffff>${compound.aliases[0]}<>")
                    } else {
                        newline = true
                        builder.appendLine()
                        builder.append(space(offset))
                        if (level > 1) {
                            builder.append(if (end) " " else "<#888888>│")
                        }
                        builder.append(space(level))
                        if (index + 1 < size) {
                            builder.append("<gradient:#888888:#cccccc>├── </gradient>")
                        } else {
                            builder.append("<gradient:#888888:#cccccc>└── </gradient>")
                        }
                        builder.append("<gradient:#66ccff:#ffffff>${compound.aliases[0]}</gradient>")
                    }
                    option = false
                    comment = compound.aliases[0].length
                }
                is CommandComponentDynamic -> {
                    val value = if (compound.comment.startsWith("@")) {
                        sender.asLangText(compound.comment.substring(1))
                    } else {
                        compound.comment
                    }
                    comment = if (compound.optional || option) {
                        option = true
                        builder.append(" ").append("<#66ffcc>[<$value>]")
                        compound.comment.length + 4
                    } else {
                        builder.append(" ").append("<#ffcc66><$value>")
                        compound.comment.length + 2
                    }
                }
            }
            if (level > 0) {
                comment += 1
            }
            val checkedChildren = check(compound.children)
            checkedChildren.forEachIndexed { i, children ->
                // 因 literal 产生新的行
                if (newline) {
                    print(children, i, checkedChildren.size, offset, level + comment, end, option)
                } else {
                    val length = if (offset == 8) command.name.length + 1 else comment + 1
                    print(children, i, checkedChildren.size, offset + length, level, end, option)
                }
            }
        }
        val checkedChildren = check(context.commandCompound.children)
        val size = checkedChildren.size
        checkedChildren.forEachIndexed { index, children ->
            print(children, index, size, end = index + 1 == size)
        }
        builder.lines().forEach {
            sender.castSafely<CommandSender>()?.sendStringAsComponent(it)
        }
    }
}
