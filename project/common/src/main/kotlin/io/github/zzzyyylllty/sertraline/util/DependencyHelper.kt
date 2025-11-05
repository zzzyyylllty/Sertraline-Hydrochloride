package io.github.zzzyyylllty.sertraline.util

import org.bukkit.Bukkit
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.MinecraftVersion.versionId

class DependencyHelper {
    fun isPluginInstalled(name: String): Boolean {
        return (Bukkit.getPluginManager().getPlugin(name) != null)
    }
}