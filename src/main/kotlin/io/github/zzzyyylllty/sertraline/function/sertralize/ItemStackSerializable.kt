package io.github.zzzyyylllty.sertraline.function.sertralize

import com.beust.klaxon.Converter
import com.beust.klaxon.JsonValue
import org.bukkit.Material
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack

@Target(AnnotationTarget.FIELD)
annotation class KlaxonItemStack

class ItemStackHolder(val item: ItemStack)

val myConverter = object: Converter {
    override fun canConvert(cls: Class<*>)
            = cls == ItemStack::class.java

    override fun toJson(value: Any): String {

        val config = YamlConfiguration()
        val item = ItemStack(Material.STICK)
        config.set("item", item)

        return """{"flag" : "${config.saveToString()}"""
    }

    override fun fromJson(jv: JsonValue): ItemStack {

        val config = YamlConfiguration()
        try {
            config.loadFromString(jv.string ?: throw NullPointerException())
            return config.getItemStack("item", null) ?: throw NullPointerException()
        } catch (e: InvalidConfigurationException) {
            throw e
        }
    }

}