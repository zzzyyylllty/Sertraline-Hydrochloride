package io.github.zzzyyylllty.sertraline.util

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.event.SertralineReloadEvent
import io.github.zzzyyylllty.sertraline.function.action.applyActions
import io.github.zzzyyylllty.sertraline.listener.action.ThrottleActionLink
import io.github.zzzyyylllty.sertraline.listener.action.ThrottleActionParam
import taboolib.common.function.ThrottleFunction
import taboolib.common.function.throttle
import taboolib.common.platform.event.SubscribeEvent


object ActionHelper {

    val actionInstances: LinkedHashMap<String, ThrottleFunction.Parameterized<ThrottleActionLink, ThrottleActionParam>?> = linkedMapOf()

    fun throttleAction(link: ThrottleActionLink, data: ThrottleActionParam) {
        val instance = actionInstances.getOrPut(link.str) {
            val time = config.getLong("action.throttle.${link.str}", config.getLong("action.throttle-time", 500))
            if (time < 1) null else
            throttle<ThrottleActionLink, ThrottleActionParam>(time) { link, data ->
                if (data.bItem == null || data.bItem.isEmpty) {
                    devLog("ItemStack is null or air or amount == 0,Skipping actions.")
                } else {
                    data.p.applyActions(link.str, data.e, data.ce, data.bItem)
                    data.p.applyActions(link.str + "@" + link.subStr, data.e, data.ce, data.bItem)
                }
            }
        }
        if (instance != null) {
            instance(link, data)
        } else {
            if (data.bItem == null || data.bItem.isEmpty) {
                devLog("ItemStack is null or air or amount == 0,Skipping actions.")
            } else {
                data.p.applyActions(link.str, data.e, data.ce, data.bItem)
                data.p.applyActions(link.str + "@" + link.subStr, data.e, data.ce, data.bItem)
            }
        }
    }

    @SubscribeEvent
    fun resetInstances(e: SertralineReloadEvent) {
        actionInstances.clear()
    }

}