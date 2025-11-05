package io.github.zzzyyylllty.sertraline.logger

import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.Sertraline.consoleSender
import io.github.zzzyyylllty.sertraline.logger.infoS
import io.github.zzzyyylllty.sertraline.logger.severeS
import io.github.zzzyyylllty.sertraline.logger.warningS
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import taboolib.module.lang.asLangText

val prefix = "[<gradient:#66ffff:#99ccff:#aa99cc>Sertraline</gradient>]"


fun infoL(node: String,vararg args: Any) {
    consoleSender.infoS(console.asLangText(node,args))
}
fun severeL(node: String,vararg args: Any) {
    consoleSender.severeS(console.asLangText(node,args))
}
fun warningL(node: String,vararg args: Any) {
    consoleSender.warningS(console.asLangText(node,args))
}

fun CommandSender?.fineS(message: String, bothSendConsole: Boolean = false) {
    (this ?:consoleSender).sendStringAsComponent("<gray>$prefix [<#66ffcc>FINES</#66ffcc>]</gray> <reset>$message")
    if (this != null && bothSendConsole) consoleSender.sendStringAsComponent("<gray>$prefix [<#66ffcc>FINES</#66ffcc>]</gray> <reset>$message")
}

fun CommandSender?.debugS(message: String, bothSendConsole: Boolean = false) {
    (this ?:consoleSender).sendStringAsComponent("<gray>$prefix [<#ddaa77>DEBUG</#ddaa77>]</gray> <#aaaaaa>$message")
    if (this != null && bothSendConsole) consoleSender.sendStringAsComponent("<gray>$prefix [<#ddaa77>DEBUG</#ddaa77>]</gray> <#aaaaaa>$message")
}

fun CommandSender?.infoS(message: String, bothSendConsole: Boolean = false) {
    (this ?:consoleSender).sendStringAsComponent("<gray>$prefix [<#66ccff>INFOS</#66ccff>]</gray> <reset>$message")
    if (this != null && bothSendConsole) consoleSender.sendStringAsComponent("<gray>$prefix [<#66ccff>INFOS</#66ccff>]</gray> <reset>$message")
}

fun CommandSender?.warningS(message: String, bothSendConsole: Boolean = false) {
    (this ?:consoleSender).sendStringAsComponent("<gray>$prefix [<#ffee66>WARNI</#ffee66>]</gray> <#eeeeaa>$message")
    if (this != null && bothSendConsole) consoleSender.sendStringAsComponent("<gray>$prefix [<#ffee66>WARNI</#ffee66>]</gray> <#eeeeaa>$message")
}

fun CommandSender?.severeS(message: String, bothSendConsole: Boolean = false) {
    (this ?:consoleSender).sendStringAsComponent("<gray>$prefix [<#ff6600>ERROR</#ff6600>]</gray> <#ffccbb>$message")
    if (this != null && bothSendConsole) consoleSender.sendStringAsComponent("<gray>$prefix [<#ff6600>ERROR</#ff6600>]</gray> <#ffccbb>$message")
}

fun fineS(message: String) {
    consoleSender.sendStringAsComponent("<gray>$prefix [<#66ffcc>FINE</#66ffcc>]</gray> <reset>$message")
}

fun debugS(message: String) {
    consoleSender.sendStringAsComponent("<gray>$prefix [<#ddaa77>DEBUG</#ddaa77>]</gray> <#aaaaaa>$message")
}

fun infoS(message: String) {
    consoleSender.sendStringAsComponent("<gray>$prefix [<#66ccff>INFO</#66ccff>]</gray> <reset>$message")
}

fun warningS(message: String) {
    consoleSender.sendStringAsComponent("<gray>$prefix [<#ffee66>WARN</#ffee66>]</gray> <#eeeeaa>$message")
}

fun severeS(message: String) {
    consoleSender.sendStringAsComponent("<gray>$prefix [<#ff6600>ERROR</#ff6600>]</gray> <#ffccbb>$message")
}

fun CommandSender.sendStringAsComponent(message: String) {
    val mm = MiniMessage.miniMessage()
    val legacy = LegacyComponentSerializer.legacyAmpersand()
    (this as Audience).sendMessage(mm.deserialize(legacy.serialize(legacy.deserialize(message.replace("§", "&")))))
}
