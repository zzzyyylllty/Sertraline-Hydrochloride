package io.github.zzzyyylllty.sertraline.util

import io.github.zzzyyylllty.sertraline.data.defaultData
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.function.fluxon.FluxonShell
import io.github.zzzyyylllty.sertraline.logger.infoS
import io.github.zzzyyylllty.sertraline.logger.severeS
import io.github.zzzyyylllty.sertraline.logger.warningS
import org.bukkit.command.CommandSender
import taboolib.common.LifeCycle
import taboolib.common.env.RuntimeEnv
import taboolib.common.platform.Awake
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFolder
import taboolib.module.configuration.Configuration
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptOptions
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import javax.script.ScriptEngineManager


/**
 * 脚本管理系统
 *
 * 支持从 scripts/ 目录加载 YAML 脚本文件，按名称执行。
 *
 * 脚本类型: graaljs (GraalVM Polyglot), javascript/js (标准 JSR 223), kether, fluxon, jexl
 *
 * 使用示例:
 *   ScriptHelper.eval("sample", "arg1_value")
 *   ScriptHelper.eval("sample", mapOf("player" to player, "amount" to 10))
 */
object ScriptHelper {

    val engineManager by lazy { ScriptEngineManager(this::class.java.classLoader) }

    private val scriptCache = ConcurrentHashMap<String, ScriptEntry>()
    private val contextHolder = ThreadLocal<ScriptContext>()

    data class ScriptEntry(
        val type: ScriptType,
        val script: String,
        val source: String
    )

    enum class ScriptType {
        /** GraalVM Polyglot API (高性能, 支持 ES2023) */
        GRAALJS,
        /** 标准 JSR 223 javax.script JavaScript / Nashorn */
        JAVASCRIPT,
        KETHER, FLUXON, JEXL;

        companion object {
            fun fromString(s: String): ScriptType = when (s.lowercase()) {
                "graaljs" -> GRAALJS
                "javascript", "js" -> JAVASCRIPT
                "kether" -> KETHER
                "fluxon", "fx" -> FLUXON
                "jexl" -> JEXL
                else -> throw IllegalArgumentException("Unknown script type: $s")
            }
        }
    }

    data class ScriptContext(
        val sender: CommandSender? = null,
        val vars: Map<String, Any?> = emptyMap()
    )

    @Awake(LifeCycle.INIT)
    private fun initDependency() {
        loadDependencies("graaljs")
    }

    internal fun loadDependencies(name: String) {
        RuntimeEnv.ENV_DEPENDENCY.loadFromLocalFile(
            this::class.java.classLoader.getResource("META-INF/dependencies/$name.json")
        )
    }

    // ==================== 脚本加载 ====================

    /**
     * 从 dataFolder/scripts/ 目录加载所有 YAML 脚本文件
     */
    fun loadScriptFiles() {
        scriptCache.clear()
        val scriptsDir = File(getDataFolder(), "scripts")

        if (!scriptsDir.exists()) {
            warningS("Scripts directory not found, releasing default...")
            try {
                releaseResourceFolder("scripts")
            } catch (_: Exception) {
                scriptsDir.mkdirs()
            }
        }

        if (!scriptsDir.exists()) {
            severeS("Failed to create scripts directory: ${scriptsDir.absolutePath}")
            return
        }

        var count = 0
        scriptsDir.walk()
            .filter { it.isFile && it.extension in setOf("yml", "yaml") }
            .forEach { file ->
                count += loadScriptFile(file)
            }

        infoS("Loaded $count scripts from ${scriptsDir.absolutePath}")
        devLog("Script cache entries: ${scriptCache.keys}")
    }

    /**
     * 重新加载脚本
     */
    fun reloadScripts() {
        loadScriptFiles()
    }

    /**
     * 获取已加载的所有脚本名称
     */
    fun getScriptNames(): Set<String> = scriptCache.keys.toSet()

    /**
     * 检查脚本是否存在
     */
    fun hasScript(name: String): Boolean = scriptCache.containsKey(name)

    private fun loadScriptFile(file: File): Int {
        var count = 0
        try {
            val config = Configuration.loadFromFile(file)
            for (key in config.getKeys(false)) {
                val section = config.getConfigurationSection(key) ?: continue
                val typeStr = section.getString("type") ?: "graaljs"
                val script = section.getString("script") ?: continue

                val type = try {
                    ScriptType.fromString(typeStr)
                } catch (e: IllegalArgumentException) {
                    warningS("Unknown script type '$typeStr' in script '$key' (${file.name}), skipping")
                    continue
                }

                scriptCache[key] = ScriptEntry(type, script, file.name)
                count++
            }
        } catch (e: Exception) {
            severeS("Failed to load script file: ${file.name} - ${e.message}")
        }
        return count
    }

    // ==================== 上下文管理 ====================

    /**
     * 推入脚本执行上下文，后续 eval 调用将自动继承此上下文
     *
     * @param sender 命令发送者（玩家/控制台）
     * @param vars   上下文变量
     */
    fun pushContext(sender: CommandSender? = null, vars: Map<String, Any?> = emptyMap()) {
        contextHolder.set(ScriptContext(sender, vars))
    }

