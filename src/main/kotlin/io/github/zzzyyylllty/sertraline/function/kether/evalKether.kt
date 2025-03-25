package io.github.zzzyyylllty.sertraline.function.kether

import org.bukkit.command.CommandSender
import taboolib.common.platform.function.adaptCommandSender
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptOptions
import java.util.concurrent.CompletableFuture

fun runKether(script: List<String>, sender: CommandSender): CompletableFuture<Any> {
    return KetherShell.eval(
        script, options = ScriptOptions(
            sender = adaptCommandSender(sender)
        )
    ).thenApply { it }
}
fun String.evalKether(sender: CommandSender): CompletableFuture<Any> {
    return KetherShell.eval(
        this, options = ScriptOptions(
            sender = adaptCommandSender(sender)
        )
    ).thenApply { it }
}
fun List<String>.evalKether(sender: CommandSender): CompletableFuture<Any> {
    return KetherShell.eval(
        this, options = ScriptOptions(
            sender = adaptCommandSender(sender)
        )
    ).thenApply { it }
}