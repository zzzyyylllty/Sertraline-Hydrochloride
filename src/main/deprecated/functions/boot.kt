package io.github.zzzyyylllty.functions

import io.github.zzzyyylllty.SertralineHydrochloride
import io.github.zzzyyylllty.SertralineHydrochloride.console
import taboolib.common.platform.function.info
import taboolib.common.platform.function.pluginId
import taboolib.common.platform.function.pluginVersion
import taboolib.module.lang.Level
import taboolib.module.lang.asLangText
import taboolib.module.lang.sendLang

fun boot() {
    info(console.asLangText("enable.boot.copyright"))
    info(console.asLangText("enable.boot.plugin", pluginId))
    info(console.asLangText("enable.boot.version", pluginVersion))
}
