package io.github.zzzyyylllty.functions.kether

import io.github.zzzyyylllty.debugMode.debugLog
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptCommandSender
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptOptions
import java.util.concurrent.CompletableFuture

fun runKether(script: List<String>, player: Player): CompletableFuture<Any> {
    debugLog("running Kether $script in Player $player")
    return KetherShell.eval(
        script, options = ScriptOptions(
            sender = adaptCommandSender(player)
        )
    ).thenApply { it }
}

fun runKether(script: String, player: Player): CompletableFuture<Any> {
    debugLog("running Kether $script in Player $player")
    return KetherShell.eval(
        script, options = ScriptOptions(
            sender = adaptCommandSender(player)
        )
    ).thenApply { it }
}

fun List<String>.evalKether(player: Player): CompletableFuture<Any> {
    val script = this
    debugLog("running Kether $script in Player $player")
    return KetherShell.eval(
        script, options = ScriptOptions(
            sender = adaptCommandSender(player)
        )
    ).thenApply { it }
}

fun String.evalKether(player: Player): CompletableFuture<Any> {
    val script = this
    debugLog("running Kether $script in Player $player")
    return KetherShell.eval(
        script, options = ScriptOptions(
            sender = adaptCommandSender(player)
        )
    ).thenApply { it }
}
