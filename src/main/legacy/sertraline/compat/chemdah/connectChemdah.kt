package io.github.zzzyyylllty.connect.chemdah

import ink.ptms.chemdah.api.event.InferItemHookEvent
import ink.ptms.chemdah.core.quest.objective.Dependency
import org.bukkit.event.EventHandler
import taboolib.common.platform.Ghost
import taboolib.common.platform.event.SubscribeEvent


/* https://wiki.ptms.ink/index.php?title=Chemdah_%E5%BC%80%E5%8F%91%E8%80%85%E6%96%87%E6%A1%A3:%E5%BC%80%E5%A7%8B */

fun connectChemdah() {

}

@Ghost
@SubscribeEvent
fun e(e: InferItemHookEvent) {
    when (e.id.lowercase()) {
        "depaz","needy","sertraline","overdose" -> {
            e.itemClass = ItemDepazSelector::class.java
        }
    }
}