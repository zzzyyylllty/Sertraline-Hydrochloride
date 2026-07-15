package io.github.zzzyyylllty.sertraline

import io.github.zzzyyylllty.sertraline.api.SertralineAPI
import io.github.zzzyyylllty.sertraline.api.SertralineAPIImpl
import io.github.zzzyyylllty.sertraline.config.ConfigUtil
import io.github.zzzyyylllty.sertraline.config.TemplateManager
import io.github.zzzyyylllty.sertraline.config.loadCraftingStationFiles
import io.github.zzzyyylllty.sertraline.gui.CraftingStationManager
import io.github.zzzyyylllty.sertraline.config.loadItemFiles
import io.github.zzzyyylllty.sertraline.config.loadLoreFormatFiles
import io.github.zzzyyylllty.sertraline.config.loadMappingFiles
import io.github.zzzyyylllty.sertraline.config.loadTierFiles
import io.github.zzzyyylllty.sertraline.config.loadTypeFiles
import io.github.zzzyyylllty.sertraline.config.loadLevelFiles
import io.github.zzzyyylllty.sertraline.config.loadRecipeFiles
import io.github.zzzyyylllty.sertraline.function.update.initRevisionAutoTracker
import io.github.zzzyyylllty.sertraline.data.CraftingStation
import io.github.zzzyyylllty.sertraline.data.LoreFormat
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.data.Tier
import io.github.zzzyyylllty.sertraline.data.Type
import io.github.zzzyyylllty.sertraline.data.Level
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.debugMode.devLogSync
import io.github.zzzyyylllty.sertraline.attribute.AttributeManager
import io.github.zzzyyylllty.sertraline.attribute.ChotenAttributeProvider
import io.github.zzzyyylllty.sertraline.attribute.MythicLibAttributeProvider
import io.github.zzzyyylllty.sertraline.event.SertralineReloadEvent
import io.github.zzzyyylllty.sertraline.listener.sertraline.builder.ItemProcessorManager
import io.github.zzzyyylllty.sertraline.listener.sertraline.builder.registerNativeAdapter
import io.github.zzzyyylllty.sertraline.item.process.tag.TagProcessorManager
import io.github.zzzyyylllty.sertraline.item.process.tag.registerNativeTagAdapter
import io.github.zzzyyylllty.sertraline.logger.ReloadCollector
import io.github.zzzyyylllty.sertraline.logger.infoL
import io.github.zzzyyylllty.sertraline.logger.infoLSync
import io.github.zzzyyylllty.sertraline.logger.infoSSync
import io.github.zzzyyylllty.sertraline.logger.severeL
import io.github.zzzyyylllty.sertraline.logger.severeS
import io.github.zzzyyylllty.sertraline.logger.severeSSync
import io.github.zzzyyylllty.sertraline.logger.warningL
import io.github.zzzyyylllty.sertraline.database.DatabaseManager
import io.github.zzzyyylllty.sertraline.util.SertralineLocalDependencyHelper
import io.github.zzzyyylllty.sertraline.util.dependencies
import io.github.zzzyyylllty.sertraline.util.ItemTagManager
import io.github.zzzyyylllty.sertraline.util.ScriptHelper
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.graalvm.polyglot.Source
import java.util.concurrent.Callable
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.Cache
import java.util.concurrent.TimeUnit
import org.tabooproject.fluxon.runtime.FluxonRuntime
import taboolib.common.LifeCycle
import taboolib.common.PrimitiveSettings
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.env.RuntimeEnv
import taboolib.common.env.RuntimeEnvDependency
import taboolib.common.platform.Awake
import taboolib.common.platform.Plugin
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.console
import taboolib.common.platform.function.submit
import taboolib.expansion.JexlCompiledScript
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.module.kether.KetherShell
import taboolib.module.lang.Language
import taboolib.module.lang.asLangText
import taboolib.module.lang.event.PlayerSelectLocaleEvent
import taboolib.module.lang.event.SystemSelectLocaleEvent
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap
import javax.script.CompiledScript

