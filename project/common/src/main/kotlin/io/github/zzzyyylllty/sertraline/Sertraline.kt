package io.github.zzzyyylllty.sertraline

import io.github.zzzyyylllty.sertraline.config.ConfigUtil
import io.github.zzzyyylllty.sertraline.config.loadItemFiles
import io.github.zzzyyylllty.sertraline.config.loadLoreFormatFiles
import io.github.zzzyyylllty.sertraline.config.loadMappingFiles
import io.github.zzzyyylllty.sertraline.data.LoreFormat
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.listener.sertraline.builder.ItemProcessorManager
import io.github.zzzyyylllty.sertraline.listener.sertraline.builder.registerNativeAdapter
import io.github.zzzyyylllty.sertraline.listener.sertraline.tag.TagProcessorManager
import io.github.zzzyyylllty.sertraline.listener.sertraline.tag.registerNativeTagAdapter
import io.github.zzzyyylllty.sertraline.logger.*
import io.github.zzzyyylllty.sertraline.reflect.*
import org.bukkit.command.CommandSender
import org.tabooproject.fluxon.runtime.FluxonRuntime
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.Plugin
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.console
import taboolib.common.platform.function.submit
import taboolib.expansion.JexlCompiledScript
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.module.database.getHost
import taboolib.module.kether.KetherShell
import taboolib.module.lang.Language
import taboolib.module.lang.event.PlayerSelectLocaleEvent
import taboolib.module.lang.event.SystemSelectLocaleEvent
import top.maplex.arim.tools.conditionevaluator.ConditionEvaluator
import top.maplex.arim.tools.fixedcalculator.FixedCalculator
import top.maplex.arim.tools.variablecalculator.VariableCalculator
import java.time.format.DateTimeFormatter
import java.util.*
import javax.script.CompiledScript


