package io.github.zzzyyylllty.sertraline.listener.sertraline

import io.github.zzzyyylllty.sertraline.compat.PlatformCompat
import org.bukkit.entity.Player
import org.bukkit.event.inventory.SmithItemEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent

// 独立文件：SmithItemEvent / SmithingInventory 是 1.16+ API，legacy12（v11200）编译面不存在，
// 由 common/build.gradle.kts 的 sourceSets exclude 排除（1.12.2 无锻造台，功能本身不适用）

@SubscribeEvent(priority = EventPriority.HIGHEST)
fun onSmithItem(event: SmithItemEvent) {
    val player = event.whoClicked as? Player ?: return

    val inv = event.inventory
    // SmithItemEvent recipe API varies across versions; we conservatively
    // pass isVanilla=true so TRUE and VANILLA both block.
    if (PlatformCompat.getSmithingInputs(inv).any { checkItemForBlock(it, "disable-smithing", isVanilla = true) }) {
        event.isCancelled = true
        sendBlockMessage(player, "smithing-blocked")
    }
}
