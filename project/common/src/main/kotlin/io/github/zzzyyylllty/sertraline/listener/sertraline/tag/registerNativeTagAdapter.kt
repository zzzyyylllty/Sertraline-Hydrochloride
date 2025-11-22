package io.github.zzzyyylllty.sertraline.listener.sertraline.tag

import io.github.zzzyyylllty.sertraline.Sertraline.tagManager
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.item.process.papiTagProcessor
import io.github.zzzyyylllty.sertraline.item.process.sertralineTagProcessor
import io.github.zzzyyylllty.sertraline.util.DependencyHelper


fun registerNativeTagAdapter() {

    if (DependencyHelper.papi) tagManager.registerProcessor("papi") { data, player ->
        devLog("Tag adapting papi")
        papiTagProcessor(data, player)
    }
    tagManager.registerProcessor("sertraline") { data, player ->
        devLog("Tag adapting sertraline")
        sertralineTagProcessor(data, player)
    }

}