//@RuntimeDependencies(
//    RuntimeDependency(
//        value = "!com.google.code.gson:gson:2.10.1",
//        relocate = ["!com.google.gson", "!io.github.zzzyyylllty.sertraline.library.com.google.gson"]
//    ),
////    RuntimeDependency(
////        value = "!org.graalvm.polyglot:js:25.0.1",
////        repository = "https://repo1.maven.org/maven2/",
////        relocate = ["!com.google.gson", "!io.github.zzzyyylllty.sertraline.library.org.graalvm.polyglot.js"]
////    ),
////    RuntimeDependency(
////        value = "!org.graalvm.polyglot:polyglot:25.0.1",
////        repository = "https://repo1.maven.org/maven2/",
////        relocate = ["!org.graalvm.polyglot", "!io.github.zzzyyylllty.sertraline.library.org.graalvm.polyglot.polyglot"]
////    ),
////    RuntimeDependency(
////        value = "!org.kotlincrypto.hash:sha2:0.7.0",
////        relocate = ["!org.kotlincrypto.hash", "!io.github.zzzyyylllty.sertraline.library.org.kotlincrypto.hash"]
////    ),
//    RuntimeDependency(
//        value = "!com.github.ben-manes.caffeine:caffeine:3.2.3",
//        relocate = ["!com.github.ben-manes.caffeine", "!io.github.zzzyyylllty.sertraline.library.com.github.ben-manes.caffeine"]
//    ),
////    RuntimeDependency(
////        value = "!org.tabooproject.fluxon:core:1.2.18",
////        repository = "https://repo.tabooproject.org/repository/releases/",
////        relocate = ["!org.tabooproject.fluxon", "!io.github.zzzyyylllty.sertraline.library.org.tabooproject.fluxon"]
////    ),
//    RuntimeDependency(
//        value = "!com.mojang:datafixerupper:8.0.16",
//        repository = "https://libraries.minecraft.net",
//        relocate = ["!com.mojang.datafixerupper", "!io.github.zzzyyylllty.sertraline.library.com.mojang.datafixerupper"]
//    ),
//    RuntimeDependency(
//        value = "!io.github.projectunified:uni-item-all:2.3.1",
//        relocate = ["!io.github.projectunified.uniitem", "!io.github.zzzyyylllty.sertraline.library.io.github.projectunified.uniitem"]
//    ),
//    RuntimeDependency(
//        value = "!net.kyori:adventure-text-serializer-legacy:4.19.0",
//        relocate = ["!net.kyori", "!io.github.zzzyyylllty.sertraline.library.net.kyori"]
//    ),
//    RuntimeDependency(
//        value = "!net.kyori:adventure-api:4.19.0",
//        relocate = ["!net.kyori", "!io.github.zzzyyylllty.sertraline.library.net.kyori"]
//    ),
//    RuntimeDependency(
//        value = "!net.kyori:adventure-text-minimessage:4.19.0",
//        relocate = ["!net.kyori", "!io.github.zzzyyylllty.sertraline.library.net.kyori"]
//    ),
//    RuntimeDependency(
//        value = "!net.kyori:adventure-nbt:4.19.0",
//        relocate = ["!net.kyori", "!io.github.zzzyyylllty.sertraline.library.net.kyori"]
//    ),
//    RuntimeDependency(
//        value = "!com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1",
//        repository = "http://mvnrepository.com/artifact",
//        relocate = ["!com.fasterxml.jackson", "!io.github.zzzyyylllty.sertraline.library.com.fasterxml.jackson"]
//    ),
//    RuntimeDependency(
//        value = "!com.fasterxml.jackson.core:jackson-databind:2.16.1",
//        repository = "http://mvnrepository.com/artifact",
//        relocate = ["!com.fasterxml.jackson", "!io.github.zzzyyylllty.sertraline.library.com.fasterxml.jackson"]
//    ),
//    RuntimeDependency(
//        value = "!com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.16.1",
//        repository = "http://mvnrepository.com/artifact",
//        relocate = ["!com.fasterxml.jackson", "!io.github.zzzyyylllty.sertraline.library.com.fasterxml.jackson"]
//    ),
//    RuntimeDependency(
//        value = "!com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.16.1",
//        repository = "http://mvnrepository.com/artifact",
//        relocate = ["!com.fasterxml.jackson", "!io.github.zzzyyylllty.sertraline.library.com.fasterxml.jackson"]
//    ),
//    RuntimeDependency(
//        value = "!top.maplex.arim:Arim:1.3.2",
//        repository = "https://repo.tabooproject.org/repository/releases/",
//        relocate = ["!top.maplex.arim", "!io.github.zzzyyylllty.sertraline.library.top.maplex.arim"]
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

    val plugin by lazy { this }
    var dataFolder = nativeDataFolder()
    val console by lazy { console() }
    val consoleSender by lazy { console.castSafely<CommandSender>()!! }
    val host by lazy { config.getHost("database") }
    val dataSource by lazy { host.createDataSource() }
    val fluxonInst by lazy { FluxonRuntime.getInstance() }

    var itemMap = LinkedHashMap<String, ModernSItem>()
    var mappings = LinkedHashMap<String, List<String>?>()
    var loreFormats = LinkedHashMap<String, LoreFormat>()
    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val itemManager = ItemProcessorManager()
    val tagManager = TagProcessorManager()
    var devMode = true
    val reflects = ReflectTargets()
    val configUtil = ConfigUtil()
    val ketherScriptCache = LinkedHashMap<String, KetherShell.Cache?>()
    val jsScriptCache = LinkedHashMap<String, CompiledScript?>()
    val jexlScriptCache = LinkedHashMap<String, JexlCompiledScript?>()
    val itemCache = LinkedHashMap<String, Map<String, Any?>?>()

    // Arim Start
    val evaluator by lazy { ConditionEvaluator() }
    val fixedCalculator by lazy { FixedCalculator() }
    val variableCalculator by lazy { VariableCalculator() }

    @Config("config.yml")
    lateinit var config: Configuration


    override fun onEnable() {

        infoL("Enable")
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

    fun reloadCustomConfig(async: Boolean = true) {
        submit(async) {

            config.reload()
            devMode = config.getBoolean("debug",false)

            itemMap.clear()
            mappings.clear()
            loreFormats.clear()

            ketherScriptCache.clear()
            jsScriptCache.clear()
            jexlScriptCache.clear()
            itemCache.clear()

            itemManager.unregisterAllProcessor()
            tagManager.unregisterAllProcessor()
            registerNativeAdapter()
            registerNativeTagAdapter()

            loadMappingFiles()
            loadItemFiles()
            loadLoreFormatFiles()
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
