package io.github.zzzyyylllty

import io.github.zzzyyylllty.data.SertralineItem
import io.github.zzzyyylllty.functions.boot
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.common.platform.function.pluginId
import taboolib.common.platform.function.pluginVersion
import taboolib.module.lang.Level
import taboolib.module.lang.sendLang

object SertralineHydrochloride : Plugin() {

    var debug = true
    var console = console()
    var items = LinkedHashMap<String, SertralineItem>()
    override fun onEnable() {
        boot()
    }


}