    /**
     * 弹出当前线程的脚本执行上下文
     */
    fun popContext() {
        contextHolder.remove()
    }

    /**
     * 使用指定上下文执行代码块，自动管理 push/pop
     *
     * @param sender 上下文发送者
     * @param vars   上下文变量
     * @param block  要执行的代码块
     */
    fun <T> withContext(sender: CommandSender? = null, vars: Map<String, Any?> = emptyMap(), block: () -> T): T {
        val previous = contextHolder.get()
        contextHolder.set(ScriptContext(sender, vars))
        try {
            return block()
        } finally {
            if (previous != null) contextHolder.set(previous) else contextHolder.remove()
        }
    }

    /**
     * 获取当前线程的脚本执行上下文
     */
    fun currentContext(): ScriptContext? = contextHolder.get()

    // ==================== 执行 API ====================

    /**
     * 按名称执行脚本，传递变长参数（映射为 arg1, arg2, ...）
     *
     * @param name 脚本名称（YAML 中的 key）
     * @param args 变长参数，在脚本中可通过 arg1, arg2, ... 访问
     * @return 脚本执行结果
     */
    fun eval(name: String, vararg args: Any?): Any? {
        val entry = scriptCache[name] ?: run {
            warningS("Script '$name' not found, available: ${scriptCache.keys}")
            return null
        }

        val ctx = currentContext()
        val vars = LinkedHashMap<String, Any?>()

        // 注入全局默认数据 (mmUtil, Bukkit, PlayerUtil, 等)
        vars.putAll(defaultData)

        // 继承上下文变量
        if (ctx != null) vars.putAll(ctx.vars)

        // 变长参数映射为 arg1, arg2, ...
        args.forEachIndexed { i, v -> vars["arg${i + 1}"] = v }

        return execute(entry, vars, ctx?.sender)
    }

    /**
     * 按名称执行脚本，传递命名参数
     *
     * @param name   脚本名称（YAML 中的 key）
     * @param vars   命名参数
     * @param sender 命令发送者，覆盖上下文中的 sender
     * @return 脚本执行结果
     */
    fun eval(name: String, vars: Map<String, Any?>, sender: CommandSender? = null): Any? {
        val entry = scriptCache[name] ?: run {
            warningS("Script '$name' not found, available: ${scriptCache.keys}")
            return null
        }

        val ctx = currentContext()
        val mergedVars = LinkedHashMap<String, Any?>()

        // 注入全局默认数据 (mmUtil, Bukkit, PlayerUtil, 等)
        mergedVars.putAll(defaultData)

        // 继承上下文变量
        if (ctx != null) mergedVars.putAll(ctx.vars)

        // 显式传入的变量覆盖继承的变量
        mergedVars.putAll(vars)

        return execute(entry, mergedVars, sender ?: ctx?.sender)
    }

    /**
     * 异步执行脚本
     */
    fun evalAsync(name: String, vararg args: Any?): CompletableFuture<Any?> {
        return CompletableFuture.supplyAsync { eval(name, *args) }
    }

    /**
     * 异步执行脚本（命名参数）
     */
    fun evalAsync(name: String, vars: Map<String, Any?>, sender: CommandSender? = null): CompletableFuture<Any?> {
        return CompletableFuture.supplyAsync { eval(name, vars, sender) }
    }

    // ==================== 内部执行 ====================

    private fun execute(entry: ScriptEntry, vars: MutableMap<String, Any?>, sender: CommandSender?): Any? {
        devLog("Executing script '${entry.source}/${entry.script.take(50)}' type=${entry.type} vars=$vars")
        return try {
            when (entry.type) {
                ScriptType.GRAALJS -> GraalJsUtil.directEval(entry.script, vars)
                ScriptType.JAVASCRIPT -> evalStandardJs(entry.script, vars)
                ScriptType.KETHER -> {
                    KetherShell.eval(entry.script, ScriptOptions.builder().apply {
                        if (sender != null) sender(sender)
                        vars(vars)
                    }.build()).get()
                }
                ScriptType.FLUXON -> FluxonShell.invoke(entry.script, useCache = true) {
                    root.rootVariables += vars
                }
                ScriptType.JEXL -> evalJexlScript(entry.script, vars)
            }
        } catch (e: Exception) {
            severeS("Error executing script '${entry.source}/${entry.script.take(50)}...': ${e.message}")
            e.printStackTrace()
            null
        }
    }

    private fun evalStandardJs(script: String, vars: Map<String, Any?>): Any? {
        val engine = engineManager.getEngineByName("js") ?: run {
            severeS("JavaScript engine not available (Nashorn/GraalJS JSR 223)")
            return null
        }
        val bindings = engine.createBindings()
        vars.forEach { (k, v) -> bindings[k] = v }
        return engine.eval(script, bindings)
    }

    private fun evalJexlScript(script: String, vars: Map<String, Any?>): Any? {
        try {
            val compiled = JexlUtil.prodJexlCompiler.compileToScript(script)
            return compiled.eval(vars)
        } catch (e: Exception) {
            severeS("JEXL evaluation error: ${e.message}")
            return null
        }
    }
}
