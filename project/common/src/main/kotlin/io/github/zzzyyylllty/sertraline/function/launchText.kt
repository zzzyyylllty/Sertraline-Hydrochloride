package io.github.zzzyyylllty.sertraline.function

import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.Sertraline.consoleSender
import io.github.zzzyyylllty.sertraline.logger.sendStringAsComponent
import io.github.zzzyyylllty.sertraline.util.VersionHelper
import org.bukkit.command.CommandSender
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.pluginVersion
import taboolib.module.lang.asLangText

@Awake(LifeCycle.ENABLE)
fun launchText() {

    val premiumDisplayName = console.asLangText(if (VersionHelper().isSertralinePremium) "PremiumVersion" else "FreeVersion")

    consoleSender.sendStringAsComponent(" ")
    consoleSender.sendStringAsComponent("<gradient:#66ffff:#99ccff:#aa99cc> $$$$$$\\                        $$\\                         $$\\ $$\\                      |")
    consoleSender.sendStringAsComponent("<gradient:#66ffff:#99ccff:#aa99cc>$$  __$$\\                       $$ |                        $$ |\\__|                     |")
    consoleSender.sendStringAsComponent("<gradient:#66ffff:#99ccff:#aa99cc>$$ /  \\__| $$$$$$\\   $$$$$$\\  $$$$$$\\    $$$$$$\\   $$$$$$\\  $$ |$$\\ $$$$$$$\\   $$$$$$\\   |")
    consoleSender.sendStringAsComponent("<gradient:#66ffff:#99ccff:#aa99cc>\\$$$$$$\\  $$  __$$\\ $$  __$$\\ \\_$$  _|  $$  __$$\\  \\____$$\\ $$ |$$ |$$  __$$\\ $$  __$$\\  |")
    consoleSender.sendStringAsComponent("<gradient:#66ffff:#99ccff:#aa99cc> \\____$$\\ $$$$$$$$ |$$ |  \\__|  $$ |    $$ |  \\__| $$$$$$$ |$$ |$$ |$$ |  $$ |$$$$$$$$ | |")
    consoleSender.sendStringAsComponent("<gradient:#66ffff:#99ccff:#aa99cc>$$\\   $$ |$$   ____|$$ |        $$ |$$\\ $$ |      $$  __$$ |$$ |$$ |$$ |  $$ |$$   ____| |")
    consoleSender.sendStringAsComponent("<gradient:#66ffff:#99ccff:#aa99cc>\\$$$$$$  |\\$$$$$$$\\ $$ |        \\$$$$  |$$ |      \\$$$$$$$ |$$ |$$ |$$ |  $$ |\\$$$$$$$\\  |")
    consoleSender.sendStringAsComponent("<gradient:#66ffff:#99ccff:#aa99cc> \\______/  \\_______|\\__|         \\____/ \\__|       \\_______|\\__|\\__|\\__|  \\__| \\_______| |")
    consoleSender.sendStringAsComponent(" ")
    consoleSender.sendStringWithPrefix("<#66ffff>", console.asLangText("Welcome1", pluginVersion))
    consoleSender.sendStringWithPrefix("<#66ffff>", console.asLangText("Welcome2", premiumDisplayName))
    consoleSender.sendStringAsComponent(" ")
    consoleSender.sendStringWithPrefix("<#66ffff>", console.asLangText("Welcome3", "https://github.com/zzzyyylllty"))
    consoleSender.sendStringWithPrefix("<#66ffff>", console.asLangText("Welcome4", "https://github.com/zzzyyylllty/Sertraline-Hydrochloride"))
    consoleSender.sendStringWithPrefix("<#66ffff>", console.asLangText("Welcome5", "https://github.com/zzzyyylllty/Sertraline-Hydrochloride/wiki"))
    consoleSender.sendStringAsComponent(" ")
    if (VersionHelper().isSertralinePremium) consoleSender.sendStringWithPrefix("<gradient:red:yellow:green:aqua:light_purple>", console.asLangText("Welcome2", premiumDisplayName))
    consoleSender.sendStringAsComponent(" ")

}

private fun CommandSender.sendStringWithPrefix(prefix: String, message: String) {
    this.sendStringAsComponent(prefix + message)
}