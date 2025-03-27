package io.github.zzzyyylllty.sertraline

import io.github.zzzyyylllty.sertraline.data.DepazItems
import io.github.zzzyyylllty.sertraline.function.load.loadItemFiles
import io.github.zzzyyylllty.sertraline.function.load.loadTemplateFile
import io.github.zzzyyylllty.sertraline.function.load.loadTemplateFiles
import io.github.zzzyyylllty.sertraline.function.load.reloadSertraline
import taboolib.common.io.newFile
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.info
import taboolib.common.platform.function.warning
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import taboolib.module.database.getHost
import java.time.format.DateTimeFormatter
import io.github.zzzyyylllty.sertraline.logger.*
import org.bukkit.command.CommandSender
import org.bukkit.configuration.ConfigurationSection
import taboolib.common.platform.function.releaseResourceFile
import java.io.File
import java.util.*

object Sertraline : Plugin() {

    val plugin by lazy { this }
    val host by lazy { config.getHost("database") }
    val dataSource by lazy { host.createDataSource() }
    var dataFolder = nativeDataFolder()
    var templateMap = LinkedHashMap<String, ConfigurationSection>()
    var itemMap = LinkedHashMap<String, DepazItems>()
    var devMode = true
    val console by lazy { console() }
    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    val config by lazy {
        if (!File(getDataFolder(), "config.yml").exists()) {
            warningL("CONFIG_REGEN")
            releaseResourceFile("config.yml")
        }
        Configuration.loadFromFile(newFile(getDataFolder(), "config.yml", create = false), Type.YAML)
    }

    override fun onEnable() {
        warning("Sertraline now starting.")
        reloadSertraline()
    }

    override fun onDisable() {
        info("Successfully running ExamplePlugin!")
    }

    fun reloadCustomConfig() {
        infoL("INTERNAL_INFO_CREATING_CONFIG")
        try {
            infoL("INTERNAL_INFO_CREATED_CONFIG")
        } catch (e: Exception) {
            severeL("INTERNAL_SEVERE_CREATE_CONFIG_ERROR")
            e.printStackTrace()
        }
        plugin.config.reload()
        itemMap = linkedMapOf()
        loadItemFiles()
        loadTemplateFiles()
    }


    fun createCustomConfig() {
        infoL("INTERNAL_INFO_CREATING_CONFIG")
        try {
            Configuration.loadFromFile(newFile(getDataFolder(), "placeholders.yml", create = true), Type.YAML)
            Configuration.loadFromFile(newFile(getDataFolder(), "config.yml", create = true), Type.YAML)
            infoL("INTERNAL_INFO_CREATED_CONFIG")
        } catch (e: Exception) {
            severeL("INTERNAL_SEVERE_CREATE_CONFIG_ERROR")
            e.printStackTrace()
        }
    }
}