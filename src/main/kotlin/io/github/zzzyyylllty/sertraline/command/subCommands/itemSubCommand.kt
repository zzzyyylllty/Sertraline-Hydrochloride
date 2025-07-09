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
import taboolib.module.configuration.util.asMap
import taboolib.platform.util.asLangText
import taboolib.platform.util.giveItem

@CommandHeader(
    name = "sertralineitem",
    permission = "sertraline.command.debug",
    description = "DEBUG Command of DepazItems.",
    permissionMessage = "",
    permissionDefault = PermissionDefault.OP,
    newParser = false,
)
object DepazItemCommand {

    @CommandBody
    val main = mainCommand {
        createModernHelper()
    }

    @CommandBody
    val help = subCommand {
        createModernHelper()
    }

}
