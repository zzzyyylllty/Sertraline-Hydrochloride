package io.github.zzzyyylllty.sertraline.listener.global

import io.github.zzzyyylllty.sertraline.data.defaultData
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.debugMode.devLogBypassCheck
import io.github.zzzyyylllty.sertraline.function.kether.evalKetherBoolean
import io.github.zzzyyylllty.sertraline.logger.infoS
import io.github.zzzyyylllty.sertraline.logger.severeS
import io.github.zzzyyylllty.sertraline.logger.warningS
import io.github.zzzyyylllty.sertraline.util.ClassAliases
import io.github.zzzyyylllty.sertraline.util.EventPlayerResolver
import io.github.zzzyyylllty.sertraline.util.ScriptHelper
import io.github.zzzyyylllty.sertraline.util.ScriptHelper.ScriptType
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFolder
import taboolib.common.platform.function.submitAsync
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration
import taboolib.platform.BukkitPlugin
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * 全局监听器系统
 *
 * 从 global-listeners/ 目录加载 YAML 监听器，绑定任意 Bukkit 事件并执行脚本。
 * 与物品动作无关，适合做全局行为（如示例中的潜行切换 loreMode）。
 *
 * 配置文件格式：
 * ```
 * sample_listener:
 *   bind: "PlayerToggleSneakEvent"   # 事件类，简写或全限定名
 *   priority: NORMAL                 # 可选：LOWEST/LOW/NORMAL/HIGH/HIGHEST/MONITOR
 *   accept-cancelled: false          # 可选：是否处理已取消的事件（默认 false）
 *   async: true                      # 可选：是否异步执行脚本（默认 true）
 *   throttle: 500                    # 可选：最短触发间隔（毫秒），0/缺省 = 不限频
 *   condition:                       # 可选：Kether 条件，为 false 时跳过
 *     - "..."
 *   eval:
 *     graaljs: |
 *       ...脚本...
 *     kether:
 *       - "..."
 * ```
 *
 * eval 的键支持所有脚本引擎：graaljs / javascript(js) / kether / fluxon(fx) / jexl。
 * 运行时注入变量：player / event / cancellableEvent / listener，并合并 defaultData（DataUtil、Bukkit、SertralineAPI 等）。
 * throttle 按（监听器 + 玩家，无玩家则监听器全局）粒度限流：高频事件（如 PlayerMoveEvent）下
 * 每个窗口只放行一次，避免每个事件都触发一次 condition 求值与 async 任务派发。
 */
object GlobalListenerManager {

    data class GlobalListener(
        val name: String,
        val eventClass: Class<out Event>,
        val priority: EventPriority,
        val ignoreCancelled: Boolean,
        val async: Boolean,
        val condition: String?,
        val scripts: List<Pair<ScriptType, String>>,
        val source: String,
        /** 最短触发间隔（毫秒），<=0 表示不限频 */
        val throttle: Long,
    )

    /** 共享 Listener 实例，仅用于全局监听器注册，可整体反注册 */
    private val dummyListener: Listener = object : Listener {}

    private val registrations = mutableListOf<GlobalListener>()

    /** 节流 key -> 上次放行时间戳（ms）；Bukkit 事件在主线程触发，用并发容器防御未来可能的异步注册 */
    private val lastFire = ConcurrentHashMap<String, Long>()

    /** 加载 global-listeners/ 目录下全部监听器并注册 */
    fun registerAll() {
        unregisterAll()
        lastFire.clear()
        val dir = File(getDataFolder(), "global-listeners")
        if (!dir.exists()) {
            infoS("Global listeners directory not found, releasing default...")
            try {
                releaseResourceFolder("global-listeners")
            } catch (_: Exception) {
                dir.mkdirs()
            }
        }
        if (!dir.exists()) {
            severeS("Failed to create global-listeners directory: ${dir.absolutePath}")
            return
        }

        val plugin = BukkitPlugin.getInstance()
        var count = 0
        dir.walk()
            .filter { it.isFile && it.extension in setOf("yml", "yaml") }
            .forEach { file ->
                count += loadFile(file, plugin)
            }
        infoS("Loaded $count global listener(s) from ${dir.absolutePath}")
        devLog("Global listeners: ${registrations.map { it.name }}")
    }

    /** 反注册所有全局监听器 */
    fun unregisterAll() {
        if (registrations.isEmpty()) return
        try {
            HandlerList.unregisterAll(dummyListener)
        } catch (_: Throwable) {
        }
        registrations.clear()
    }

    private fun loadFile(file: File, plugin: BukkitPlugin): Int {
        var count = 0
        try {
            val config = Configuration.loadFromFile(file)
            for (key in config.getKeys(false)) {
                val section = config.getConfigurationSection(key) ?: continue
                try {
                    val listener = parseListener(key, section, file.name)
                    plugin.server.pluginManager.registerEvent(
                        listener.eventClass,
                        dummyListener,
                        listener.priority,
                        EventExecutor { _, event -> onEvent(listener, event) },
                        plugin,
                        listener.ignoreCancelled
                    )
                    registrations += listener
                    devLog("Registered global listener \"$key\" -> ${listener.eventClass.name} (${file.name})")
                    count++
                } catch (ex: Exception) {
                    severeS("Failed to register global listener \"$key\" (${file.name}): ${ex.message}")
                }
            }
        } catch (e: Exception) {
            severeS("Failed to load global listener file: ${file.name} - ${e.message}")
        }
        return count
    }

