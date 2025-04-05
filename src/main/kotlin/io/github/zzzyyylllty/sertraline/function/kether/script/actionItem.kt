package io.github.zzzyyylllty.sertraline.function.kether.script

import ink.ptms.zaphkiel.impl.feature.kether.itemStream
import io.github.zzzyyylllty.sertraline.data.DepazItemInst
import io.github.zzzyyylllty.sertraline.function.kether.getScriptItem
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.ComponentLike
import taboolib.module.kether.KetherParser
import taboolib.module.kether.actionNow
import taboolib.module.kether.script
import taboolib.module.kether.scriptParser
import taboolib.module.kether.switch
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.module.kether.combinationParser


@KetherParser(["item"], shared = true)
fun actionItem() = combinationParser {
    it.group(text()).apply(it) { str ->
        now {
            when (str) {
                "consume" ->
                    actionNow { getScriptItem().item.amount-- }
            }
        }
    }
}
/*scriptParser {
    it.switch {
        case("minitell","minimessage") {
            val message = it.nextParsedAction().
            val mm = MiniMessage.miniMessage()
            actionNow {
                val sender = script().sender?.castSafely<CommandSender>()
                (sender as Audience).sendMessage(mm.deserialize(message))
            }
        }
    }
}*/