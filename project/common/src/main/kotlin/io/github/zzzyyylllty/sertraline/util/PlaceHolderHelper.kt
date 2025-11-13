package io.github.zzzyyylllty.sertraline.util

import io.github.zzzyyylllty.sertraline.logger.severeS
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.entity.Player

fun String.replacePlaceholderSafety(player: Player?): String {
    return try {
        PlaceholderAPI.setPlaceholders(player, this)
    } catch (ex: NoClassDefFoundError) {
        severeS("PlaceholderAPI Not Found")
        this
    }
}
fun List<String>.replacePlaceholderSafety(player: Player?): List<String> {
    return try {
        PlaceholderAPI.setPlaceholders(player, this)
    } catch (ex: NoClassDefFoundError) {
        severeS("PlaceholderAPI Not Found")
        this
    }
}