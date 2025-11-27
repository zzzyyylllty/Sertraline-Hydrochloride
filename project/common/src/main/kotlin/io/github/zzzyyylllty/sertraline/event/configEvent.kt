package io.github.zzzyyylllty.sertraline.event

import taboolib.platform.type.BukkitProxyEvent

class SertralineReloadEvent() : BukkitProxyEvent()

/**
 * Modify [defaultData] you can register
 * your custom utils.
 * DO NOT USE clear, re-set or directly modify it, OR OTHER SENSITIVE FUNCTIONS.
 * */
class SertralineCustomScriptDataLoadEvent(
    var defaultData: LinkedHashMap<String, Any?>
) : BukkitProxyEvent()
