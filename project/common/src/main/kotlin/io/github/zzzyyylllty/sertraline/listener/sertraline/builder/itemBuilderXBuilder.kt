package io.github.zzzyyylllty.sertraline.listener.sertraline.builder

import io.github.zzzyyylllty.sertraline.Sertraline.itemManager
import io.github.zzzyyylllty.sertraline.item.xbuilder.xbuilderAdapter

fun registerNativeAdapter() {

    itemManager.registerProcessor("xbuilder") { sItem, item, player ->
        xbuilderAdapter(item, sItem)
    }
}