package io.github.zzzyyylllty.sertraline.command

import com.mojang.serialization.DynamicOps
import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.itemManager
import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.Sertraline.mappings
import io.github.zzzyyylllty.sertraline.Sertraline.reflects
import io.github.zzzyyylllty.sertraline.Sertraline.tagManager
import io.github.zzzyyylllty.sertraline.item.rebuild
import io.github.zzzyyylllty.sertraline.item.sertralineItemBuilder
import io.github.zzzyyylllty.sertraline.logger.infoS
import io.github.zzzyyylllty.sertraline.logger.sendStringAsComponent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
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
    name = "sertralinedebug",
    aliases = ["itemdebug","needyitemdebug","depazdebug"],
    permission = "sertraline.command.debug",
    description = "DEBUG Command of DepazItems.",
    permissionMessage = "",
    permissionDefault = PermissionDefault.OP,
    newParser = false,
)
object DebugCommand {

    @CommandBody
    val main = mainCommand {
        createModernHelper()
    }

    @CommandBody
    val help = subCommand {
        createModernHelper()
    }

    @CommandBody
    val getMappings = subCommand {
        execute<CommandSender> { sender, context, argument ->
            var message = mappings.toString()
            sender.infoS(message, false)
        }
    }
    @CommandBody
    val getItems = subCommand {
        execute<CommandSender> { sender, context, argument ->
            var message = itemMap.toString()
            sender.infoS(message, false)
        }
    }
    @CommandBody
    val getConfig = subCommand {
        execute<CommandSender> { sender, context, argument ->
            sender.infoS(config.toString())
        }
    }
    @CommandBody
    val giveTestItem = subCommand {
        execute<CommandSender> { sender, context, argument ->
            val p = sender as Player
            p.giveItem(sertralineItemBuilder("depaz_pills",p))
        }
    }
    @CommandBody
    val processors = subCommand {
        execute<CommandSender> { sender, context, argument ->
            sender.sendStringAsComponent(itemManager.listProcessors().toString())
        }
    }
    @CommandBody
    val tagProcessors = subCommand {
        execute<CommandSender> { sender, context, argument ->
            sender.sendStringAsComponent(tagManager.listProcessors().toString())
        }
    }
    @CommandBody
    val rebuild = subCommand {
        execute<CommandSender> { sender, context, argument ->
            val inv = (sender as Player).inventory
            val hand = inv.itemInMainHand.rebuild(sender)
            inv.setItemInMainHand(hand)
        }
    }

    @CommandBody
    val buildDisplay = subCommand {
        dynamic("id") {
            execute<CommandSender> { sender, context, argument ->
                val id = context["id"]
                if (sender is Player) sender.sendMessage(sertralineItemBuilder(id,sender).displayName()) else sender.sendStringAsComponent("Must a player.")
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
                    sender.sendMessage(sertralineItemBuilder(id,bukkitPlayer).displayName())
                }
            }
        }
    }

}