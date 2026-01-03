package io.github.zzzyyylllty.sertraline.hook

import ink.ptms.chemdah.api.event.InferItemHookEvent
import ink.ptms.chemdah.core.quest.selector.DataMatch
import ink.ptms.chemdah.core.quest.selector.Flags
import ink.ptms.chemdah.core.quest.selector.InferItem
import ink.ptms.chemdah.taboolib.module.kether.action.transform.CheckType
import ink.ptms.chemdah.um.Mythic
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.event.SertralineCustomScriptDataLoadEvent
import io.github.zzzyyylllty.sertraline.function.data.getSertralineId
import io.github.zzzyyylllty.sertraline.logger.infoS
import io.github.zzzyyylllty.sertraline.util.DependencyHelper
import io.github.zzzyyylllty.sertraline.util.ItemTagUtil.parseMapNBT
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import taboolib.common.LifeCycle
import taboolib.common.platform.Ghost
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.kether.inferType
import taboolib.module.nms.getItemTag

@Ghost
@SubscribeEvent
fun mmDataHook(e: SertralineCustomScriptDataLoadEvent) {
    if (Mythic.isLoaded()) {
        val api = Mythic.API
        infoS("Hooking onto mythicmobs")
        e.defaultData["UMAPI"] = api
    }
}