//@RuntimeDependencies(
//    RuntimeDependency(
//        value = "!com.google.code.gson:gson:2.10.1",
//        relocate = ["!com.google.gson", "!io.github.zzzyyylllty.sertraline.dep.gson"]
//    ),
//    RuntimeDependency(
//        value = "!org.graalvm.polyglot:js:25.0.1",
//        // repository = "https://repo1.maven.org/maven2",
//        relocate = ["!graalvm.polyglot.js", "!io.github.zzzyyylllty.sertraline.dep.js"]
//    ),
//    RuntimeDependency(
//        value = "!org.graalvm.polyglot:polyglot:25.0.1",
//        // repository = "https://repo1.maven.org/maven2",
//        relocate = ["!org.graalvm.polyglot", "!io.github.zzzyyylllty.sertraline.dep.polyglot"]
//    ),
//    RuntimeDependency(
//        value = "!org.kotlincrypto.hash:sha2:0.7.0",
//        relocate = ["!org.kotlincrypto.hash", "!io.github.zzzyyylllty.sertraline.dep.hash"]
//    ),
//    RuntimeDependency(
//        value = "!com.github.ben-manes.caffeine:caffeine:3.2.3",
//        relocate = ["!com.github.benmanes.caffeine", "!io.github.zzzyyylllty.sertraline.dep.caffeine"]
//    ),
//    RuntimeDependency(
//        value = "!org.tabooproject.fluxon:core:1.2.18",
//        repository = "https://repo.tabooproject.org/repository/releases",
//        relocate = ["!org.tabooproject.fluxon", "!io.github.zzzyyylllty.sertraline.dep.fluxon"],
//        transitive = false  // 不下载传递依赖
//    ),
//    RuntimeDependency(
//        value = "!org.tabooproject.fluxon:core-console:1.2.18",
//        repository = "https://repo.tabooproject.org/repository/releases",
//        relocate = ["!org.tabooproject.fluxon", "!io.github.zzzyyylllty.sertraline.dep.fluxon"],
//        transitive = false  // 不下载传递依赖
//    ),
//    RuntimeDependency(
//        value = "!org.tabooproject.fluxon:core-jsr223:1.2.18",
//        repository = "https://repo.tabooproject.org/repository/releases",
//        relocate = ["!org.tabooproject.fluxon", "!io.github.zzzyyylllty.sertraline.dep.fluxon"],
//        transitive = false  // 不下载传递依赖
//    ),
//    RuntimeDependency(
//        value = "!com.mojang:datafixerupper:8.0.16",
//        // repository = "https://libraries.minecraft.net",
//        relocate = ["!com.mojang.datafixerupper", "!io.github.zzzyyylllty.sertraline.dep.datafixerupper"]
//    ),
//    RuntimeDependency(
//        value = "!io.github.projectunified:uni-item-all:2.3.1",
//        relocate = ["!io.github.projectunified.uniitem", "!io.github.zzzyyylllty.sertraline.dep.uniitem"],
//        transitive = false  // 不下载传递依赖
//    ),
//    RuntimeDependency(
//        value = "!net.kyori:adventure-text-serializer-legacy:4.19.0",
//        relocate = ["!net.kyori", "!io.github.zzzyyylllty.sertraline.dep.kyori"],
//        transitive = false  // 不下载传递依赖
//    ),
//    RuntimeDependency(
//        value = "!net.kyori:adventure-api:4.19.0",
//        relocate = ["!net.kyori", "!io.github.zzzyyylllty.sertraline.dep.kyori"],
//        transitive = false  // 不下载传递依赖
//    ),
//    RuntimeDependency(
//        value = "!net.kyori:adventure-text-minimessage:4.19.0",
//        relocate = ["!net.kyori", "!io.github.zzzyyylllty.sertraline.dep.kyori"],
//        transitive = false  // 不下载传递依赖
//    ),
//    RuntimeDependency(
//        value = "!net.kyori:adventure-nbt:4.19.0",
//        relocate = ["!net.kyori", "!io.github.zzzyyylllty.sertraline.dep.kyori"],
//        transitive = false  // 不下载传递依赖
//    ),
//    RuntimeDependency(
//        value = "!com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1",
//        relocate = ["!com.fasterxml.jackson", "!io.github.zzzyyylllty.sertraline.dep.jackson"]
//    ),
//    RuntimeDependency(
//        value = "!com.fasterxml.jackson.core:jackson-databind:2.16.1",
//        relocate = ["!com.fasterxml.jackson", "!io.github.zzzyyylllty.sertraline.dep.jackson"]
//    ),
//    RuntimeDependency(
//        value = "!com.fasterxml.jackson.core:jackson-annotations:2.16.1",
//        relocate = ["!com.fasterxml.jackson", "!io.github.zzzyyylllty.sertraline.dep.jackson"]
//    ),
//    RuntimeDependency(
//        value = "!com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.16.1",
//        relocate = ["!com.fasterxml.jackson", "!io.github.zzzyyylllty.sertraline.dep.jackson"]
//    ),
//    RuntimeDependency(
//        value = "!com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.16.1",
//        relocate = ["!com.fasterxml.jackson", "!io.github.zzzyyylllty.sertraline.dep.jackson"]
//    ),
//    RuntimeDependency(
//        value = "!top.maplex.arim:Arim:1.3.2",
//        repository = "https://repo.tabooproject.org/repository/releases",
//        relocate = ["!top.maplex.arim", "!io.github.zzzyyylllty.sertraline.dep.arim"]
//    ),
//    RuntimeDependency(
//        value = "!org.kotlincrypto.hash:sha2:0.7.0",
//        relocate = ["!org.kotlincrypto.hash", "!io.github.zzzyyylllty.sertraline.dep.hash"]
//    ),
//)
class RuntimeEnv


