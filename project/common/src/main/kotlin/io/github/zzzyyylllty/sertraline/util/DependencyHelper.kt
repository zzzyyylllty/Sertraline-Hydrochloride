package io.github.zzzyyylllty.sertraline.util

import com.cryptomorin.xseries.XMaterial
import ink.ptms.um.event.MobDropLoadEvent
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.item.sertralineItemBuilder
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.Ghost
import taboolib.common.platform.event.SubscribeEvent
import java.util.*
import kotlin.math.roundToInt


val dependencies = listOf(
    "adventure",
    // "arim",
    "caffeine",
    "datafixerupper",
    "fluxon",
    // "graaljs",
    "gson",
    "jackson",
    // "kotlincrypto",
//    "uniitem"
)

object DependencyHelper {

    val mmLib by lazy {
        isPluginInstalled("MythicLib")
    }

    val mm by lazy {
        isPluginInstalled("MythicMobs")
    }

    val papi by lazy {
        isPluginInstalled("PlaceholderAPI")
    }

    val pe by lazy {
        isPluginInstalled("packetevents")
    }




    fun isPluginInstalled(name: String): Boolean {
        return (Bukkit.getPluginManager().getPlugin(name) != null)
    }

}


@Ghost
@SubscribeEvent
fun onDropLoad(event: MobDropLoadEvent) {
    // 注册自定义掉落

    devLog("[MMCompat] dropName: ${event.dropName}")
    if (!event.dropName.contains("sertraline")) return
    val id = extractItemId(event.dropName)
    devLog("[MMCompat] ID: $id")

    event.registerItem { dropMeta ->
        val killer = dropMeta.cause as? Player
        val id = event.dropName
        val item = sertralineItemBuilder(id, killer)
        item?.amount = dropMeta.amount.roundToInt()
        item ?: XMaterial.STONE.parseItem()!!
    }
}

private fun extractItemId(line: String): String? {
    if (line.startsWith("sertraline ")) {
        val nextSpaceIndex = line.indexOf(' ', 11)
        return if (nextSpaceIndex == -1)
            line.substring(11)
        else
            line.substring(11, nextSpaceIndex)
    }
    if (line.startsWith("sertraline:")) {
        val spaceIndex = line.indexOf(' ', 11)
        return if (spaceIndex == -1)
            line.substring(11)
        else
            line.substring(11, spaceIndex)
    }
    return null
}