import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import io.github.zzzyyylllty.sertraline.data.SertralineItem
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.function.kether.evalKetherValue
import io.github.zzzyyylllty.sertraline.function.sertralize.AnySerializer
import io.github.zzzyyylllty.sertraline.function.sertralize.ConfigurationSerializableAdapter
import io.github.zzzyyylllty.sertraline.function.sertralize.PatternTypeAdapter
import io.github.zzzyyylllty.sertraline.function.sertralize.TimeZoneTypeAdapter
import io.github.zzzyyylllty.sertraline.logger.warningS

import kotlinx.serialization.json.Json
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Color
import org.bukkit.DyeColor
import org.bukkit.block.banner.Pattern
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import taboolib.common.platform.function.console
import taboolib.library.xseries.XItemFlag
import taboolib.library.xseries.XMaterial
import taboolib.library.xseries.XPatternType
import taboolib.module.lang.asLangText
import taboolib.module.nms.getItemTag
import taboolib.platform.util.buildItem
import java.util.Locale
import kotlinx.serialization.modules.SerializersModule
import taboolib.library.xseries.XEnchantment
import taboolib.library.xseries.XPotion
import java.lang.reflect.Modifier
import java.util.Random
import java.util.TimeZone
import kotlin.jvm.java

val jsonUtils = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
    coerceInputValues = true
    encodeDefaults = true
    allowStructuredMapKeys = true
    allowSpecialFloatingPointValues = true

    serializersModule = SerializersModule {
        contextual(Any::class, AnySerializer)
    }
}

val gsonBuilder = GsonBuilder()
    .setVersion(1.0)
    .disableJdkUnsafe()
    .disableHtmlEscaping()
    .excludeFieldsWithModifiers(Modifier.STATIC) // 添加这一行，排除静态字段
    .excludeFieldsWithModifiers(Modifier.TRANSIENT) // 排除transient字段
    .setExclusionStrategies(object : ExclusionStrategy {
        override fun shouldSkipField(f: FieldAttributes): Boolean {
            // 排除可能导致问题的Java内部类
            return f.declaredType.typeName.startsWith("java.") ||
                    f.declaredType.typeName.startsWith("sun.") ||
                    f.declaredType.typeName.startsWith("com.sun.")
        }

        override fun shouldSkipClass(clazz: Class<*>): Boolean {
            // 排除特定问题类
            return clazz == Random::class.java ||
                    clazz == Pattern::class.java
        }
    })
    .disableInnerClassSerialization()
    .setPrettyPrinting()
    .excludeFieldsWithModifiers()
    .setLenient()
    .registerTypeAdapter(TimeZone::class.java, TimeZoneTypeAdapter())
    .registerTypeAdapter(Pattern::class.java, PatternTypeAdapter())
    .registerTypeAdapter(ConfigurationSerializableAdapter::class.java, PatternTypeAdapter())
    .create()



fun SertralineItem.buildItem(player: Player?): ItemStack {

    devLog("prepare encode item: $this")

    var datajson = gsonBuilder.toJson(this.sertralineMeta.data)

    while (datajson.contains("\\{\\{(.+?)\\}\\}".toRegex())) {
        val pattern = "\\{\\{(.+?)\\}\\}".toRegex()

        val found = pattern.findAll(datajson)

        found.forEach { f ->
            val m = f.value
            val section = m.substring(2..(m.length-3))
            devLog("Founded data kether shell module $m , $section")
            datajson = datajson.replace(m,section.evalKetherValue(player, this.sertralineMeta.data).toString())
        }
    }

    val data = jsonUtils.decodeFromString<LinkedHashMap<String, (with = AnySerializer::class) Any?>>(datajson)

    var json = gsonBuilder.toJson(this)

    devLog("encoded item json: $json")

    while (json.contains("\\{\\{(.+?)\\}\\}".toRegex())) {
        val pattern = "\\{\\{(.+?)\\}\\}".toRegex()

        val found = pattern.findAll(json)

        found.forEach { f ->
            val m = f.value
            val section = m.substring(2..(m.length-3))
            devLog("Founded kether shell module $m , $section")
            json = json.replace(m,section.evalKetherValue(player, data).toString())
        }
    }
    devLog("kethered json: $json")

    val parsed = jsonUtils.decodeFromString<SertralineItem>(json)

    devLog("parsed: $parsed")
    return parsed.initializeItem(player)
}