/*
 *  $$$$$$\                        $$\                         $$\ $$\
 * $$  __$$\                       $$ |                        $$ |\__|
 * $$ /  \__| $$$$$$\   $$$$$$\  $$$$$$\    $$$$$$\   $$$$$$\  $$ |$$\ $$$$$$$\   $$$$$$\
 * \$$$$$$\  $$  __$$\ $$  __$$\ \_$$  _|  $$  __$$\  \____$$\ $$ |$$ |$$  __$$\ $$  __$$\
 *  \____$$\ $$$$$$$$ |$$ |  \__|  $$ |    $$ |  \__| $$$$$$$ |$$ |$$ |$$ |  $$ |$$$$$$$$ |
 * $$\   $$ |$$   ____|$$ |        $$ |$$\ $$ |      $$  __$$ |$$ |$$ |$$ |  $$ |$$   ____|
 * \$$$$$$  |\$$$$$$$\ $$ |        \$$$$  |$$ |      \$$$$$$$ |$$ |$$ |$$ |  $$ |\$$$$$$$\
 *  \______/  \_______|\__|         \____/ \__|       \_______|\__|\__|\__|  \__| \_______|
 */
object Sertraline : Plugin() {


    @Config("config.yml", migrate = true)
    lateinit var config: Configuration

    val _api: SertralineAPI? by lazy { SertralineAPIImpl() }
    val plugin by lazy { this }
    val dataFolder by lazy { nativeDataFolder() }
    val console by lazy { console() }
    val consoleSender by lazy { console.castSafely<CommandSender>()!! }
    var fluxonInst: FluxonRuntime? = null

