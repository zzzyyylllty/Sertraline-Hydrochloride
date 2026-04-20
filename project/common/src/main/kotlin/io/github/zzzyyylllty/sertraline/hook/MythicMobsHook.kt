package io.github.zzzyyylllty.sertraline.hook

import ink.ptms.um.Mythic
import ink.ptms.um.event.MobDeathEvent
import ink.ptms.um.event.MobSpawnEvent
import io.github.zzzyyylllty.sertraline.Sertraline
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.event.SertralineCustomScriptDataLoadEvent
import io.github.zzzyyylllty.sertraline.function.data.getSertralineId
import io.github.zzzyyylllty.sertraline.logger.infoS
import io.github.zzzyyylllty.sertraline.logger.infoSSync
import io.github.zzzyyylllty.sertraline.util.DependencyHelper
import io.github.zzzyyylllty.sertraline.util.ItemTagUtil.parseMapNBT
import io.lumine.mythic.lib.comp.mythicmobs.MythicMobsHook
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import taboolib.common.LifeCycle
import taboolib.common.platform.Ghost
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.kether.inferType
import taboolib.module.nms.getItemTag
import java.util.Locale
import kotlin.random.Random

@Ghost
@SubscribeEvent
fun mmDataHook(e: SertralineCustomScriptDataLoadEvent) {
    if (Mythic.isLoaded()) {
        val api = Mythic.API
        infoSSync("Hooking onto mythicmobs")
        e.defaultData["UMAPI"] = api
    }
}

/**
 * Ref https://github.com/YsGqHY/Sertraline
 * */
object MythicHook {

    @Ghost
    @SubscribeEvent
    fun onMobSpawn(e: MobSpawnEvent) {
        val mob = e.mob ?: return
        val config = mob.config
        val equipmentSection = config.getConfigurationSection("Sertraline.equipments") ?: config.getConfigurationSection("Sertraline.equipments") ?: return
        val entity = mob.entity as? LivingEntity ?: return
        submit(delay = 5L) {
            applyEquipments(equipmentSection, entity)
        }
    }

    @Ghost
    @SubscribeEvent
    fun onMobDeath(e: MobDeathEvent) {
        val mob = e.mob
        val config = mob.config
        val dropLines = config.getStringList("Sertraline.drops")
        devLog("[Sertraline_MM_DEATH] mob=${mob.id} killer=${(e.killer as? Player)?.name} dropLines=$dropLines")
        if (dropLines.isEmpty()) {
            return
        }

        val killer = e.killer as? Player
        val context = linkedMapOf<String, Any?>()
        if (killer != null) {
            context["player"] = killer
            context["sender"] = killer
            context["killer"] = killer
            context["event"] = e
        }

        val drops = e.drop
        dropLines.forEach { line ->
            val parsed = parseDrop(line)
            if (parsed == null) {
                devLog("[Sertraline][DEBUG][MM_DROP] SKIP parse failed: '$line'")
                return@forEach
            }
            val rolled = parsed.roll()
            if (!rolled) {
                devLog("[Sertraline][DEBUG][MM_DROP] SKIP roll failed: item=${parsed.itemId} chance=${parsed.chance}")
                return@forEach
            }
            val item = Sertraline.api().buildDataItem(parsed.itemId, killer, vars = context)
            if (item == null) {
                devLog("[Sertraline][DEBUG][MM_DROP] SKIP item not found: '${parsed.itemId}' (generateItemStack returned null)")
                return@forEach
            }
            item.amount = parsed.nextAmount()
            drops += item
            devLog("[Sertraline][DEBUG][MM_DROP] OK item=${parsed.itemId} amount=${item.amount} type=${item.type}")
        }
    }

    private fun applyEquipments(section: ConfigurationSection, entity: LivingEntity) {
        section.getValues(false).forEach { (slot, raw) ->
            val value = raw?.toString()?.trim()?.takeIf { it.isNotEmpty() } ?: return@forEach
            val item = if (value.equals("air", true)) {
                ItemStack(Material.AIR)
            } else {
                Sertraline.api().buildDataItem(value, entity as? Player?)
            }
            devLog("[Sertraline][DEBUG][MM_EQUIP] entity=${entity.type} slot=$slot itemId=$value resolved=${item != null} type=${item?.type}")
            setEquipment(entity, slot, item ?: ItemStack(Material.AIR))
        }
    }

    private fun setEquipment(entity: LivingEntity, rawSlot: String, item: ItemStack) {
        val equipment = entity.equipment ?: return
        when (rawSlot.trim().lowercase(Locale.ENGLISH)) {
            "mainhand", "main_hand", "hand" -> {
                equipment.setItemInMainHand(item)
                equipment.itemInMainHandDropChance = 0f
            }
            "offhand", "off_hand" -> {
                equipment.setItemInOffHand(item)
                equipment.itemInOffHandDropChance = 0f
            }
            "head", "helmet" -> {
                equipment.helmet = item
                equipment.helmetDropChance = 0f
            }
            "chest", "chestplate" -> {
                equipment.chestplate = item
                equipment.chestplateDropChance = 0f
            }
            "legs", "leggings" -> {
                equipment.leggings = item
                equipment.leggingsDropChance = 0f
            }
            "feet", "boots" -> {
                equipment.boots = item
                equipment.bootsDropChance = 0f
            }
        }
    }

    private data class ParsedDrop(
        val itemId: String,
        val minAmount: Int,
        val maxAmount: Int,
        val chance: Double
    ) {

        fun roll(): Boolean {
            return chance >= 1.0 || Random.nextDouble() <= chance
        }

        fun nextAmount(): Int {
            if (minAmount >= maxAmount) {
                return minAmount
            }
            return Random.nextInt(minAmount, maxAmount + 1)
        }
    }

    private fun parseDrop(raw: String): ParsedDrop? {
        val tokens = raw.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        if (tokens.isEmpty()) {
            return null
        }
        val itemId = tokens[0]
        val (min, max) = parseAmount(tokens.getOrNull(1))
        val chance = tokens.getOrNull(2)?.toDoubleOrNull()?.coerceIn(0.0, 1.0) ?: 1.0
        return ParsedDrop(
            itemId = itemId,
            minAmount = min,
            maxAmount = max,
            chance = chance
        )
    }

    private fun parseAmount(raw: String?): Pair<Int, Int> {
        val source = raw?.trim().orEmpty()
        if (source.isEmpty()) {
            return 1 to 1
        }
        val split = source.split('-', limit = 2)
        if (split.size == 1) {
            val value = split[0].toIntOrNull()?.coerceAtLeast(1) ?: 1
            return value to value
        }
        val first = split[0].toIntOrNull()?.coerceAtLeast(1) ?: 1
        val second = split[1].toIntOrNull()?.coerceAtLeast(1) ?: first
        return if (first <= second) {
            first to second
        } else {
            second to first
        }
    }
}