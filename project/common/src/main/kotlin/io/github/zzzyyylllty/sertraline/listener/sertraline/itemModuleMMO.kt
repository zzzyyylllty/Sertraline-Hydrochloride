package io.github.zzzyyylllty.sertraline.listener.sertraline

import io.github.zzzyyylllty.sertraline.Sertraline.consoleSender
import io.github.zzzyyylllty.sertraline.Sertraline.mappings
import io.github.zzzyyylllty.sertraline.config.ConfigUtil
import io.github.zzzyyylllty.sertraline.event.ItemLoadEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.BukkitListener.BukkitListener

@SubscribeEvent(priority = EventPriority.NORMAL)
fun itemModuleMMO(e: ItemLoadEvent) {

    val prefix = "mmo"
    val c = ConfigUtil()

    val features = listOf(
        "$prefix:uuid",
        "$prefix:attack-damage",
        "$prefix:attack-speed",
        "$prefix:critical-strike-chance",
        "$prefix:critical-strike-power",
        "$prefix:skill-critical-strike-chance",
        "$prefix:skill-critical-strike-power",
        "$prefix:range",
        "$prefix:mana-cost",
        "$prefix:stamina-cost",
        "$prefix:arrow-velocity",
        "$prefix:blunt-power",
        "$prefix:blunt-rating",
        "$prefix:two-handed",
        "$prefix:handworn",
        "$prefix:knockback",
        "$prefix:recoil",
        "$prefix:note-weight",
        "$prefix:lifesteal",
        "$prefix:spell-vampirism",
        "$prefix:pve-damage",
        "$prefix:pvp-damage",
        "$prefix:magic-damage",
        "$prefix:weapon-damage",
        "$prefix:undead-damage",
        "$prefix:skill-damage",
        "$prefix:physical-damage",
        "$prefix:projectile-damage",
        "$prefix:faction-damage-undead",
        "$prefix:ability-format",
        "$prefix:ability-modifier",
        "$prefix:ability-splitter",
        "$prefix:block-power",
        "$prefix:block-rating",
        "$prefix:block-cooldown-reduction",
        "$prefix:dodge-rating",
        "$prefix:dodge-cooldown-reduction",
        "$prefix:parry-rating",
        "$prefix:parry-cooldown-reduction",
        "$prefix:armor",
        "$prefix:armor-toughness",
        "$prefix:knockback-resistance",
        "$prefix:movement-speed",
        "$prefix:defense",
        "$prefix:damage-reduction",
        "$prefix:fall-damage-reduction",
        "$prefix:fire-damage-reduction",
        "$prefix:magic-damage-reduction",
        "$prefix:projectile-damage-reduction",
        "$prefix:physical-damage-reduction",
        "$prefix:pve-damage-reduction",
        "$prefix:pvp-damage-reduction",
        "$prefix:max-health",
        "$prefix:health-regeneration",
        "$prefix:max-mana",
        "$prefix:mana-regeneration",
        "$prefix:max-stamina",
        "$prefix:stamina-regeneration"
    )

    e.itemData.putAll(c.getFeatures(e.itemKey, e.arguments, features, e.itemData))
}