fun SertralineItem.initializeItem(player: Player?): ItemStack {

    val mc = minecraftItem
    val smeta = sertralineMeta

    val xMaterial = XMaterial.matchXMaterial(mc.material ?: "STONE").orElse(XMaterial.STONE)
    devLog("Resolved XMaterial: $xMaterial")
    var material = buildItem(xMaterial) {
        mc.displayName?.let { name = it }
        mc.model?.let { customModelData = it }
        mc.extra.forEach {
            val value = it.value
            when (it.key.lowercase(Locale.getDefault())) {
                "damage","dam" -> damage = value.toString().toInt()
                "unbreak","unbreakable","isunbreakable" -> isUnbreakable = value.toString().toBoolean()
                "flag","flags","hideflags" -> (value as List<String>).let {
                    for (s in it) {
                        XItemFlag.valueOf(s).get()?.let { e -> flags.add(e) } ?: run {
                            warningS(
                                console().asLangText(
                                    "ItemFlagSkipped",
                                    "${smeta.key}"
                                )
                            )
                            null
                        }
                    }
                }
                "color","colour" -> (value.toString()).let {
                    color = if (it.contains(",")) {
                        val split = it.split(",")
                        Color.fromRGB(split[0].toInt(), split[1].toInt(), split[2].toInt())
                    } else {
                        Color.fromRGB(it.toInt())
                    }
                }
                "pattern", "patterns" -> (value as List<String>).let {
                    for (s in it) {
                        val dyeColor = DyeColor.valueOf(s.split(",")[0])
                        val pattern = XPatternType.of(s.split(",")[1]).get().get()
                        pattern?.let { it1 -> patterns.add(Pattern(dyeColor, it1)) } ?: run {
                            warningS(
                                console().asLangText(
                                    "ItemPatternSkipped",
                                    "${smeta.key}"
                                )
                            )
                            null
                        }
                    }
                }
                "ench","enchant","enchants" -> (value as List<String>).let {
                    for (s in it) {
                        val enchant = XEnchantment.of(s.split(",")[0]).get().get() ?: run {
                            warningS(
                                console().asLangText(
                                    "ItemEnchantSkipped",
                                    "${smeta.key}"
                                )
                            )
                            null
                        }
                        val level = (s.split(",")[1]).toInt()
                        enchant?.let { key -> enchants.put(key, level) }
                    }
                }
                "effect", "potion", "potions" -> (value as List<String>).let {
                    for (s in it) {
                        val potion = XPotion.valueOf(s.split(",")[0]).potionEffectType ?: run {
                            warningS(
                                console().asLangText(
                                    "ItemPotionSkipped",
                                    "${smeta.key}"
                                )
                            )
                            null
                        }
                        val duration = s.split(",")[1].toString().toInt()
                        val amp =s.split(",")[2].toString().toInt()
                        potion?.let { potions.add(PotionEffect(it, duration, amp)) }
                    }
                }

            }
        }
    }

    // write nbt
    val tag = material.getItemTag()
    mc.nbt?.forEach {
        val key = it.key
        tag.put(key, it.value)
        devLog("writing nbt ${it.key} = ${it.value}")
    }
    tag.put("sertraline_id", sertralineMeta.key.serialize())
    tag.put("SERTRALINE_DATA", sertralineMeta.data)

    tag.saveTo(material)

    val mm = MiniMessage.miniMessage()
    val legacy = LegacyComponentSerializer.legacyAmpersand()
    var compLore : MutableList<Component>? = null
    mc.lore?.forEach {
        if (compLore == null) compLore = mutableListOf()
        val comp = mm.deserialize(legacy.serialize(legacy.deserialize(it.replace("§", "&"))))
        compLore.add(comp.decorationIfAbsent(TextDecoration.ITALIC,TextDecoration.State.FALSE))
    }

    if (mc.displayName != null) {
        val meta = material.itemMeta
        val name = mm.deserialize(mc.displayName).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
        meta.displayName(name)
        material.setItemMeta(meta)
    }
    material.lore(compLore)

    return material
}
