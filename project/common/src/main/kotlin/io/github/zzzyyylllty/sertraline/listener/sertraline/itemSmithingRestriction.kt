package io.github.zzzyyylllty.sertraline.listener.sertraline

import io.github.zzzyyylllty.sertraline.compat.PlatformCompat
import org.bukkit.entity.Player
import org.bukkit.event.inventory.SmithItemEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent

// Standalone file: SmithItemEvent / SmithingInventory are 1.16+ APIs and do not exist on the legacy12 (v11200) build surface,
// excluded via sourceSets in common/build.gradle.kts (1.12.2 has no smithing table, the feature itself is not applicable)

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
