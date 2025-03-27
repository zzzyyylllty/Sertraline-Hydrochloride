package io.github.zzzyyylllty.sertraline.function.load

import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.Sertraline.plugin
import io.github.zzzyyylllty.sertraline.function.internalMessage.sendInternalMessages
import io.github.zzzyyylllty.sertraline.logger.infoL
import org.bukkit.command.CommandSender
import taboolib.platform.util.asLangText

fun reloadSertraline(inputSender: CommandSender = console.castSafely<CommandSender>()!!) {
    infoL("INTERNAL_INFO_RELOADING")
    val sender = inputSender
    sender.sendInternalMessages(sender.asLangText("INTERNAL_INFO_RELOADING"))
    try {
        plugin.reloadCustomConfig()
        sender.sendInternalMessages(sender.asLangText("INTERNAL_INFO_RELOADED"))
    } catch (e: Throwable) {
        sender.sendInternalMessages(sender.asLangText("INTERNAL_SEVERE_RELOAD_ERROR"))
        e.printStackTrace()
    }
}
