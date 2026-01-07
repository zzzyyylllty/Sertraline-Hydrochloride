package io.github.zzzyyylllty.sertraline

import io.github.zzzyyylllty.sertraline.api.SertralineAPI
import io.github.zzzyyylllty.sertraline.api.SertralineAPIImpl
import io.github.zzzyyylllty.sertraline.config.ConfigUtil
import io.github.zzzyyylllty.sertraline.config.loadItemFiles
import io.github.zzzyyylllty.sertraline.config.loadLoreFormatFiles
import io.github.zzzyyylllty.sertraline.config.loadMappingFiles
import io.github.zzzyyylllty.sertraline.data.LoreFormat
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.debugMode.devLogSync
import io.github.zzzyyylllty.sertraline.event.SertralineReloadEvent
import io.github.zzzyyylllty.sertraline.listener.sertraline.builder.ItemProcessorManager
import io.github.zzzyyylllty.sertraline.listener.sertraline.builder.registerNativeAdapter
import io.github.zzzyyylllty.sertraline.item.process.tag.TagProcessorManager
import io.github.zzzyyylllty.sertraline.item.process.tag.registerNativeTagAdapter
import io.github.zzzyyylllty.sertraline.logger.infoL
import io.github.zzzyyylllty.sertraline.logger.infoLSync
import io.github.zzzyyylllty.sertraline.logger.infoSSync
import io.github.zzzyyylllty.sertraline.logger.severeS
import io.github.zzzyyylllty.sertraline.logger.severeSSync
import io.github.zzzyyylllty.sertraline.util.SertralineLocalDependencyHelper
import io.github.zzzyyylllty.sertraline.util.dependencies
import org.bukkit.command.CommandSender
import org.graalvm.polyglot.Source
import org.tabooproject.fluxon.runtime.FluxonRuntime
import taboolib.common.LifeCycle
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.env.RuntimeEnv
import taboolib.common.platform.Awake
import taboolib.common.platform.Plugin
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.console
import taboolib.common.platform.function.submit
import taboolib.expansion.JexlCompiledScript
import taboolib.expansion.setupPlayerDatabase
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.module.database.getHost
import taboolib.module.kether.KetherShell
import taboolib.module.lang.Language
import taboolib.module.lang.event.PlayerSelectLocaleEvent
import taboolib.module.lang.event.SystemSelectLocaleEvent
import java.io.File
import java.time.format.DateTimeFormatter
import javax.script.CompiledScript

