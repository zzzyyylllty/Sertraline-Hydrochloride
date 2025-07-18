package io.github.zzzyyylllty.sertraline.function.item

import com.google.gson.GsonBuilder
import ink.ptms.adyeshach.taboolib.library.xseries.XEnchantment
import ink.ptms.adyeshach.taboolib.library.xseries.XPotion
import io.github.zzzyyylllty.sertraline.Sertraline
import io.github.zzzyyylllty.sertraline.data.SertralineItem
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.function.kether.evalKetherValue
import io.github.zzzyyylllty.sertraline.function.sertralize.AnySerializer
import io.github.zzzyyylllty.sertraline.function.sertralize.PatternTypeAdapter
import io.github.zzzyyylllty.sertraline.function.sertralize.TimeZoneTypeAdapter
import io.github.zzzyyylllty.sertraline.logger.warningS
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.block.banner.Pattern
import org.bukkit.block.banner.PatternType
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.potion.PotionData
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType
import taboolib.common.platform.function.console
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.XItemFlag
import taboolib.library.xseries.XMaterial
import taboolib.library.xseries.XPatternType
import taboolib.module.lang.asLangText
import taboolib.module.nms.getItemTag
import taboolib.platform.util.buildItem
import taboolib.platform.util.modifyMeta
import java.util.Locale
import kotlin.math.min
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
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
}

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
    .create()

fun SertralineItem.buildItem(player: Player?): ItemStack {

    devLog("prepare encode item: $this")

    var datajson = gsonBuilder.toJson(this)

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

    val data = jsonUtils.decodeFromString<LinkedHashMap<String, @Serializable(with = AnySerializer::class) Any?>>(datajson)

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

    var material = buildItem(XMaterial.valueOf(mc.material ?: "STONE")) {
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
                        org.bukkit.Color.fromRGB(split[0].toInt(), split[1].toInt(), split[2].toInt())
                    } else {
                        org.bukkit.Color.fromRGB(it.toInt())
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
                        val enchant = taboolib.library.xseries.XEnchantment.of(s.split(",")[0]).get().get() ?: run {
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
        val key = it.key.replace("__",".")
        tag.put(key, it.value)
        devLog("writing nbt ${it.key} = ${it.value}")
    }
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
