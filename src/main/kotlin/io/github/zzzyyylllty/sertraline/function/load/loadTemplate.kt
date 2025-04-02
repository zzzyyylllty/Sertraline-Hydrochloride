package io.github.zzzyyylllty.sertraline.function.load

import org.bukkit.configuration.file.YamlConfiguration

fun loadTemplate(config: YamlConfiguration, root: String) : org.bukkit.configuration.ConfigurationSection? {
    return config.getConfigurationSection(root)
}