package io.github.zzzyyylllty.sertraline.function.load

import io.github.zzzyyylllty.sertraline.data.Action
import io.github.zzzyyylllty.sertraline.data.DepazItems
import io.github.zzzyyylllty.sertraline.function.error.throwNPEWithMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.XMaterial
import taboolib.module.nms.itemTagReader
import taboolib.platform.util.buildItem

fun loadTemplate(config: YamlConfiguration, root: String) : ConfigurationSection {
    return config.get(root) as ConfigurationSection
}