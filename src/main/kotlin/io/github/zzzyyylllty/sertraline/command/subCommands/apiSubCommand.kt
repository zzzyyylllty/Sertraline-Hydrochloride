package io.github.zzzyyylllty.sertraline.command.subCommands

import io.github.zzzyyylllty.sertraline.logger.infoS
import io.github.zzzyyylllty.sertraline.function.kether.evalKether
import io.github.zzzyyylllty.sertraline.function.kether.runKether
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.PermissionDefault
import taboolib.common.platform.command.player
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.submitAsync

@CommandHeader(
    name = "sertralineapi",
    aliases = ["itemapi","needyitemapi","depazapi","chotenapi"],
    permission = "sertraline.command.api",
    description = "API Command of DepazItems.",
    permissionMessage = "",
    permissionDefault = PermissionDefault.OP,
    newParser = false,
)
object DepazApiCommand {

    /** 解析 Minimessage */
    @CommandBody
    val minimessage = subCommand {
        dynamic("content") {
            execute<CommandSender> { sender, context, argument ->
                submitAsync {
                    val mm = MiniMessage.miniMessage()
                    // 获取参数的值
                    val content = context["content"]
                    sender.infoS(content) }
            }
        }
    }

    /** Kether */
    @CommandBody
    val eval = subCommand {
        dynamic("script") {
            execute<CommandSender> { sender, context, argument ->
                submitAsync {
                    val mm = MiniMessage.miniMessage()
                    // 获取参数的值
                    val content = context["script"]
                    val ret = runKether(listOf(content), sender)
                    sender.infoS("<yellow>Kether: <gray>$content")
                    sender.infoS("<yellow>Return: <gray>${ret.get()}") }
            }
        }
    }

    /** Kether */
    @CommandBody
    val evalByPlayer = subCommand {
        player("player") {
            dynamic("script") {
                execute<CommandSender> { sender, context, argument ->
                    submitAsync {
                        val tabooPlayer = context.player("player")
                        val bukkitPlayer = tabooPlayer.castSafely<Player>() as CommandSender
                        val mm = MiniMessage.miniMessage()
                        // 获取参数的值
                        val content = context["script"]
                        val ret = runKether(listOf(content), bukkitPlayer)
                        sender.infoS("<yellow>Kether: <gray>$content")
                        sender.infoS("<yellow>Return: <gray>${ret.get()}") }
                }
            }
        }
    }
    /** Kether */
    @CommandBody
    val evalSilent = subCommand {
        dynamic("script") {
            execute<CommandSender> { sender, context, argument ->
                submitAsync {
                    val mm = MiniMessage.miniMessage()
                    // 获取参数的值
                    val content = context["script"]
                    runKether(listOf(content), sender) }
            }
        }
    }

    /** Kether */
    @CommandBody
    val evalByPlayerSilent = subCommand {
        player("player") {
            dynamic("script") {
                execute<CommandSender> { sender, context, argument ->
                    submitAsync {
                        val tabooPlayer = context.player("player")
                        val bukkitPlayer = tabooPlayer.castSafely<Player>() as CommandSender
                        val mm = MiniMessage.miniMessage()
                        // 获取参数的值
                        val content = context["script"]
                        runKether(listOf(content), bukkitPlayer)
                    }
                }
            }
        }
    }


}