    private fun parseListener(key: String, section: ConfigurationSection, file: String): GlobalListener {
        val className = section.getString("bind") ?: section.getString("class") ?: section.getString("listen")
            ?: error("missing \"bind\"")
        val clazz = ClassAliases.getClass(className)
            ?: error("invalid event class \"$className\"")
        if (!Event::class.java.isAssignableFrom(clazz)) {
            error("\"$className\" is not a Bukkit Event")
        }
        @Suppress("UNCHECKED_CAST")
        val eventClass = clazz as Class<out Event>
        val priority = section.getString("priority")?.let { name ->
            EventPriority.values().firstOrNull { it.name.equals(name, true) }
        } ?: EventPriority.NORMAL
        val acceptCancelled = section.getBoolean("accept-cancelled", false)
        val async = section.getBoolean("async", true)
        val throttle = section.getLong("throttle", 0)
        val condition = parseCondition(section.get("condition"))
        val scripts = parseScripts(key, section.getConfigurationSection("eval"), file)
        return GlobalListener(key, eventClass, priority, !acceptCancelled, async, condition, scripts, file, throttle)
    }

    private fun parseCondition(raw: Any?): String? {
        return when (raw) {
            null -> null
            is String -> raw.trim().ifBlank { null }
            is List<*> -> raw.mapNotNull { it?.toString() }.joinToString("\n").trim().ifBlank { null }
            else -> raw.toString().trim().ifBlank { null }
        }
    }

    /** 解析 eval 段为「脚本类型 -> 脚本内容」列表；值可为多行字符串或行列表 */
    private fun parseScripts(key: String, eval: ConfigurationSection?, file: String): List<Pair<ScriptType, String>> {
        val scripts = mutableListOf<Pair<ScriptType, String>>()
        if (eval == null) return scripts
        for (engineKey in eval.getKeys(false)) {
            val type = try {
                ScriptType.fromString(engineKey)
            } catch (e: IllegalArgumentException) {
                warningS("Unknown script type \"$engineKey\" in global listener \"$key\" (${file}), skipping")
                continue
            }
            val raw = eval.get(engineKey)
            val script = when (raw) {
                is String -> raw.trim()
                is List<*> -> raw.mapNotNull { it?.toString() }.joinToString("\n")
                else -> null
            }?.ifBlank { null } ?: continue
            scripts += type to script
        }
        return scripts
    }

    private fun onEvent(listener: GlobalListener, event: Event) {
        // 节流放行才继续：高频事件下避免每个事件都做 condition 求值 + async 任务派发
        if (listener.throttle > 0 && !throttleAcquire(listener, event)) {
            devLogBypassCheck { "Global listener \"${listener.name}\": throttled, skipped." }
            return
        }
        val player = EventPlayerResolver.resolvePlayer(event)
        val vars = LinkedHashMap<String, Any?>()
        // 与 eval 脚本保持一致：condition 求值同样注入 defaultData（DataUtil/Bukkit/SertralineAPI 等）
        vars.putAll(defaultData)
        vars["player"] = player
        vars["event"] = event
        vars["cancellableEvent"] = event as? Cancellable
        vars["listener"] = listener.name

        val condition = listener.condition
        if (condition != null && !condition.evalKetherBoolean(player, vars, def = false)) {
            devLogBypassCheck { "Global listener \"${listener.name}\": condition not met, skipped." }
            return
        }

        devLogBypassCheck { "Firing global listener \"${listener.name}\" on ${event.eventName}" }
        if (listener.async) {
            submitAsync { runScripts(listener, vars) }
        } else {
            runScripts(listener, vars)
        }
    }

    /**
     * 按（监听器文件:监听器名 + 玩家 uuid，无玩家则仅监听器）粒度判定节流窗口。
     * 返回 true 表示本次事件放行，并把放行时间更新为 now（原子，避免同窗口并发放行）。
     */
    private fun throttleAcquire(listener: GlobalListener, event: Event): Boolean {
        val player = EventPlayerResolver.resolvePlayer(event)
        val key = if (player != null) "${listener.source}:${listener.name}:${player.uniqueId}" else "${listener.source}:${listener.name}"
        val now = System.currentTimeMillis()
        val result = lastFire.compute(key) { _, last ->
            val lastTime = last ?: 0L
            if (now - lastTime < listener.throttle) lastTime else now
        }
        return result == now
    }

    private fun runScripts(listener: GlobalListener, vars: Map<String, Any?>) {
        for ((type, script) in listener.scripts) {
            ScriptHelper.executeScript(type, script, vars)
        }
    }
}
