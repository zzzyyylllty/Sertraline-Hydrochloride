package io.github.zzzyyylllty.sertraline

import io.github.zzzyyylllty.sertraline.config.loadItemFiles
import io.github.zzzyyylllty.sertraline.config.loadLoreFormatFiles
import io.github.zzzyyylllty.sertraline.config.loadMappingFiles
import io.github.zzzyyylllty.sertraline.data.LoreFormat
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.listener.sertraline.builder.ItemProcessorManager
import io.github.zzzyyylllty.sertraline.listener.sertraline.builder.registerNativeAdapter
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.module.configuration.Configuration
import taboolib.module.database.getHost
import java.time.format.DateTimeFormatter
import io.github.zzzyyylllty.sertraline.logger.*
import io.github.zzzyyylllty.sertraline.reflect.ReflectTargets
import io.github.zzzyyylllty.sertraline.reflect.`clazz$BuiltInRegistries`
import io.github.zzzyyylllty.sertraline.reflect.`clazz$CraftItemStack`
import io.github.zzzyyylllty.sertraline.reflect.`clazz$DataComponentHolder`
import io.github.zzzyyylllty.sertraline.reflect.`clazz$DataComponentType`
import io.github.zzzyyylllty.sertraline.reflect.`clazz$HolderLookup$Provider`
import io.github.zzzyyylllty.sertraline.reflect.`clazz$ItemStack`
import io.github.zzzyyylllty.sertraline.reflect.`clazz$MinecraftServer`
import io.github.zzzyyylllty.sertraline.reflect.`clazz$NbtOps`
import io.github.zzzyyylllty.sertraline.reflect.`clazz$Registry`
import io.github.zzzyyylllty.sertraline.reflect.`clazz$RegistryAccess$Frozen`
import io.github.zzzyyylllty.sertraline.reflect.`clazz$RegistryOps`
import io.github.zzzyyylllty.sertraline.reflect.`clazz$ResourceLocation`
import io.github.zzzyyylllty.sertraline.reflect.`clazz$Tag`
import io.github.zzzyyylllty.sertraline.reflect.`field$BuiltInRegistries$DATA_COMPONENT_TYPE`
import io.github.zzzyyylllty.sertraline.reflect.`field$CraftItemStack$handle`
import io.github.zzzyyylllty.sertraline.reflect.`field$NbtOps$INSTANCE`
import io.github.zzzyyylllty.sertraline.reflect.`instance$BuiltInRegistries$DATA_COMPONENT_TYPE`
import io.github.zzzyyylllty.sertraline.reflect.`instance$DynamicOps$JAVA`
import io.github.zzzyyylllty.sertraline.reflect.`instance$DynamicOps$JSON`
import io.github.zzzyyylllty.sertraline.reflect.`instance$DynamicOps$NBT`
import io.github.zzzyyylllty.sertraline.reflect.`instance$MinecraftServer$SERVER`
import io.github.zzzyyylllty.sertraline.reflect.`instance$MinecraftServer$registryAccess`
import io.github.zzzyyylllty.sertraline.reflect.`instance$NbtOps$INSTANCE`
import io.github.zzzyyylllty.sertraline.reflect.`method$DataComponentHolder$getDataComponentType`
import io.github.zzzyyylllty.sertraline.reflect.`method$DataComponentType$codec`
import io.github.zzzyyylllty.sertraline.reflect.`method$ItemStack$removeComponent`
import io.github.zzzyyylllty.sertraline.reflect.`method$ItemStack$setComponent`
import io.github.zzzyyylllty.sertraline.reflect.`method$MinecraftServer$getServer`
import io.github.zzzyyylllty.sertraline.reflect.`method$MinecraftServer$registryAccess`
import io.github.zzzyyylllty.sertraline.reflect.`method$Registry$getValue`
import io.github.zzzyyylllty.sertraline.reflect.`method$RegistryOps$create`
import io.github.zzzyyylllty.sertraline.reflect.`method$ResourceLocation$fromNamespaceAndPath`
import io.github.zzzyyylllty.sertraline.reflect.`method$ResourceLocation$tryParse`
import org.bukkit.command.CommandSender
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.module.configuration.Config
import taboolib.module.lang.Language
import taboolib.module.lang.event.PlayerSelectLocaleEvent
import taboolib.module.lang.event.SystemSelectLocaleEvent
import top.maplex.arim.tools.conditionevaluator.ConditionEvaluator
import top.maplex.arim.tools.fixedcalculator.FixedCalculator
import top.maplex.arim.tools.variablecalculator.VariableCalculator
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
