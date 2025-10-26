package io.github.zzzyyylllty.sertraline

import io.github.zzzyyylllty.sertraline.Sertraline.reloadCustomConfig
import io.github.zzzyyylllty.sertraline.data.Key
import io.github.zzzyyylllty.sertraline.data.SertralineItem
import io.github.zzzyyylllty.sertraline.data.SertralinePack
import io.github.zzzyyylllty.sertraline.load.loadItemFiles
import io.github.zzzyyylllty.sertraline.load.loadPackFiles
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
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import taboolib.common.LifeCycle.ENABLE
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.releaseResourceFile
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigFile
import taboolib.module.lang.Language
import taboolib.module.lang.event.PlayerSelectLocaleEvent
import taboolib.module.lang.event.SystemSelectLocaleEvent
import top.maplex.arim.tools.conditionevaluator.ConditionEvaluator
import top.maplex.arim.tools.fixedcalculator.FixedCalculator
import top.maplex.arim.tools.variablecalculator.VariableCalculator
import java.io.File
import java.util.*

@Awake(ENABLE)
fun onEnable(){
    infoL("Enable")
    Language.enableSimpleComponent = true
    reloadCustomConfig()
}

object Sertraline : Plugin() {

    val plugin by lazy { this }
    var dataFolder = nativeDataFolder()
    val console by lazy { console() }
    val consoleSender by lazy { console.castSafely<CommandSender>()!! }
    val host by lazy { config.getHost("database") }
    val dataSource by lazy { host.createDataSource() }
    var packMap = LinkedHashMap<String, SertralinePack>()
    var itemMap = LinkedHashMap<Key, SertralineItem>()
    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    var devMode = true

    // Arim Start
    val evaluator by lazy { ConditionEvaluator() }
    val fixedCalculator by lazy { FixedCalculator() }
    val variableCalculator by lazy { VariableCalculator() }

    @Config("config.yml")
    lateinit var config: Configuration

    override fun onEnable() {
    }

    override fun onDisable() {
        infoL("Disable")
    }
    /*
    fun compat() {
        if (Bukkit.getPluginManager().getPlugin("Chemdah") != null) {
            connectChemdah()
        }
    }*/

    fun reloadCustomConfig() {
        itemMap.clear()
        packMap.clear()
        loadPackFiles()
        loadItemFiles()
        plugin.config.reload()
        // devMode = config.getBoolean("debug",false)
    }
//
//
//    fun createCustomConfig() {
//        infoL("INTERNAL_INFO_CREATING_CONFIG")
//        try {
//            Configuration.loadFromFile(newFile(getDataFolder(), "placeholders.yml", create = true), Type.YAML)
//            Configuration.loadFromFile(newFile(getDataFolder(), "config.yml", create = true), Type.YAML)
//            infoL("INTERNAL_INFO_CREATED_CONFIG")
//        } catch (e: Exception) {
//            severeL("INTERNAL_SEVERE_CREATE_CONFIG_ERROR")
//            e.printStackTrace()
//        }
//    }


    @SubscribeEvent
    fun lang(event: PlayerSelectLocaleEvent) {
        event.locale = config.getString("lang", "zh_CN")!!
    }

    @SubscribeEvent
    fun lang(event: SystemSelectLocaleEvent) {
        event.locale = config.getString("lang", "zh_CN")!!
    }
}
