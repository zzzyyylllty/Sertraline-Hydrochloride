package io.github.zzzyyylllty.sertraline

import ink.ptms.adyeshach.taboolib.common.env.RuntimeDependencies
import ink.ptms.adyeshach.taboolib.common.env.RuntimeDependency
import io.github.zzzyyylllty.sertraline.data.DepazItems
import io.github.zzzyyylllty.sertraline.logger.fineS
import io.github.zzzyyylllty.sertraline.function.load.loadItemFiles
import io.github.zzzyyylllty.sertraline.function.load.loadTemplateFile
import io.github.zzzyyylllty.sertraline.function.load.loadTemplateFiles
import io.github.zzzyyylllty.sertraline.function.load.reloadSertraline
import io.github.zzzyyylllty.sertraline.function.sertralize.AnySerializer
import taboolib.common.io.newFile
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.common.platform.function.getDataFolder
import io.github.zzzyyylllty.sertraline.logger.infoS
import io.github.zzzyyylllty.sertraline.logger.warningS
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import taboolib.module.database.getHost
import java.time.format.DateTimeFormatter
import io.github.zzzyyylllty.sertraline.logger.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.serializersModule
import kotlinx.serialization.modules.SerializersModule
import net.luckperms.api.query.QueryOptions.contextual
import org.bukkit.command.CommandSender
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import taboolib.common.platform.function.releaseResourceFile
import top.maplex.arim.tools.conditionevaluator.ConditionEvaluator
import top.maplex.arim.tools.fixedcalculator.FixedCalculator
import top.maplex.arim.tools.variablecalculator.VariableCalculator
import java.io.File
import java.util.*

@RuntimeDependencies(
    RuntimeDependency(
        "!org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.6.0",
        test = "!kotlinx.serialization.Serializer",
        relocate = ["!kotlin.", "!kotlin1822.", "!kotlinx.serialization.", "!kotlinx.serialization133."],
        transitive = false
    ),
    RuntimeDependency(
        "!org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.6.0",
        test = "!kotlinx.serialization.json.Json",
        relocate = ["!kotlin.", "!kotlin1822.", "!kotlinx.serialization.", "!kotlinx.serialization133."],
        transitive = false
    )
)
class RuntimeEnv

object Sertraline : Plugin() {

    val plugin by lazy { this }
    val host by lazy { config.getHost("database") }
    val dataSource by lazy { host.createDataSource() }
    var dataFolder = nativeDataFolder()
    var templateMap = LinkedHashMap<String, ConfigurationSection>()
    var itemMap = LinkedHashMap<String, DepazItems>()
    var devMode = true
    val console by lazy { console() }
    val consoleSender by lazy { console.castSafely<CommandSender>()!! }
    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    // Arim Start
    val evaluator by lazy { ConditionEvaluator() }
    val fixedCalculator by lazy { FixedCalculator() }
    val variableCalculator by lazy { VariableCalculator() }



    val config by lazy {
        if (!File(getDataFolder(), "config.yml").exists()) {
            warningL("CONFIG_REGEN")
            releaseResourceFile("config.yml")
        }
        Configuration.loadFromFile(newFile(getDataFolder(), "config.yml", create = false), Type.YAML)
    }

    override fun onEnable() {
        infoL("INTERNAL_ONENABLE")
        reloadSertraline()
    }

    override fun onDisable() {
        infoL("INTERNAL_ONDISABLE")
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
        loadTemplateFiles()
        loadItemFiles()
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