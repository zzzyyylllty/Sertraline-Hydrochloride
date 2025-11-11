package io.github.zzzyyylllty.sertraline.listener.sertraline.tag

import io.github.zzzyyylllty.sertraline.Sertraline.itemManager
import io.github.zzzyyylllty.sertraline.Sertraline.tagManager
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.item.adapter.minecraftAdapter
import io.github.zzzyyylllty.sertraline.item.adapter.xbuilderAdapter
import io.github.zzzyyylllty.sertraline.item.process.sertralineTagProcessor
import io.github.zzzyyylllty.sertraline.item.sertralineItemBuilder


fun registerNativeTagAdapter() {

    tagManager.registerProcessor("sertraline") { data, player ->
        devLog("Tag adapting")
        sertralineTagProcessor(data, player)
    }

}
