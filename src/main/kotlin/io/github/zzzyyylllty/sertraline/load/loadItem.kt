package io.github.zzzyyylllty.sertraline.load

import io.github.zzzyyylllty.sertraline.data.Key
import io.github.zzzyyylllty.sertraline.data.SertralineItem
import io.github.zzzyyylllty.sertraline.data.SertralineMaterial
import io.github.zzzyyylllty.sertraline.data.SertralineMeta
import org.bukkit.configuration.file.YamlConfiguration

fun loadItem(iconfig: YamlConfiguration, root: String) : SertralineItem {

    val keys = root.split(":")
    val keyEntry = if (keys[0] != null) Key(
        keys[0], keys[1]
    ) else throw NullPointerException("The item must have a namespace, example: chotenpack:test")

    val config = iconfig.getConfigurationSection(root)

    val item = SertralineMaterial(
        material = config.get("minecraft.material"),
        displayName = config.get("minecraft.display-name"),
        lore = config.get("minecraft.lore"),
        nbt = config.get("minecraft.nbt")
    )
    return SertralineItem(
        minecraftItem = item,
        sertralineMeta = SertralineMeta(
            key = TODO(),
            parent = TODO(),
            data = TODO(),
            customMeta = TODO()
        )
    )
}
