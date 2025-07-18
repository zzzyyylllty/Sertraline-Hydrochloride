package io.github.zzzyyylllty.sertraline.function.kether

import ink.ptms.zaphkiel.api.ItemEvent
import ink.ptms.zaphkiel.api.ItemStream
import io.github.zzzyyylllty.sertraline.data.DepazItemInst
import io.github.zzzyyylllty.sertraline.data.DepazItems
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.function.item.buildInstance
import io.github.zzzyyylllty.sertraline.function.item.buildItem
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerEvent
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submitAsync
import taboolib.module.kether.KetherShell.eval
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.ScriptOptions
import java.util.concurrent.CompletableFuture

// Some code from ink.ptms.zaphkiel.impl.item.DefaultItemEvent
fun DepazItemInst?.directInvokeItemEvent(player: Player?, event: Event, data: Map<String, Any?>, script: List<String>): CompletableFuture<DepazItemInst?> {
        val future = CompletableFuture<DepazItemInst?>()
        val depaz = this@directInvokeItemEvent
        val options = ScriptOptions.new {
            if (player != null) {
                sender(player)
            }
            vars(data)
            set("@Event", event)
            set("@Item", depaz)
            sandbox()
            detailError()
        }
        eval(script, options).thenRun {
            if (depaz != null) {
                future.complete(depaz)
            } else {
                future.complete(null)
            }
        }
        devLog("invoking item for player ${player?.player?.name} event ${event.eventName} data $data script $script for instance $depaz")
        return future
}

fun ScriptFrame.getScriptItem(): DepazItemInst {
    return variables().get<Any?>("@Item").orElse(null) as? DepazItemInst ?: error("No item-stream selected.")
}