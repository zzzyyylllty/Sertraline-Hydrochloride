package io.github.zzzyyylllty.sertraline.load

import com.google.gson.GsonBuilder
import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.Sertraline.packMap
import io.github.zzzyyylllty.sertraline.data.Action
import io.github.zzzyyylllty.sertraline.data.Key
import io.github.zzzyyylllty.sertraline.data.SertralineItem
import io.github.zzzyyylllty.sertraline.data.SertralineMaterial
import io.github.zzzyyylllty.sertraline.data.SertralineMeta
import io.github.zzzyyylllty.sertraline.data.deSerializeKey
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.function.sertralize.AnySerializer
import io.github.zzzyyylllty.sertraline.function.sertralize.ConfigurationSerializableAdapter
import io.github.zzzyyylllty.sertraline.function.sertralize.PatternTypeAdapter
import io.github.zzzyyylllty.sertraline.function.sertralize.TimeZoneTypeAdapter
import io.github.zzzyyylllty.sertraline.logger.warningL
import io.github.zzzyyylllty.sertraline.logger.warningS
import kotlinx.serialization.Serializable
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.util.getMap
import java.util.LinkedHashMap
import org.bukkit.block.banner.Pattern
import taboolib.module.lang.asLangText
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

    val keyEntry = deSerializeKey(root)
    if (packMap[keyEntry.namespace] == null) {
        warningS(console.asLangText("DebugLoadingItem", keyEntry))
    }

    devLog(console.asLangText("DebugLoadingItem", iconfig.name, root))

    val config = iconfig.getConfigurationSection(root)!!

    var customMeta = iconfig.getConfigurationSection(root)?.getValues(false)!! as LinkedHashMap<String, @Serializable(AnySerializer::class) Any?>

    customMeta.remove("minecraft")
    customMeta.remove("sertraline")

    // load nbt map
    val nbtConfig = config.get("minecraft.nbt")
    devLog("nbtConfig: ${nbtConfig}")
    val str = gsonBuilder.toJson(nbtConfig)
    devLog("json: ${str.toString()}")
    val pNbts = gsonBuilder.fromJson<List<Map<String, Any?>>?>(str ?: "[{}]", List::class.java)
    val nbts = LinkedHashMap<String, Any?>()
    for (map in pNbts ?: emptyList()) {
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

    val actions = mutableListOf<Action>()
    devLog(config.getMapList("sertraline.action").toString())
    config.getMapList("sertraline.action").forEach {
        actions.add(Action(
            trigger = it["trigger"] as String,
            condition = serializeStringList(it["conditions"]),
            kether = serializeStringList(it["kether"]),
        ))
    }


    return SertralineItem(
        minecraftItem = item,
        sertralineMeta = SertralineMeta(
            key = keyEntry,
            parent = config.getString("sertraline.parent")?.getKey(),
            data = config.getMap<String, Any?>("sertraline.data") as HashMap<String, @Serializable(AnySerializer::class) Any?>,
            actions = if (actions.isEmpty()) null else actions
        ),
        customMeta = customMeta
    )
}
