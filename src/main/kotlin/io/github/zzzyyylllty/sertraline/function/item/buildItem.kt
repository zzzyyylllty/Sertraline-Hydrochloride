package io.github.zzzyyylllty.sertraline.function.item

import io.github.zzzyyylllty.sertraline.data.SertralineItem
import io.github.zzzyyylllty.sertraline.logger.warningS
import org.bukkit.DyeColor
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
import taboolib.module.lang.asLangText
import taboolib.module.nms.getItemTag
import taboolib.platform.util.buildItem
import taboolib.platform.util.modifyMeta
import kotlin.math.min

fun SertralineItem.buildItem(player: Player?): ItemStack {

    val mc = minecraftItem
    val smeta = sertralineMeta

    var material = buildItem(XMaterial.valueOf(mc.material ?: "STONE")) {
        mc.displayName?.let { name = it }
        mc.model?.let { customModelData = it }
        mc.lore?.let {
            for (s in it) {
                lore.add(s)
            }
        }
        mc.extra.forEach {
            val value = it.value
            when (it.key) {
                "damage","dam" -> damage = value.toString().toInt()
                "flag","flags" -> (value as List<String>).let {
                    for (s in it) {
                        flags.add(ItemFlag.valueOf(s))
                    }
                }
                "pattern", "patterns" -> (value as List<String>).let {
                    for (s in it) {
                        val dyeColor = DyeColor.valueOf(s.split(",")[0] ?: throw NullPointerException("DyeColor must be a value"))
                        val pattern = PatternType.valueOf(s.split(",")[1] ?: throw NullPointerException("Pattern must be a value"))
                        patterns.add(Pattern(dyeColor, pattern))
                    }
                }
                "enchant","enchants" -> ((value as ConfigurationSection).getValues(false) as Map<String , Int>).let {
                        it.forEach {
                            Enchantment.getByName(it.key)?.let { key -> enchants.put(key, it.value) } ?: warningS(console().asLangText("ItemEnchantSkipped", "${smeta.key}"))
                        }
                }
                "effect", "potion", "potions" -> (value as List<String>).let {
                    for (s in it) {
                        val potion = PotionEffectType.getByName(s.split(",")[0]) ?: run {
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
    mc.nbt.forEach { tag.put(it.key, it.value) }
    tag.saveTo(material)

    return material
}
