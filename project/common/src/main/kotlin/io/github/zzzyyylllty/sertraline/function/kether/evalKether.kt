package io.github.zzzyyylllty.sertraline.function.kether

import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.Sertraline.ketherScriptCache
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptCommandSender
import taboolib.common5.Coerce
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.KetherFunction
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.ScriptOptions
import taboolib.module.kether.script
import java.util.concurrent.CompletableFuture
import kotlin.String

/**
 * Evaling script
 *
 * From [TabooLib DOC](https://taboolib.feishu.cn/wiki/RYDWwwT4ZiOpsakCEyRcXWOjnu1)
 * */

fun runKether(script: List<String>, sender: CommandSender): CompletableFuture<Any> {
    return KetherShell.eval(
        script, options = ScriptOptions(
            sender = adaptCommandSender(sender)
        )
    ).thenApply { it }
}

fun ScriptFrame.getBukkitPlayer(name: ParsedAction<*>? = null): Player {
    val player = name?.let { Bukkit.getPlayerExact(this.newFrame(name).run<String>().get()) }
        ?: script().sender?.castSafely<Player>()
        ?: error("No player selected.")
    return player
}


/** 内联脚本 */
fun List<String>.parseKether(
    player: CommandSender?,
    vars: Map<String, Any?> = mapOf(),
    sets: List<Pair<String, Any?>> = emptyList(),
    cacheId: String? = null
): List<String> {
    if (this.isEmpty()) {
        return listOf("")
    }
    return KetherFunction.parse(this, ScriptOptions.builder().apply {
        sender(player ?: console)
        vars(vars)
        sets.forEach {
            set(it.first, it.second)
        }
        if (cacheId != null) ketherScriptCache[cacheId]?.let { cache(it) }
    }.build())
}

/** 内联脚本 */
fun String?.parseKether(
    player: CommandSender?,
    vars: Map<String, Any?> = mapOf(),
    sets: List<Pair<String, Any?>> = emptyList(),
    cacheId: String? = null
): String {
    if (this.isNullOrBlank()) {
        return ""
    }
    return KetherFunction.parse(this, ScriptOptions.builder().apply {
        sender(player ?: console)
        vars(vars)
        sets.forEach {
            set(it.first, it.second)
        }
        if (cacheId != null) ketherScriptCache[cacheId]?.let { cache(it) }
    }.build())
}

fun List<String>.evalKether(
    player: CommandSender?,
    vars: Map<String, Any?> = mapOf(),
    sets: List<Pair<String, Any?>> = emptyList(),
    cacheId: String? = null
): CompletableFuture<Any?> {
    if (isEmpty()) {
        val future = CompletableFuture<Any?>()
        future.complete(null)
        return future
    }
    if (size == 1) {
        return this[0].evalKether(player, vars, sets)
    }
    return KetherShell.eval(this, ScriptOptions.builder().apply {
        sender(player ?: console)
        vars(vars)
        sets.forEach {
            set(it.first, it.second)
        }
        if (cacheId != null) ketherScriptCache[cacheId]?.let { cache(it) }
    }.build())
}

fun String?.evalKether(
    player: CommandSender?,
    vars: Map<String, Any?> = mapOf(),
    sets: List<Pair<String, Any?>> = emptyList(),
    cacheId: String? = null
): CompletableFuture<Any?> {
    if (this.isNullOrBlank()) {
        val future = CompletableFuture<Any?>()
        future.complete(null)
        return future
    }
    return KetherShell.eval(this, ScriptOptions.builder().apply {
        sender(player ?: console)
        vars(vars)
        sets.forEach {
            set(it.first, it.second)
        }
        if (cacheId != null) ketherScriptCache[cacheId]?.let { cache(it) }
    }.build())
}

fun String?.evalKetherValue(
    player: CommandSender?,
    vars: Map<String, Any?> = mapOf(),
    sets: List<Pair<String, Any?>> = emptyList(),
    def: Any = "null",
    cacheId: String? = null
): Any? {
    if (this.isNullOrBlank()) {
        val future = CompletableFuture<Any?>()
        future.complete(null)
        return future
    }
    return KetherShell.eval(this, ScriptOptions.builder().apply {
        sender(player ?: console)
        vars(vars)
        sets.forEach {
            set(it.first, it.second)
        }
        if (cacheId != null) ketherScriptCache[cacheId]?.let { cache(it) }
    }.build()).getNow(def)
}

fun String?.evalKetherString(
    player: CommandSender?,
    vars: Map<String, Any?> = mapOf(),
    sets: List<Pair<String, Any?>> = emptyList(),
    def: Any = "null",
    cacheId: String? = null
): String? {
    if (this.isNullOrBlank()) {
        val future = CompletableFuture<Any?>()
        future.complete(null)
        return future.toString()
    }
    return KetherShell.eval(this, ScriptOptions.builder().apply {
        sender(player ?: console)
        vars(vars)
        sets.forEach {
            set(it.first, it.second)
        }
        if (cacheId != null) ketherScriptCache[cacheId]?.let { cache(it) }
    }.build()).getNow(def).toString()
}

fun String?.evalKetherBoolean(
    player: CommandSender?,
    vars: Map<String, Any?> = mapOf(),
    sets: List<Pair<String, Any?>> = emptyList(),
    def: Boolean = true,
    cacheId: String? = null
): Boolean {
    if (this.isNullOrBlank()) {
        return def
    }

    /*
     * 预处理
     * 条件写 true / false 将直接绕过 Kether
     */
    if (this == "true") return true
    if (this == "false") return false

    return try {
        KetherShell.eval(this, ScriptOptions.builder().apply {
            sender(player ?: console)
            vars(vars)
            sets.forEach {
                set(it.first, it.second)
            }
            if (cacheId != null) ketherScriptCache[cacheId]?.let { cache(it) }
        }.build()).thenApply {
            Coerce.toBoolean(it)
        }.get()
    } catch (_: Exception) {
        def
    }
}

/**
 * @param all 是否全部条件通过
 */
fun List<String>.evalKetherBoolean(
    player: CommandSender?,
    vars: Map<String, Any?> = mapOf(),
    sets: List<Pair<String, Any?>> = emptyList(),
    def: Boolean = true,
    /**
     * 列表是否所有条件通过
     */
    all: Boolean = true,
    cacheId: String? = null
): Boolean {
    if (this.isEmpty()) {
        return def
    }
    val condition = if (all) {
        if (this.all { it == "true" }) {
            return true
        }
        if (this.all { it == "false" }) {
            return false
        }
        "all"
    } else {
        if (this.any { it == "true" }) {
            return true
        }
        "any"
    } + "[ " + this.joinToString("\n") + " ]"
    return try {
        KetherShell.eval(condition, ScriptOptions.builder().apply {
            sender(player ?: console)
            vars(vars)
            sets.forEach {
                set(it.first, it.second)
            }
            if (cacheId != null) ketherScriptCache[cacheId]?.let { cache(it) }
        }.build()).thenApply {
            Coerce.toBoolean(it)
        }.get()
    } catch (_: Exception) {
        def
    }
}
