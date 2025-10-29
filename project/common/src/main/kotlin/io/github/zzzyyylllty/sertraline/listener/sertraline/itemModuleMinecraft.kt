package io.github.zzzyyylllty.sertraline.listener.sertraline

import io.github.zzzyyylllty.sertraline.Sertraline.consoleSender
import io.github.zzzyyylllty.sertraline.Sertraline.mappings
import io.github.zzzyyylllty.sertraline.config.ConfigUtil
import io.github.zzzyyylllty.sertraline.event.ItemLoadEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.BukkitListener.BukkitListener

@SubscribeEvent(priority = EventPriority.NORMAL)
fun itemModuleMinecraft(e: ItemLoadEvent) {

    val prefix = "minecraft"
    val c = ConfigUtil()

    val features = listOf(
        "$prefix:material",
        "$prefix:attribute_modifiers",
        "$prefix:banner_patterns",
        "$prefix:base_color",
        "$prefix:bees",
        "$prefix:block_entity_data",
        "$prefix:blocks_attacks",
        "$prefix:break_sound",
        "$prefix:bucket_entity_data",
        "$prefix:bundle_contents",
        "$prefix:container",
        "$prefix:container_loot",
        "$prefix:can_break",
        "$prefix:can_place_on",
        "$prefix:charged_projectiles",
        "$prefix:consumable",
        "$prefix:custom_data",
        "$prefix:custom_model_data",
        "$prefix:custom_name",
        "$prefix:damage",
        "$prefix:damage_resistant",
        "$prefix:damage_type",
        "$prefix:debug_stick_state",
        "$prefix:death_protection",
        "$prefix:dyed_color",
        "$prefix:enchantable",
        "$prefix:enchantment_glint_override",
        "$prefix:enchantments",
        "$prefix:entity_data",
        "$prefix:equippable",
        "$prefix:firework_explosion",
        "$prefix:fireworks",
        "$prefix:food",
        "$prefix:glider",
        "$prefix:instrument",
        "$prefix:intangible_projectile",
        "$prefix:item_model",
        "$prefix:item_name",
        "$prefix:jukebox_playable",
        "$prefix:kinetic_weapon",
        "$prefix:lock",
        "$prefix:lodestone_tracker",
        "$prefix:lore",
        "$prefix:map_color",
        "$prefix:map_decorations",
        "$prefix:map_id",
        "$prefix:max_damage",
        "$prefix:max_stack_size",
        "$prefix:minimum_attack_charge",
        "$prefix:note_block_sound",
        "$prefix:ominous_bottle_amplifier",
        "$prefix:piercing_weapon",
        "$prefix:pot_decorations",
        "$prefix:potion_contents",
        "$prefix:potion_duration_scale",
        "$prefix:profile",
        "$prefix:provides_banner_patterns",
        "$prefix:provides_trim_material",
        "$prefix:rarity",
        "$prefix:recipes",
        "$prefix:repairable",
        "$prefix:repair_cost",
        "$prefix:stored_enchantments",
        "$prefix:suspicious_stew_effects",
        "$prefix:swing_animation",
        "$prefix:tool",
        "$prefix:tooltip_display",
        "$prefix:tooltip_style",
        "$prefix:trim",
        "$prefix:unbreakable",
        "$prefix:use_cooldown",
        "$prefix:use_effects",
        "$prefix:use_remainder",
        "$prefix:weapon",
        "$prefix:writable_book_content",
        "$prefix:written_book_content",

        )

    e.itemData.putAll(c.getFeatures(e.arguments, features, e.itemData))
}
