package io.github.zzzyyylllty.sertraline.util.dependencies

import io.github.zzzyyylllty.sertraline.attribute.AttributeManager
import org.bukkit.entity.Player

@Deprecated(
    "Use AttributeManager.refreshAttributes() instead",
    ReplaceWith("AttributeManager.refreshAttributes(player)")
)
object AttributeUtil {
    fun refreshAttributes(player: Player) {
        AttributeManager.refreshAttributes(player)
    }
}
