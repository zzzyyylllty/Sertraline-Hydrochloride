package io.github.zzzyyylllty.sertraline.util

import io.github.zzzyyylllty.sertraline.logger.severeS
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.entity.Player
import java.util.concurrent.atomic.AtomicBoolean

private val papiLoggedMissing = AtomicBoolean(false)

fun String.replacePlaceholderSafety(player: Player?): String {
    return try {
        PlaceholderAPI.setPlaceholders(player, this)
    } catch (ex: NoClassDefFoundError) {
        if (papiLoggedMissing.compareAndSet(false, true)) {
            severeS("PlaceholderAPI Not Found")
        }
        this
    }
}
fun List<String>.replacePlaceholderSafety(player: Player?): List<String> {
    return try {
        PlaceholderAPI.setPlaceholders(player, this)
    } catch (ex: NoClassDefFoundError) {
        if (papiLoggedMissing.compareAndSet(false, true)) {
            severeS("PlaceholderAPI Not Found")
        }
        this
    }
}