    var itemMap: LinkedHashMap<String, ModernSItem> = LinkedHashMap<String, ModernSItem>()
    var mappings = LinkedHashMap<String, List<String>?>()
    var loreFormats = LinkedHashMap<String, LoreFormat>()
    var craftingStations = LinkedHashMap<String, CraftingStation>()
    var tiers = LinkedHashMap<String, Tier>()
    var types = LinkedHashMap<String, Type>()
    var levels = LinkedHashMap<String, Level>()
    val dateTimeFormatter: DateTimeFormatter by lazy { DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") }
    val itemManager by lazy { ItemProcessorManager() }
    val manager = io.github.zzzyyylllty.sertraline.manager.ItemManager()
    val tagManager by lazy { TagProcessorManager() }
    var devMode = true
    var allowAsyncLog = true
    var isEnabled = false
    val fileLastModified: MutableMap<String, Long> = mutableMapOf()
    val ketherScriptCache = ConcurrentHashMap<String, KetherShell.Cache?>()
    val jsScriptCache = ConcurrentHashMap<String, CompiledScript?>()
    val gjsScriptCache = ConcurrentHashMap<String, Source?>()
    val jexlScriptCache = ConcurrentHashMap<String, JexlCompiledScript?>()
    val itemCache = ConcurrentHashMap<String, Map<String, Any?>?>()
    val itemExpectedRevision: MutableMap<String, Int> = mutableMapOf()

    fun api() : SertralineAPI {
        return _api ?: throw IllegalStateException("Sertraline API not present,or failed to load")
    }

    override fun onLoad() {
        fluxonInst = FluxonRuntime.getInstance()
        DatabaseManager.init()
    }

    override fun onEnable() {
        isEnabled = true
        infoL("Enable")
        Language.enableSimpleComponent = true
//        try {
//            reloadCustomConfig()
//        } catch (e: Exception) {
//            severeL("Failed to load configurations during startup: ${e.message}")
//            e.printStackTrace()
//        }
    }

    override fun onDisable() {
        // 取消所有合成任务（保留持久化数据，玩家下次加入时可恢复）
        CraftingStationManager.shutdownAll()
        // 清理临时物品（私有临时物品在服务器关闭后销毁）
        manager.shutdown()
        infoLSync("Disable")
    }
    /*
    fun compat() {
        if (Bukkit.getPluginManager().getPlugin("Chemdah") != null) {
            connectChemdah()
        }
    }*/

    @Awake(LifeCycle.ENABLE)
    fun onEnableLoad() {
        try {
        reloadCustomConfig(true)
        } catch (e: Exception) {
            severeL("Failed to load configurations during startup: ${e.message}")
            e.printStackTrace()
        }
    }

    fun reloadCustomConfig(async: Boolean = true, sender: CommandSender? = null) {
        submit(async = async) {
            ReloadCollector.begin()

            config.reload()
            devMode = config.getBoolean("debug", false)
            allowAsyncLog = config.getBoolean("async-logging", true)

            // save public-temporary items, restore after reload
            manager.preReload()

            itemMap.clear()
            mappings.clear()
            loreFormats.clear()
            CraftingStationManager.cancelAll()
            craftingStations.clear()
            tiers.clear()
            types.clear()
            levels.clear()
            itemExpectedRevision.clear()
            ketherScriptCache.clear()
            jsScriptCache.clear()
            gjsScriptCache.clear()
            jexlScriptCache.clear()
            itemCache.clear()

            itemManager.unregisterAllProcessor()
            tagManager.unregisterAllProcessor()
            AttributeManager.unregisterAll()
            registerNativeAdapter()
            registerNativeTagAdapter()
            registerNativeAttributeProviders()

            // 模板优先加载，后续所有配置均可引用
            try { TemplateManager.loadTemplates() } catch (e: Exception) {
                severeL("Config_Load_Error_Parse", "templates", e.message ?: "Unknown error")
            }
            ReloadCollector.addStat(console.asLangText("Reload_Stat_Templates", TemplateManager.templateCount()))

            try { loadMappingFiles() } catch (e: Exception) {
                severeL("Mapping_Load_Error_Parse", "mappings", e.message ?: "Unknown error")
            }
            ReloadCollector.addStat(console.asLangText("Reload_Stat_Mappings", mappings.size))

            // tier/type/level 必须在物品之前加载，否则物品的 FeatureLoadEvent 查不到对应数据
            try { loadTierFiles() } catch (e: Exception) {
                severeL("Tier_Load_Error_Parse", "tiers", e.message ?: "Unknown error")
            }
            ReloadCollector.addStat(console.asLangText("Reload_Stat_Tiers", tiers.size))

            try { loadTypeFiles() } catch (e: Exception) {
                severeL("Type_Load_Error_Parse", "types", e.message ?: "Unknown error")
            }
            ReloadCollector.addStat(console.asLangText("Reload_Stat_Types", types.size))

            try { loadLevelFiles() } catch (e: Exception) {
                severeL("Level_Load_Error_Parse", "levels", e.message ?: "Unknown error")
            }
            ReloadCollector.addStat(console.asLangText("Reload_Stat_Levels", levels.size))

            try { loadItemFiles() } catch (e: Exception) {
                severeL("Config_Load_Error_Parse", "items", e.message ?: "Unknown error")
            }
            // restore public-temporary items that were saved before reload
            manager.postReload()
            ReloadCollector.addStat(console.asLangText("Reload_Stat_Items", itemMap.size))

            try { loadLoreFormatFiles() } catch (e: Exception) {
                severeL("LoreFormat_Load_Error_Parse", "lore-formats", e.message ?: "Unknown error")
            }
            ReloadCollector.addStat(console.asLangText("Reload_Stat_LoreFormats", loreFormats.size))

            try { loadCraftingStationFiles() } catch (e: Exception) {
                severeL("CraftingStation_Load_Error_Parse", "crafting-stations", e.message ?: "Unknown error")
            }
            ReloadCollector.addStat(console.asLangText("Reload_Stat_CraftingStations", craftingStations.size))

            try { initRevisionAutoTracker() } catch (e: Exception) {
                severeL("Config_Load_Error_Parse", "revision-auto-tracker", e.message ?: "Unknown error")
            }

            // 配方注册 + 事件触发等 Bukkit API 操作必须在主线程执行
            if (Bukkit.isPrimaryThread()) {
                runRecipeSyncTasks(sender)
            } else {
                val plugin = Bukkit.getPluginManager().getPlugin("Sertraline")
                if (plugin != null) {
                    Bukkit.getScheduler().callSyncMethod(plugin, Callable {
                        runRecipeSyncTasks(sender)
                        null
                    }).get()
                } else {
                    severeL("Config_Load_Error_Parse", "recipes", "Sertraline plugin instance not found for sync task")
                }
            }
        }
    }

    private fun runRecipeSyncTasks(sender: CommandSender?) {
        try { loadRecipeFiles() } catch (e: Exception) {
            severeL("Config_Load_Error_Parse", "recipes", e.message ?: "Unknown error")
        }
        try { ScriptHelper.loadScriptFiles() } catch (e: Exception) {
            severeS("Failed to load scripts: ${e.message}")
        }
        SertralineReloadEvent().call()
        ReloadCollector.printSummary(sender)
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
        event.locale = config.getString("lang", "en_US")!!
    }

    @SubscribeEvent
    fun lang(event: SystemSelectLocaleEvent) {
        event.locale = config.getString("lang", "en_US")!!
    }


    @Awake(LifeCycle.INIT)
    fun initDependenciesInit() {
        solveDependencies(dependencies, true)
    }

    @Awake(LifeCycle.ENABLE)
    fun initItemTags() {
        try {
            ItemTagManager.initializeVanillaTags()
            infoL("ItemTagManager initialized successfully")
        } catch (e: Exception) {
            severeL("Failed to initialize ItemTagManager: ${e.message}")
        }
    }


    fun solveDependencies(dependencies: List<String>, useTaboo: Boolean = false) {
        devLogSync("Starting loading dependencies...")
        for (name in dependencies) {
            try {
                infoSSync("Trying to load dependencies from file $name")
                val resource = Sertraline::class.java.classLoader.getResource("META-INF/dependencies/$name.json")
                if (resource == null) {
                    severeS("Resource META-INF/dependencies/$name.json not found!")
                    continue // 跳过这个依赖文件
                }

                if (useTaboo) RuntimeEnv.ENV_DEPENDENCY.loadFromLocalFile(resource) else SertralineLocalDependencyHelper().loadFromLocalFile(resource)

                infoSSync("Trying to load dependencies from file $name ... DONE.")
            } catch (e: Exception) {
                severeSSync("Trying to load dependencies from file $name FAILED.")
                severeSSync("Exception: $e")
                e.printStackTrace()
            }
        }
    }

}

fun registerNativeAttributeProviders() {
    AttributeManager.register(MythicLibAttributeProvider())
    AttributeManager.register(ChotenAttributeProvider())
}
