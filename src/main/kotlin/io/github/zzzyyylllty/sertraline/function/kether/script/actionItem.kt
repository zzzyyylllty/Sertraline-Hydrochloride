package io.github.zzzyyylllty.sertraline.function.kether.script

import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.function.kether.getScriptItemStack
import io.github.zzzyyylllty.sertraline.function.kether.getScriptSertralineItem
import io.github.zzzyyylllty.sertraline.logger.severeS
import io.github.zzzyyylllty.sertraline.logger.warningS
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender
import taboolib.common5.scriptEngine
import taboolib.library.kether.Parser.frame
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ParserHolder.now
import taboolib.module.kether.ParserHolder.text
import taboolib.module.kether.actionNow
import taboolib.module.kether.combinationParser
import taboolib.module.kether.script
import taboolib.module.kether.scriptParser
import taboolib.module.kether.switch
import org.bukkit.entity.Player
import taboolib.module.kether.*
import taboolib.module.nms.ItemTagType
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag
import java.util.concurrent.CompletableFuture

//
@KetherParser(["depaz-item"], shared = true)
fun actionItem() = scriptParser {
    it.switch {
        case ("data") {
            val type = it.nextToken()
            val key = it.nextParsedAction()
            devLog("type: ${type}, key: $key")
            when (type) {
                "get" -> actionNow {
                    return@actionNow getScriptSertralineItem().sertralineMeta.data[run(key).get()]
                }
                "set" -> actionNow {
                    val value = run(it.nextParsedAction()).get()
                    val tag = getScriptItemStack().getItemTag()
                    tag[run(key).get().toString()] = value
                }
                "add","plus" -> actionNow {
                    val value = run(it.nextParsedAction()).get()
                    val i = getScriptItemStack()
                    val tag = i.getItemTag()
                    try {
                    tag[run(key).get().toString()] =
                        if (tag[run(key).get().toString()]?.type == ItemTagType.LONG) (tag[run(key).get()
                            .toString()]?.asLong()?.plus(value.toString().toLong()))
                        else if (tag[run(key).get().toString()]?.type == ItemTagType.FLOAT || tag[run(key).get()
                                .toString()]?.type == ItemTagType.DOUBLE
                        ) (tag[run(key).get().toString()]?.asDouble()?.plus(value.toString().toDouble()))
                        else throw IllegalArgumentException("Value must be long or float or double.")
                    } catch (e: Exception) {
                        severeS("Value must be long or float or double.")
                    }
                }
                "remove","rem","take","minus" -> actionNow {
                    val value = run(it.nextParsedAction()).get()
                    val i = getScriptItemStack()
                    val tag = i.getItemTag()
                    try {
                        tag[run(key).get().toString()] =
                            if (tag[run(key).get().toString()]?.type == ItemTagType.LONG) (tag[run(key).get()
                                .toString()]?.asLong()?.minus(value.toString().toLong()))
                            else if (tag[run(key).get().toString()]?.type == ItemTagType.FLOAT || tag[run(key).get()
                                    .toString()]?.type == ItemTagType.DOUBLE
                            ) (tag[run(key).get().toString()]?.asDouble()?.minus(value.toString().toDouble()))
                            else throw IllegalArgumentException("Value must be long or float or double.")
                    } catch (e: Exception) {
                        severeS("Value must be long or float or double.")
                    }
                }
                else -> error("out of when case.")
            }
        }
        case ("printitem") {
            actionNow {
                warningS(getScriptSertralineItem().toString())
            }
        }
    }
}



@KetherParser(["depaz-itemstack"], shared = true)
fun actionItemStack() = scriptParser {
    it.switch {
        case ("consume") {
            actionNow { getScriptItemStack().amount-- }
        }
        case ("amount") {
            actionNow { return@actionNow getScriptItemStack().amount }
        }
        case ("consumeAll") {
            actionNow { getScriptItemStack().amount = 0}
        }
        case ("printitem") {
            actionNow { warningS(getScriptItemStack().toString()) }
        }
    }
}

/*
@KetherParser(["depazitemstack"], shared = true)
fun actionItemStack() = scriptParser {
    it.switch {
        case ("consume") {
            actionNow { getScriptItemStack().amount-- }
        }
        case ("amount") {
            actionNow { return@actionNow getScriptItemStack().amount }
        }
        case ("consumeAll") {
            actionNow { getScriptItemStack().amount = 0}
        }
        case ("printitem") {
            actionNow { devLog(getScriptItemStack().toString()) }
        }
    }
}*/

