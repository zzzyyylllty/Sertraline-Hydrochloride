package io.github.zzzyyylllty

import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.common.platform.function.info
import taboolib.common.platform.function.pluginId
import taboolib.common.platform.function.pluginVersion
import taboolib.module.lang.Level
import taboolib.module.lang.sendLang

object DepazItems : Plugin() {

    var debug = true
    var console = console()
    var items = LinkedHashMap<String, DepazItem>
    override fun onEnable() {
        console.sendLang(Level.INFO, "enable.boot.copyright")
        console.sendLang(Level.INFO, "enable.boot.plugin", pluginId)
        console.sendLang(Level.INFO, "enable.boot.version", pluginVersion)


    }
}