package io.github.zzzyyylllty.sertraline.function.internalMessage

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender

fun CommandSender.sendInternalMessages(message: String) {
    this.sendStringAsComponent("<gray>[<gradient:#BBBBFF:#66CCFF:#CC66FF:#aa00ff>Sertraline-Hydrochloride</gradient>]</gray> <reset>$message")
}


fun CommandSender.sendStringAsComponent(message: String) {
    val mm = MiniMessage.miniMessage()
    (this as Audience).sendMessage(mm.deserialize(message))
}

