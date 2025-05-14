package io.github.zzzyyylllty.sertraline.function.load

import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.Sertraline.createCustomConfig
import io.github.zzzyyylllty.sertraline.Sertraline.plugin
import io.github.zzzyyylllty.sertraline.logger.fineS
import io.github.zzzyyylllty.sertraline.logger.infoS
import io.github.zzzyyylllty.sertraline.logger.infoL
import io.github.zzzyyylllty.sertraline.logger.severeS
import org.bukkit.command.CommandSender
import taboolib.common.platform.function.getDataFolder
import taboolib.platform.util.asLangText
import java.io.File

fun reloadSertraline(inputSender: CommandSender = console.castSafely<CommandSender>()!!) {
    infoL("INTERNAL_INFO_RELOADING")
    val sender = inputSender
    sender.infoS(sender.asLangText("INTERNAL_INFO_RELOADING"))
    try {
        plugin.reloadCustomConfig()
        sender.fineS(sender.asLangText("INTERNAL_INFO_RELOADED"))
    } catch (e: Throwable) {
        sender.severeS(sender.asLangText("INTERNAL_SEVERE_RELOAD_ERROR"))
        e.printStackTrace()
    }
    if (!File(getDataFolder(), "config.yml").exists()) createCustomConfig()
}
