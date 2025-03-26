package io.github.zzzyyylllty.sertraline.function.internalMessage

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

fun CommandSender.sendStringAsComponent(message: String) {
    val mm = MiniMessage.miniMessage()
    val legacy = LegacyComponentSerializer.legacyAmpersand()
    (this as Audience).sendMessage(mm.deserialize(legacy.serialize(legacy.deserialize(message.replace("§", "&")))))
}

