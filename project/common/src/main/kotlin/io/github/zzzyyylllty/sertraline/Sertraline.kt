package io.github.zzzyyylllty.sertraline

import io.github.zzzyyylllty.sertraline.config.ConfigUtil
import io.github.zzzyyylllty.sertraline.config.loadItemFiles
import io.github.zzzyyylllty.sertraline.config.loadLoreFormatFiles
import io.github.zzzyyylllty.sertraline.config.loadMappingFiles
import io.github.zzzyyylllty.sertraline.data.LoreFormat
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.listener.sertraline.builder.ItemProcessorManager
import io.github.zzzyyylllty.sertraline.listener.sertraline.builder.registerNativeAdapter
import io.github.zzzyyylllty.sertraline.logger.*
import io.github.zzzyyylllty.sertraline.reflect.*
import org.bukkit.command.CommandSender
import taboolib.common.platform.Plugin
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.console
import taboolib.common.platform.function.submit
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.module.database.getHost
import taboolib.module.lang.Language
import taboolib.module.lang.event.PlayerSelectLocaleEvent
import taboolib.module.lang.event.SystemSelectLocaleEvent
import top.maplex.arim.tools.conditionevaluator.ConditionEvaluator
import top.maplex.arim.tools.fixedcalculator.FixedCalculator
import top.maplex.arim.tools.variablecalculator.VariableCalculator
import java.time.format.DateTimeFormatter
import java.util.*


object Sertraline : Plugin() {

    val plugin by lazy { this }
    var dataFolder = nativeDataFolder()
    val console by lazy { console() }
    val consoleSender by lazy { console.castSafely<CommandSender>()!! }
    val host by lazy { config.getHost("database") }
    val dataSource by lazy { host.createDataSource() }
    var itemMap = LinkedHashMap<String, ModernSItem>()
    var mappings = LinkedHashMap<String, List<String>?>()
    var loreFormats = LinkedHashMap<String, LoreFormat>()
    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val itemManager = ItemProcessorManager()
    var devMode = true
    val reflects = ReflectTargets()
    val configUtil = ConfigUtil()

    // Arim Start
    val evaluator by lazy { ConditionEvaluator() }
    val fixedCalculator by lazy { FixedCalculator() }
    val variableCalculator by lazy { VariableCalculator() }

    @Config("config.yml")
    lateinit var config: Configuration


    override fun onEnable() {

        infoL("Enable")
        devLog("clazz\$ResourceLocation: ${`clazz$ResourceLocation`}")
        devLog("clazz\$Registry: ${`clazz$Registry`}")
        devLog("clazz\$BuiltInRegistries: ${`clazz$BuiltInRegistries`}")
        devLog("clazz\$DataComponentType: ${`clazz$DataComponentType`}")
        devLog("clazz\$DataComponentHolder: ${`clazz$DataComponentHolder`}")
        devLog("clazz\$MinecraftServer: ${`clazz$MinecraftServer`}")
        devLog("field\$BuiltInRegistries\$DATA_COMPONENT_TYPE: ${`field$BuiltInRegistries$DATA_COMPONENT_TYPE`}")
        devLog("instance\$BuiltInRegistries\$DATA_COMPONENT_TYPE: ${`instance$BuiltInRegistries$DATA_COMPONENT_TYPE`}")
        devLog("method\$ResourceLocation\$fromNamespaceAndPath: ${`method$ResourceLocation$fromNamespaceAndPath`}")
        devLog("method\$Registry\$getValue: ${`method$Registry$getValue`}")
        devLog("clazz\$RegistryOps: ${`clazz$RegistryOps`}")
        devLog("clazz\$HolderLookup\$Provider: ${`clazz$HolderLookup$Provider`}")
        devLog("method\$RegistryOps\$create: ${`method$RegistryOps$create`}")
        devLog("method\$MinecraftServer\$getServer: ${`method$MinecraftServer$getServer`}")
        devLog("instance\$MinecraftServer\$SERVER: ${`instance$MinecraftServer$SERVER`}")
        devLog("clazz\$RegistryAccess\$Frozen: ${`clazz$RegistryAccess$Frozen`}")
        devLog("method\$MinecraftServer\$registryAccess: ${`method$MinecraftServer$registryAccess`}")
        devLog("instance\$MinecraftServer\$registryAccess: ${`instance$MinecraftServer$registryAccess`}")
        devLog("method\$DataComponentType\$codec: ${`method$DataComponentType$codec`}")
        devLog("method\$DataComponentHolder\$getDataComponentType: ${`method$DataComponentHolder$getDataComponentType`}")
        devLog("clazz\$ItemStack: ${`clazz$ItemStack`}")
        devLog("method\$ItemStack\$setComponent: ${`method$ItemStack$setComponent`}")
        devLog("clazz\$CraftItemStack: ${`clazz$CraftItemStack`}")
        devLog("field\$CraftItemStack\$handle: ${`field$CraftItemStack$handle`}")
        devLog("clazz\$NbtOps: ${`clazz$NbtOps`}")
        devLog("field\$NbtOps\$INSTANCE: ${`field$NbtOps$INSTANCE`}")
        devLog("instance\$NbtOps\$INSTANCE: ${`instance$NbtOps$INSTANCE`}")
        devLog("instance\$DynamicOps\$NBT: ${`instance$DynamicOps$NBT`}")
        devLog("instance\$DynamicOps\$JAVA: ${`instance$DynamicOps$JAVA`}")
        devLog("instance\$DynamicOps\$JSON: ${`instance$DynamicOps$JSON`}")
        devLog("clazz\$Tag: ${`clazz$Tag`}")
        devLog("method\$ResourceLocation\$tryParse: ${`method$ResourceLocation$tryParse`}")
        devLog("method\$ItemStack\$removeComponent: ${`method$ItemStack$removeComponent`}")
        Language.enableSimpleComponent = true
        reloadCustomConfig()

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

    fun reloadCustomConfig(async: Boolean = false) {
        submit(async) {
            itemMap.clear()
            mappings.clear()
            loadMappingFiles()
            loadItemFiles()
            loadLoreFormatFiles()
            plugin.config.reload()
            itemManager.unregisterAllProcessor()
            registerNativeAdapter()
        // devMode = config.getBoolean("debug",false)
        }
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
