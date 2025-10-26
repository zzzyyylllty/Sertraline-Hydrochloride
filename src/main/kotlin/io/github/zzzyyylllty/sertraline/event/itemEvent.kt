package io.github.zzzyyylllty.sertraline.event

import taboolib.platform.type.BukkitProxyEvent

class ItemLoadEvent(val itemKey: String, val arguments: Map<String, Any?>, var itemData: Map<String, Any?>) : BukkitProxyEvent() {

}