package io.github.zzzyyylllty.commands

import io.github.zzzyyylllty.SertralineHydrochloride
import io.github.zzzyyylllty.data.SertralineItem
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.subCommand
import taboolib.common.util.asList
import kotlin.collections.get

@CommandHeader("depazitems", ["needyitems", "kangelitems", "di"], permission = "kangelbackrooms.command.main")
object DepazMainCommand {

    // 子节点
    @CommandBody
    val reload = subCommand {
        execute<CommandSender> { sender, context, argument ->
            reload()
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
                dynamic ("user") {

                    suggestion<CommandSender>(true) { sender, context -> Bukkit.getOnlinePlayers().asList() }

                    execute<CommandSender> { sender, context, argument ->
                        val player = Bukkit.getPlayer(context["user"]) ?: sender as Player
                        val value = context["value"].toDouble()
                        if (context["type"] == "LUCIDITY") KAngelBackrooms.playerData[player.uniqueId]?.lucidity = value
                        if (context["type"] == "MAX_LUCIDITY") KAngelBackrooms.playerData[player.uniqueId]?.maxLucidity = value
                        if (context["type"] == "DARKNESS") KAngelBackrooms.playerData[player.uniqueId]?.darkness = value
                        if (context["type"] == "MAX_DARKNESS") KAngelBackrooms.playerData[player.uniqueId]?.maxDarkness = value
                        if (context["type"] == "INSANITY") KAngelBackrooms.playerData[player.uniqueId]?.stress = value
                        if (context["type"] == "MAX_INSANITY") KAngelBackrooms.playerData[player.uniqueId]?.maxStress = value
                        if (context["type"] == "AP") Bukkit.broadcastMessage(KAngelBackrooms.playerData.toString())
                    }
                }}
        }
    }


}