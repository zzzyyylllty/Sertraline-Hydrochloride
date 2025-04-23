package io.github.zzzyyylllty.sertraline.function.kether.script

import ink.ptms.zaphkiel.impl.feature.getCurrentDurability
import ink.ptms.zaphkiel.impl.feature.kether.itemStream
import io.github.zzzyyylllty.sertraline.data.DepazItemInst
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.function.kether.getScriptItem
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.ComponentLike
import taboolib.module.kether.KetherParser
import taboolib.module.kether.actionNow
import taboolib.module.kether.script
import taboolib.module.kether.scriptParser
import taboolib.module.kether.switch


@KetherParser(["needyitem","depaz"], shared = true)
fun actionItem() = scriptParser {
    it.switch {
        case ("consume") {
            actionNow { getScriptItem().item.amount-- }
        }
        case ("amount") {
            actionNow { return@actionNow getScriptItem().item.amount }
        }
        case ("consumeAll") {
            actionNow { getScriptItem().item.amount = 0}
        }
        case ("printitem") {
                actionNow { devLog(getScriptItem().toString()) }
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