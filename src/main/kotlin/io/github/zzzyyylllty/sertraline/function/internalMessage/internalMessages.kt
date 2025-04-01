package io.github.zzzyyylllty.sertraline.function.internalMessage

import io.github.zzzyyylllty.sertraline.Sertraline.consoleSender
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import taboolib.common.platform.ProxyCommandSender
import taboolib.module.chat.Components

fun CommandSender.sendInternalMessages(message: String) {
    if (this is ConsoleCommandSender) fineS(message)
        else this.sendStringAsComponent("<gray>[<gradient:#BBBBFF:#66CCFF:#CC66FF>Sertraline</gradient>] [<#ccccff>MESSA</#ccccff>]</gray> <reset>$message")
}

fun fineS(message: String) {
    consoleSender?.sendStringAsComponent("<gray>[<gradient:#BBBBFF:#66CCFF:#CC66FF>Sertraline</gradient>] [<#66ffcc>FINES</#66ffcc>]</gray> <reset>$message")
}

fun debugS(message: String) {
    consoleSender?.sendStringAsComponent("<gray>[<gradient:#BBBBFF:#66CCFF:#CC66FF>Sertraline</gradient>] [<#ddaa77>DEBUG</#ddaa77>]</gray> <#aaaaaa>$message")
}

fun infoS(message: String) {
    consoleSender?.sendStringAsComponent("<gray>[<gradient:#BBBBFF:#66CCFF:#CC66FF>Sertraline</gradient>] [<#66ccff>INFOS</#66ccff>]</gray> <reset>$message")
}

fun warningS(message: String) {
    consoleSender?.sendStringAsComponent("<gray>[<gradient:#BBBBFF:#66CCFF:#CC66FF>Sertraline</gradient>] [<#ffee66>WARNI</#ffee66>]</gray> <#eeeeaa>$message")
}

fun severeS(message: String) {
    consoleSender?.sendStringAsComponent("<gray>[<gradient:#BBBBFF:#66CCFF:#CC66FF>Sertraline</gradient>] [<#ff6600>ERROR</#ff6600>]</gray> <#ffccbb>$message")
}

fun CommandSender.sendStringAsComponent(message: String) {
    val mm = MiniMessage.miniMessage()
    val legacy = LegacyComponentSerializer.legacyAmpersand()
    this.sendMessage(mm.deserialize(legacy.serialize(legacy.deserialize(message.replace("§", "&")))))
}

