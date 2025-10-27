package io.github.zzzyyylllty.sertraline.command

import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.function.kether.evalKetherString
import io.github.zzzyyylllty.sertraline.item.sertralineItemBuilder
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.PermissionDefault
import taboolib.common.platform.command.bool
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.player
import taboolib.common.platform.command.subCommand
import taboolib.common.util.asList
import taboolib.platform.util.giveItem

@CommandHeader(
    name = "sertralineitem",
    aliases = ["items","needyitemi","depazi"],
    permission = "sertraline.command.item",
    description = "DEBUG Command of DepazItems.",
    permissionMessage = "",
    permissionDefault = PermissionDefault.OP,
    newParser = false,
)
object ItemCommand {

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
                val id = context["id"]
                if (sender is Player) sender.giveItem(sertralineItemBuilder(itemMap[id]!!,sender))
            }
            suggestion<CommandSender>(uncheck = true) { sender, context ->
                itemMap.keys.asList()
            }
            player("player") {
                execute<CommandSender> { sender, context, argument ->
                    val id = context["id"]
                    val tabooPlayer = context.player("player")
                    // 转化为Bukkit的Player
                    val bukkitPlayer = tabooPlayer.castSafely<Player>()
                    bukkitPlayer?.giveItem(sertralineItemBuilder(itemMap[id]!!,bukkitPlayer))
                }

                dynamic("amount") {
                    execute<CommandSender> { sender, context, argument ->
                        val id = context["id"]
                        val amount = context["amount"]
                        if (sender is Player) sender.giveItem(sertralineItemBuilder(itemMap[id]!!,sender, amount = amount.toInt()))
                    }
                    suggestion<CommandSender>(uncheck = true) { sender, context ->
                        listOf("1","64","16")
                    }
                    bool("silent") {
                        execute<CommandSender> { sender, context, argument ->
                            val id = context["id"]
                            val tabooPlayer = context.player("player")
                            // 转化为Bukkit的Player
                            val bukkitPlayer = tabooPlayer.castSafely<Player>()
                            val amount = context["amount"]
                            bukkitPlayer?.giveItem(sertralineItemBuilder(itemMap[id]!!,bukkitPlayer, amount = amount.toInt()))
                        }
                    }
                }
            }
        }
    }


}
