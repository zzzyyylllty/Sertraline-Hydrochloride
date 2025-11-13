package io.github.zzzyyylllty.sertraline.listener.sertraline.builder

import io.github.zzzyyylllty.sertraline.Sertraline.itemManager
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.item.adapter.minecraftAdapter
import io.github.zzzyyylllty.sertraline.item.adapter.sertralineAdapter
import io.github.zzzyyylllty.sertraline.item.adapter.xbuilderAdapter
import io.github.zzzyyylllty.sertraline.util.VersionHelper

fun registerNativeAdapter() {

    itemManager.registerProcessor("xbuilder") { sItem, item, player ->
        devLog("Adapting xbuilder")
        xbuilderAdapter(item, sItem, player)
    }
    itemManager.registerProcessor("sertraline") { sItem, item, player ->
        devLog("Adapting sertraline")
        sertralineAdapter(item, sItem, player)
    }
    if (VersionHelper().isOrAbove12005()) itemManager.registerProcessor("minecraft") { sItem, item, player ->
        devLog("Adapting minecraft")
        minecraftAdapter(item, sItem, player)
    }

}