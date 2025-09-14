package io.github.zzzyyylllty.sertraline.function.kether.script

import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.function.item.getSavedData
import io.github.zzzyyylllty.sertraline.function.kether.getScriptItemStack
import io.github.zzzyyylllty.sertraline.function.kether.getScriptSertralineItem
import io.github.zzzyyylllty.sertraline.logger.severeS
import io.github.zzzyyylllty.sertraline.logger.warningS
import taboolib.module.kether.KetherParser
import taboolib.module.kether.actionNow
import taboolib.module.kether.scriptParser
import taboolib.module.kether.switch
import taboolib.module.configuration.util.asMap
import taboolib.module.kether.*
import taboolib.module.nms.ItemTagType
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag

//
@KetherParser(["depaz-item"], shared = true)
fun actionItem() = scriptParser {
    it.switch {
        case ("data") {
            val type = it.nextToken()
            val keyAction = it.nextParsedAction()
            when (type) {
                "get" -> actionNow {
                    val key = run(keyAction).get()
                    devLog("type: ${type}, key: $key")
                    val i = getScriptItemStack()
                    val tag = i.getItemTag()
                    devLog("Tag reading completed: ${key} = $tag")
                    return@actionNow i.getSavedData()[key]
                }
                "get-original" -> actionNow {
                    val key = run(keyAction).get()
                    devLog("type: ${type}, key: $key")
                    return@actionNow getScriptSertralineItem().sertralineMeta.data[key]
                }
                "set","=" -> actionNow {
                    val key = run(keyAction).get()
                    val value = run(it.nextParsedAction()).get()
                    devLog("type: ${type}, key: $key, value: $value")
                    val i = getScriptItemStack()
                    var data = i.getSavedData()
                    data[key.toString()] = value
                    val tag = i.getItemTag()
                    tag["SERTRALINE_DATA"] = data
                    getScriptItemStack().setItemTag(tag)
                    devLog("Tag writing completed: $data")
                }
                "add","plus","+","+=" -> actionNow {
                    val key = run(keyAction).get()
                    val value = run(it.nextParsedAction()).get()
                    devLog("type: ${type}, key: $key, value: $value")
                    val i = getScriptItemStack()
                    var data = i.getSavedData()
                    try {
                        data[key.toString()] =
                            if (data[key] is Long) (data[key]?.toString()?.toLong()?.plus(value.toString().toLong()))
                            else if (data[key] is Double) (data[key]?.toString()?.toDouble()?.plus(value.toString().toDouble()))
                            else if (data[key] is Float) (data[key]?.toString()?.toFloat()?.plus(value.toString().toFloat()))
                            else if (data[key] is Int) (data[key]?.toString()?.toInt()?.plus(value.toString().toInt()))
                            else throw IllegalArgumentException("Value must be long or float or double or int.")
                        val tag = i.getItemTag()
                        tag["SERTRALINE_DATA"] = data
                        getScriptItemStack().setItemTag(tag)
                    } catch (e: Exception) {
                        severeS("Value must be long or float or double.")
                    }
                    devLog("Tag writing completed: $data")
                }
                "minus","remove","-","-=" -> actionNow {
                    val key = run(keyAction).get()
                    val value = run(it.nextParsedAction()).get()
                    devLog("type: ${type}, key: $key, value: $value")
                    val i = getScriptItemStack()
                    var data = i.getSavedData()
                    try {
                        data[key.toString()] =
                            if (data[key] is Long) (data[key]?.toString()?.toLong()?.minus(value.toString().toLong()))
                            else if (data[key] is Double) (data[key]?.toString()?.toDouble()?.minus(value.toString().toDouble()))
                            else if (data[key] is Float) (data[key]?.toString()?.toFloat()?.minus(value.toString().toFloat()))
                            else if (data[key] is Int) (data[key]?.toString()?.toInt()?.minus(value.toString().toInt()))
                            else throw IllegalArgumentException("Value must be long or float or double or int.")
                        val tag = i.getItemTag()
                        tag["SERTRALINE_DATA"] = data
                        getScriptItemStack().setItemTag(tag)
                    } catch (e: Exception) {
                        severeS("Value must be long or float or double.")
                    }
                    devLog("Tag writing completed: $data")
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

