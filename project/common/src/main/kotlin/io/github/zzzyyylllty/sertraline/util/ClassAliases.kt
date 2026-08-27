package io.github.zzzyyylllty.sertraline.util

/**
 * Event class name resolution: supports fully qualified names as well as shorthand that omits prefixes such as org.bukkit.event.
 * Reference Vulpecula's ClassAliases.
 */
object ClassAliases {

    private val PREFIXES = arrayOf(
        "org.bukkit.event",
        "org.bukkit.event.player",
        "org.bukkit.event.entity",
        "org.bukkit.event.block",
        "org.bukkit.event.inventory",
        "org.bukkit.event.world",
        "org.bukkit.event.server",
        "org.bukkit.event.vehicle",
        "org.bukkit.event.weather",
        "org.bukkit.event.hanging",
        "org.bukkit.event.enchantment",
        "com.destroystokyo.paper.event",
        "com.destroystokyo.paper.event.player",
        "com.destroystokyo.paper.event.entity",
        "io.papermc.paper.event",
        "io.papermc.paper.event.player",
        "io.papermc.paper.event.entity",
    )

    /** Resolve the class name, returning null on failure */
    fun getClass(name: String): Class<*>? {
        resolve(name)?.let { return it }
        val simple = name.substringAfterLast('.')
        if (simple == name) {
            for (prefix in PREFIXES) {
                resolve("$prefix.$simple")?.let { return it }
            }
        }
        return null
    }

    private fun resolve(name: String): Class<*>? = try {
        Class.forName(name)
    } catch (_: Throwable) {
        null
    }
}
