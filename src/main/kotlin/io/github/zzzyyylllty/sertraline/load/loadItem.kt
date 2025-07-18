package io.github.zzzyyylllty.sertraline.load

import com.google.gson.GsonBuilder
import io.github.zzzyyylllty.sertraline.data.Key
import io.github.zzzyyylllty.sertraline.data.SertralineItem
import io.github.zzzyyylllty.sertraline.data.SertralineMaterial
import io.github.zzzyyylllty.sertraline.data.SertralineMeta
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.function.item.jsonUtils
import io.github.zzzyyylllty.sertraline.function.sertralize.AnySerializer
import io.github.zzzyyylllty.sertraline.function.sertralize.ConfigurationSerializableAdapter
import io.github.zzzyyylllty.sertraline.function.sertralize.PatternTypeAdapter
import io.github.zzzyyylllty.sertraline.function.sertralize.TimeZoneTypeAdapter
import kotlinx.serialization.Serializable
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.util.getMap
import java.util.LinkedHashMap
import kotlinx.serialization.decodeFromString
import org.bukkit.block.banner.Pattern
import java.util.TimeZone

val gsonBuilder = GsonBuilder()
    .setVersion(1.0)
    .disableJdkUnsafe()
    .disableHtmlEscaping()
    .disableInnerClassSerialization()
    .setPrettyPrinting()
    .excludeFieldsWithModifiers()
    .setLenient()
    .registerTypeAdapter(TimeZone::class.java, TimeZoneTypeAdapter())
    .registerTypeAdapter(Pattern::class.java, PatternTypeAdapter())
    .registerTypeAdapter(ConfigurationSerializableAdapter::class.java, PatternTypeAdapter())
    .create()


fun loadItem(iconfig: Configuration, root: String) : SertralineItem {

    val keys = root.split(":")
    val keyEntry = Key(
        keys[0], keys[1]
    )

    val config = iconfig.getConfigurationSection(root)!!

    var customMeta = iconfig.getConfigurationSection(root)?.getValues(false)!! as kotlin.collections.LinkedHashMap<String, @Serializable(AnySerializer::class) Any?>

    customMeta.remove("minecraft")
    customMeta.remove("sertraline")

    val str = gsonBuilder.toJson(config.getConfigurationSection("minecraft.nbt"))
    devLog(str.toString())
    val pNbts = gsonBuilder.fromJson<List<HashMap<String, @Serializable(AnySerializer::class) Any?>>>(str ?: "[{}]", List::class.java)
    val nbts = HashMap<String, Any?>()
    for (map in pNbts) {
        for (entry in map) {
            nbts.put(entry.key, entry.value)
        }
    }

    val item = SertralineMaterial(
        material = config.getString("minecraft.material"),
        displayName = config.getString("minecraft.display-name") ,
        lore = serializeStringList(config.get("minecraft.lore")),
        model = config.getInt("minecraft.model") ,
        nbt = nbts,
        extra = config.getMap<String, Any?>("minecraft.extra") as HashMap<String, @Serializable(AnySerializer::class) Any?>,
    )
    return SertralineItem(
        minecraftItem = item,
        sertralineMeta = SertralineMeta(
            key = keyEntry,
            parent = config.getString("sertraline.parent")?.getKey(),
            data = config.getMap<String, Any?>("sertraline.data") as HashMap<String, @Serializable(AnySerializer::class) Any?>,
        ),
        customMeta = customMeta
    )
}
