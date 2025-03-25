package io.github.zzzyyylllty.sertraline.function.load

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import taboolib.common.util.asList
import taboolib.library.xseries.XMaterial
import taboolib.module.chat.colored
import taboolib.platform.util.buildItem
import taboolib.platform.util.modifyLore

fun loadMaterialPart(config: YamlConfiguration, root: String) : ItemStack {
    val item = buildItem(XMaterial.valueOf(config["$root.minecraft.material"].toString())) {
        name = config["$root.minecraft.name"].toString()
        colored()
    }.modifyLore {
        val strings = config.getString("$root.minecraft.lore")
        if (strings != null) {
            for (string in strings.split("\n")) {
                add("$string<reset>")
            }
            colored()
        }
    }

    return item
}