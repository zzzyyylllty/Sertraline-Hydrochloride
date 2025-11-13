package io.github.zzzyyylllty.sertraline.event

import taboolib.platform.type.BukkitProxyEvent

class ItemLoadEvent(val itemKey: String, val arguments: Map<String, Any?>, var itemData: MutableMap<String, Any?>) : BukkitProxyEvent()

class FeatureLoadEvent(
    val sItemKey: String?,
    val content: Any?, // 此节点的内容
    val feature: String,
    var result: Any?
) : BukkitProxyEvent()
