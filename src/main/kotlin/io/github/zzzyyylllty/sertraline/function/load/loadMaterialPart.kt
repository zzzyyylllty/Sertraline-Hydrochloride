package io.github.zzzyyylllty.sertraline.function.load

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import taboolib.common.util.asList
import taboolib.library.xseries.XMaterial
import taboolib.module.chat.colored
import taboolib.module.nms.setDisplayNameComponent
import taboolib.platform.util.buildItem
import taboolib.platform.util.modifyLore
import taboolib.platform.util.modifyMeta

fun loadMaterialPart(config: YamlConfiguration, root: String) : ItemStack {
    val legacyApi = LegacyComponentSerializer.legacyAmpersand()
    val mm = MiniMessage.miniMessage()

    val item = buildItem(XMaterial.valueOf(config["$root.minecraft.material"].toString()))
        var meta = item.itemMeta
    meta.displayName(mm.deserialize("<white>${config["$root.minecraft.name"].toString()}"))
    item.setItemMeta(meta)
    val strings = config.getString("$root.minecraft.lore")
    var lore : MutableList<Component> = mutableListOf()
    val legacy = LegacyComponentSerializer.legacyAmpersand()
    if (strings != null) {
        for (string in strings.split("\n")) {
            lore.add(mm.deserialize(legacy.serialize(legacy.deserialize(string.replace("§", "&")))))
        }
    }
    item.lore(lore)
    /*
    val item = buildItem(XMaterial.valueOf(config["$root.minecraft.material"].toString())) {
        name = legacyApi.serialize(mm.deserialize())
        colored()
    }.modifyLore {
        colored()
    }*/

    return item
}