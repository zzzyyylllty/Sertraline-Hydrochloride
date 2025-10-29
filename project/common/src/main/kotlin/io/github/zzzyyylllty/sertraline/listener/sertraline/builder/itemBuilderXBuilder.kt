package io.github.zzzyyylllty.sertraline.listener.sertraline.builder

import io.github.zzzyyylllty.sertraline.Sertraline.itemManager
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.item.adapter.minecraftAdapter
import io.github.zzzyyylllty.sertraline.item.adapter.xbuilderAdapter

fun registerNativeAdapter() {

    itemManager.registerProcessor("minecraft") { sItem, item, player ->
        devLog("Adapting minecraft")
        minecraftAdapter(item, sItem, player)
    }
    itemManager.registerProcessor("xbuilder") { sItem, item, player ->
        devLog("Adapting xbuilder")
        xbuilderAdapter(item, sItem, player)
    }

}