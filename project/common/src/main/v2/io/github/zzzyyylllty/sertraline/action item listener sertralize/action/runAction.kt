package io.github.zzzyyylllty.sertraline.function.action

import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.data.deSerializeKey
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.function.item.getSavedData
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submitAsync
import taboolib.module.configuration.util.asMap
import taboolib.module.lang.asLangText
import taboolib.module.nms.getItemTag

fun Player.applyActions(trigger: String, e: Event, i: ItemStack,islot: Int? = null) {
    val player = this
    submitAsync {
        val inv = player.inventory
        devLog(console.asLangText("DebugActionApply", player.player?.name ?:"Unknown", trigger))
        devLog("Item: $i")
        val nbt = i.getItemTag()
        devLog("Nbt: $nbt")
        val id = (nbt.get("SERTRALINE_ID") ?: return@submitAsync).asString()
        devLog("Id: $id (${deSerializeKey(id)})")
        val data = (nbt.get("SERTRALINE_DATA") ?: emptyMap<String, Any>()).asMap()
        devLog("Id: $id (${deSerializeKey(id)})")
        val item = itemMap[deSerializeKey(id)] ?: return@submitAsync
        devLog("Sertraline: $item")
        item.sertralineMeta.actions?.forEach { if (it.trigger.equals(trigger, ignoreCase = true)) it.runAction(player, i.getSavedData() , i, e, item) }
    }
}