//@RuntimeDependencies(
//    RuntimeDependency(
//        value = "!com.google.code.gson:gson:2.10.1",
//        relocate = ["!com.google.gson", "!io.github.zzzyyylllty.sertraline.library.gson"]
//    ),
//    RuntimeDependency(
//        value = "!org.graalvm.polyglot:js:25.0.1",
//        // repository = "https://repo1.maven.org/maven2",
//        relocate = ["!graalvm.polyglot.js", "!io.github.zzzyyylllty.sertraline.library.js"]
//    ),
//    RuntimeDependency(
//        value = "!org.graalvm.polyglot:polyglot:25.0.1",
//        // repository = "https://repo1.maven.org/maven2",
//        relocate = ["!org.graalvm.polyglot", "!io.github.zzzyyylllty.sertraline.library.polyglot"]
//    ),
//    RuntimeDependency(
//        value = "!org.kotlincrypto.hash:sha2:0.7.0",
//        relocate = ["!org.kotlincrypto.hash", "!io.github.zzzyyylllty.sertraline.library.hash"]
//    ),
//    RuntimeDependency(
//        value = "!com.github.ben-manes.caffeine:caffeine:3.2.3",
//        relocate = ["!com.github.benmanes.caffeine", "!io.github.zzzyyylllty.sertraline.library.caffeine"]
//    ),
//    RuntimeDependency(
//        value = "!org.tabooproject.fluxon:core:1.2.18",
//        repository = "https://repo.tabooproject.org/repository/releases",
//        relocate = ["!org.tabooproject.fluxon", "!io.github.zzzyyylllty.sertraline.library.fluxon"],
//        transitive = false  // 不下载传递依赖
//    ),
//    RuntimeDependency(
//        value = "!org.tabooproject.fluxon:core-console:1.2.18",
//        repository = "https://repo.tabooproject.org/repository/releases",
//        relocate = ["!org.tabooproject.fluxon", "!io.github.zzzyyylllty.sertraline.library.fluxon"],
//        transitive = false  // 不下载传递依赖
//    ),
//    RuntimeDependency(
//        value = "!org.tabooproject.fluxon:core-jsr223:1.2.18",
//        repository = "https://repo.tabooproject.org/repository/releases",
//        relocate = ["!org.tabooproject.fluxon", "!io.github.zzzyyylllty.sertraline.library.fluxon"],
//        transitive = false  // 不下载传递依赖
//    ),
//    RuntimeDependency(
//        value = "!com.mojang:datafixerupper:8.0.16",
//        // repository = "https://libraries.minecraft.net",
//        relocate = ["!com.mojang.datafixerupper", "!io.github.zzzyyylllty.sertraline.library.datafixerupper"]
//    ),
//    RuntimeDependency(
//        value = "!io.github.projectunified:uni-item-all:2.3.1",
//        relocate = ["!io.github.projectunified.uniitem", "!io.github.zzzyyylllty.sertraline.library.uniitem"],
//        transitive = false  // 不下载传递依赖
//    ),
//    RuntimeDependency(
//        value = "!net.kyori:adventure-text-serializer-legacy:4.19.0",
//        relocate = ["!net.kyori", "!io.github.zzzyyylllty.sertraline.library.kyori"],
//        transitive = false  // 不下载传递依赖
//    ),
//    RuntimeDependency(
//        value = "!net.kyori:adventure-api:4.19.0",
//        relocate = ["!net.kyori", "!io.github.zzzyyylllty.sertraline.library.kyori"],
//        transitive = false  // 不下载传递依赖
//    ),
//    RuntimeDependency(
//        value = "!net.kyori:adventure-text-minimessage:4.19.0",
//        relocate = ["!net.kyori", "!io.github.zzzyyylllty.sertraline.library.kyori"],
//        transitive = false  // 不下载传递依赖
//    ),
//    RuntimeDependency(
//        value = "!net.kyori:adventure-nbt:4.19.0",
//        relocate = ["!net.kyori", "!io.github.zzzyyylllty.sertraline.library.kyori"],
//        transitive = false  // 不下载传递依赖
//    ),
//    RuntimeDependency(
//        value = "!com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1",
//        relocate = ["!com.fasterxml.jackson", "!io.github.zzzyyylllty.sertraline.library.jackson"]
//    ),
//    RuntimeDependency(
//        value = "!com.fasterxml.jackson.core:jackson-databind:2.16.1",
//        relocate = ["!com.fasterxml.jackson", "!io.github.zzzyyylllty.sertraline.library.jackson"]
//    ),
//    RuntimeDependency(
//        value = "!com.fasterxml.jackson.core:jackson-annotations:2.16.1",
//        relocate = ["!com.fasterxml.jackson", "!io.github.zzzyyylllty.sertraline.library.jackson"]
//    ),
//    RuntimeDependency(
//        value = "!com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.16.1",
//        relocate = ["!com.fasterxml.jackson", "!io.github.zzzyyylllty.sertraline.library.jackson"]
//    ),
//    RuntimeDependency(
//        value = "!com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.16.1",
//        relocate = ["!com.fasterxml.jackson", "!io.github.zzzyyylllty.sertraline.library.jackson"]
//    ),
//    RuntimeDependency(
//        value = "!top.maplex.arim:Arim:1.3.2",
//        repository = "https://repo.tabooproject.org/repository/releases",
//        relocate = ["!top.maplex.arim", "!io.github.zzzyyylllty.sertraline.library.arim"]
//    ),
//    RuntimeDependency(
//        value = "!org.kotlincrypto.hash:sha2:0.7.0",
//        relocate = ["!org.kotlincrypto.hash", "!io.github.zzzyyylllty.sertraline.library.hash"]
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


    @Config("config.yml")
    lateinit var config: Configuration

    val api: SertralineAPI? by lazy { SertralineAPIImpl().INSTANCE }
    val plugin by lazy { this }
    val dataFolder by lazy { nativeDataFolder() }
    val console by lazy { console() }
    val consoleSender by lazy { console.castSafely<CommandSender>()!! }
    val host by lazy { config.getHost("database") }
    val dataSource by lazy { host.createDataSource() }
    var fluxonInst: FluxonRuntime? = null

    var itemMap = LinkedHashMap<String, ModernSItem>()
    var mappings = LinkedHashMap<String, List<String>?>() 
    var loreFormats = LinkedHashMap<String, LoreFormat>() 
    val dateTimeFormatter: DateTimeFormatter by lazy { DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") }
    val itemManager by lazy { ItemProcessorManager() }
    val tagManager by lazy { TagProcessorManager() }
    var devMode = true
    var allowAsyncLog = true
    var isEnabled = false
    val configUtil by lazy { ConfigUtil }
    val ketherScriptCache by lazy { LinkedHashMap<String, KetherShell.Cache?>() }
    val jsScriptCache by lazy { LinkedHashMap<String, CompiledScript?>() }
    val gjsScriptCache by lazy { LinkedHashMap<String, Source?>() }
    val jexlScriptCache by lazy { LinkedHashMap<String, JexlCompiledScript?>() }
    val itemCache by lazy { LinkedHashMap<String, Map<String, Any?>?>() }

    fun api() : SertralineAPI {
        return api ?: error("SertralineAPI has not finished loading, or failed to load!")
    }

    override fun onLoad() {
        fluxonInst = FluxonRuntime.getInstance()
        if (config.getBoolean("database.enable", false)) {
            setupPlayerDatabase(config.getConfigurationSection("database")!!)
        } else {
            setupPlayerDatabase(File("${config.getString("database.filename") ?: "data"}.db"))
        }
    }

    override fun onEnable() {
        isEnabled = true
        infoL("Enable")
        Language.enableSimpleComponent = true
        reloadCustomConfig()

    }

    override fun onDisable() {
        infoLSync("Disable")
    }
    /*
    fun compat() {
        if (Bukkit.getPluginManager().getPlugin("Chemdah") != null) {
            connectChemdah()
        }
    }*/

    fun reloadCustomConfig(async: Boolean = true) {
        submit(async) {

            config.reload()
            devMode = config.getBoolean("debug",false)
            allowAsyncLog = config.getBoolean("async-logging", true)

            itemMap.clear()
            mappings.clear()
            loreFormats.clear()

            ketherScriptCache.clear()
            jsScriptCache.clear()
            jexlScriptCache.clear()
            gjsScriptCache.clear()
            itemCache.clear()

            itemManager.unregisterAllProcessor()
            tagManager.unregisterAllProcessor()
            registerNativeAdapter()
            registerNativeTagAdapter()

            loadMappingFiles()
            loadItemFiles()
            loadLoreFormatFiles()

            SertralineReloadEvent().call()
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


    @Awake(LifeCycle.INIT)
    fun initDependenciesInit() {
        solveDependencies(dependencies)
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
