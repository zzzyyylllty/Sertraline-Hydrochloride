package io.github.zzzyyylllty.sertraline.command

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.Sertraline.mappings
import io.github.zzzyyylllty.sertraline.item.sertralineItemBuilder
import io.github.zzzyyylllty.sertraline.logger.infoS
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.PermissionDefault
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
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
            p.giveItem(sertralineItemBuilder(itemMap["depaz_pills"]!!,p))
        }
    }

}
