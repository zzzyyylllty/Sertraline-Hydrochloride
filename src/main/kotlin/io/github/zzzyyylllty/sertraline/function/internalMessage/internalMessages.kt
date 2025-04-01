package io.github.zzzyyylllty.sertraline.function.internalMessage

import io.github.zzzyyylllty.sertraline.Sertraline.consoleSender
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.command.CommandSender
import taboolib.common.platform.ProxyCommandSender
import taboolib.module.chat.Components

fun CommandSender.sendInternalMessages(message: String) {
    this.sendStringAsComponent("<gray>[<gradient:#BBBBFF:#66CCFF:#CC66FF:#aa00ff>Sertraline-Hydrochloride</gradient>]</gray> <reset>$message")
}

fun fineS(message: String) {
    consoleSender?.sendStringAsComponent("<gray>[<gradient:#BBBBFF:#66CCFF:#CC66FF>Sertraline</gradient>] [<#66ffcc>FINE</#66ffcc>]</gray> <reset>$message")
}

fun infoS(message: String) {
    consoleSender?.sendStringAsComponent("<gray>[<gradient:#BBBBFF:#66CCFF:#CC66FF>Sertraline</gradient>] [<#66ccff>INFO</#66ccff>]</gray> <reset>$message")
}

fun warningS(message: String) {
    consoleSender?.sendStringAsComponent("<gray>[<gradient:#BBBBFF:#66CCFF:#CC66FF>Sertraline</gradient>] [<#ffee66>WARN</#ffee66>]</gray> <reset>$message")
}

fun severeS(message: String) {
    consoleSender?.sendStringAsComponent("<gray>[<gradient:#BBBBFF:#66CCFF:#CC66FF>Sertraline</gradient>] [<#ff6600>ERRO</#ff6600>]</gray> <reset>$message")
}

fun CommandSender.sendStringAsComponent(message: String) {
    val mm = MiniMessage.miniMessage()
    val legacy = LegacyComponentSerializer.legacyAmpersand()
    this.sendMessage(mm.deserialize(legacy.serialize(legacy.deserialize(message.replace("§", "&")))))
}

