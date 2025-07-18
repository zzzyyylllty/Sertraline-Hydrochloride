package io.github.zzzyyylllty.sertraline.load

import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.data.SertralinePack
import io.github.zzzyyylllty.sertraline.logger.warningS
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.module.lang.asLangText

fun loadPack(iconfig: YamlConfiguration, root: String) : SertralinePack {
    return SertralinePack(
        namespace = root,
        description = iconfig.getString("$root.description") ?: run {
            warningS(console.asLangText("PackNoDesc", root))
            "No desc provided."
        },
        authors = iconfig.getStringList("$root.authors"),
        version = iconfig.getString("$root.authors") ?: run {
            warningS(console.asLangText("PackNoVersion", root))
            "1.0.0"
        },
        enabled = iconfig.getBoolean("$root.enabled", true),
        name = iconfig.getString("$root.name") ?: run {
            warningS(console.asLangText("PackNoName", root))
            root
        },
    )
}