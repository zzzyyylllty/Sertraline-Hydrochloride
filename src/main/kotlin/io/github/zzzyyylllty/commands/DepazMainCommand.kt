package io.github.zzzyyylllty.commands

import io.github.zzzyyylllty.SertralineHydrochloride
import io.github.zzzyyylllty.SertralineHydrochloride.console
import io.github.zzzyyylllty.SertralineHydrochloride.items
import io.github.zzzyyylllty.data.SertralineItem
import io.github.zzzyyylllty.functions.generate.generateItemStack
import io.github.zzzyyylllty.functions.kether.evalKether
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import pers.neige.neigeitems.utils.PlayerUtils.giveItem
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.player
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.severe
import taboolib.common.util.asList
import taboolib.module.lang.asLangText
import kotlin.collections.get

@CommandHeader("depazitems", ["needyitems", "kangelitems", "di"], permission = "kangelbackrooms.command.main")
object DepazMainCommand {

    // 子节点
    @CommandBody
    val reload = subCommand {
        execute<CommandSender> { sender, context, argument ->
            //reload()
        }
    }
    // 子节点
    @CommandBody
    val kether = subCommand {

        dynamic ("shell") {
            execute<CommandSender> { sender, context, argument ->
                context["shell"].evalKether(sender as Player)
            }
        }

    }

    // 子节点
    @CommandBody
    val give = subCommand {
        dynamic ("item") {
            // TabComplete Items
            suggestion<CommandSender>(true) { sender, context ->
                SertralineHydrochloride.items.keys.toMutableList()
            }
            dynamic ("amount") {

                suggestion<CommandSender>(true) { sender, context ->
                    listOf("1","11-45","64","2stack","2304")
                }
                player("player") {

                    execute<CommandSender> { sender, context, argument ->
                        val user = context.player("user")
                        // 转化为Bukkit的Player
                        val bukkitPlayer = user.castSafely<Player>() ?: run {
                            if (sender !is ConsoleCommandSender) { sender as Player } else {
                                console.asLangText("command.main.no_player_found", user.name)
                                error(console.asLangText("command.main.no_player_found", user.name))
                            }
                        }
                        val item = items[context["item"]] ?: run {
                            severe(console.asLangText("command.main.no_item_found", context["item"]))
                            error(console.asLangText("command.main.no_item_found", context["item"]))
                        }
                        bukkitPlayer.giveItem(generateItemStack(item, bukkitPlayer))
                    }
                }
            }
        }
    }


}