package io.github.zzzyyylllty.sertraline.load

import io.github.zzzyyylllty.sertraline.data.Key
import io.github.zzzyyylllty.sertraline.data.SertralineItem
import io.github.zzzyyylllty.sertraline.data.SertralineMaterial
import io.github.zzzyyylllty.sertraline.data.SertralineMeta
import io.github.zzzyyylllty.sertraline.function.sertralize.AnySerializer
import kotlinx.serialization.Serializable
import org.bukkit.configuration.file.YamlConfiguration

fun loadItem(iconfig: YamlConfiguration, root: String) : SertralineItem {

    val keys = root.split(":")
    val keyEntry = if (keys[0] != null) Key(
        keys[0], keys[1]
    ) else throw NullPointerException("The item must have a namespace, example: chotenpack:test")

    val config = iconfig.getConfigurationSection(root)!!

    var customMeta = iconfig.getConfigurationSection(root)?.getValues(false)!! as kotlin.collections.LinkedHashMap<String, @Serializable(AnySerializer::class) Any?>

    customMeta.remove("minecraft")
    customMeta.remove("sertraline")

    val item = SertralineMaterial(
        material = config.getString("minecraft.material"),
        displayName = config.getString("minecraft.display-name") ,
        lore = serializeStringList(config.get("minecraft.lore")),
        model = config.getInt("minecraft.model") ,
        nbt = (config.getConfigurationSection("minecraft.nbt")?.getValues(false) ?: linkedMapOf<String, @Serializable(AnySerializer::class) Any>()) as kotlin.collections.LinkedHashMap<String, @Serializable(AnySerializer::class) Any?>,
        extra = (config.getConfigurationSection("minecraft.extra")?.getValues(false) ?: linkedMapOf<String, @Serializable(AnySerializer::class) Any>()) as kotlin.collections.LinkedHashMap<String, @Serializable(AnySerializer::class) Any?>,
    )
    return SertralineItem(
        minecraftItem = item,
        sertralineMeta = SertralineMeta(
            key = keyEntry,
            parent = config.getString("sertraline.parent")?.getKey(),
            data = (config.getConfigurationSection("sertraline.data")?.getValues(false) ?: linkedMapOf<String, @Serializable(AnySerializer::class) Any>()) as kotlin.collections.LinkedHashMap<String, @Serializable(AnySerializer::class) Any?>,
        ),
        customMeta = customMeta
    )
}
