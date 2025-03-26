package io.github.zzzyyylllty.sertraline.function.load

import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import taboolib.common.util.asList
import taboolib.library.xseries.XMaterial
import taboolib.module.chat.colored
import taboolib.platform.util.buildItem
import taboolib.platform.util.modifyLore

fun loadMaterialPart(config: YamlConfiguration, root: String) : ItemStack {
    val legacyApi = LegacyComponentSerializer.legacyAmpersand()
    val mm = MiniMessage.miniMessage()
    val item = buildItem(XMaterial.valueOf(config["$root.minecraft.material"].toString())) {
        name = legacyApi.serialize(mm.deserialize("<white>${config["$root.minecraft.name"].toString()}"))
        colored()
    }.modifyLore {
        val strings = config.getString("$root.minecraft.lore")
        if (strings != null) {
            for (string in strings.split("\n")) {
                add(legacyApi.serialize(mm.deserialize("<white>$string<reset>")).replace("&", "§"))
                colored()
            }
        }
        colored()
    }

    return item
}