package io.github.zzzyyylllty.sertraline.config

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.loreFormats
import io.github.zzzyyylllty.sertraline.data.LineMode
import io.github.zzzyyylllty.sertraline.data.LoreElement
import io.github.zzzyyylllty.sertraline.data.LoreFormat
import io.github.zzzyyylllty.sertraline.data.LoreSetting
import io.github.zzzyyylllty.sertraline.data.LoreSettingOverride
import io.github.zzzyyylllty.sertraline.data.SwitchFork
import io.github.zzzyyylllty.sertraline.data.SwitchableLoreFormat
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.logger.infoL
import io.github.zzzyyylllty.sertraline.logger.infoS
import io.github.zzzyyylllty.sertraline.logger.severeL
import io.github.zzzyyylllty.sertraline.logger.warningL
import io.github.zzzyyylllty.sertraline.util.serialize.parseToMap
// import org.yaml.snakeyaml.Yaml
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFolder
import taboolib.common.platform.function.warning
import taboolib.common5.compileJS
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.io.File
import javax.script.CompiledScript


fun loadLoreFormatFiles() {
    infoL("LoreFormat_Load")
    if (!File(getDataFolder(), "lore-formats").exists()) {
        warningL("LoreFormat_Load_Regen")
        // 整个文件夹种子化：同时释放 loreGenerator.yml 与 switchable_example.yml 两个示例
        releaseResourceFolder("lore-formats")
    }
    val files = File(getDataFolder(), "lore-formats").listFiles() ?: run {
        warningL("LoreFormat_Load_No_Files")
        return
    }
    val regex = (config["file-load.lore-format"] ?: ".*").toString()
    // Phase 1: 加载所有普通格式，收集所有 switchable 格式
    val allSwitchable = LinkedHashMap<String, Map<String, Any?>>()
    for (file in files) {
        if (file.isDirectory) {
            file.listFiles()?.forEach { allSwitchable.putAll(loadLoreFormatFile(it, regex)) }
        } else {
            allSwitchable.putAll(loadLoreFormatFile(file, regex))
        }
    }
    // Phase 2: 全局解析所有 switchable 格式（跨文件引用现在可解析）
    loadSwitchableFormats(allSwitchable, "all files")
}
/**
 * 加载一个 lore-format 文件，加载其中的普通格式并返回 switchable 格式条目。
 * 调用方负责收集所有 switchable 条目后统一解析（支持跨文件引用）。
 */
fun loadLoreFormatFile(file: File, regex: String): Map<String, Map<String, Any?>> {
    devLog("Loading file ${file.name}")

    if (file.isDirectory) {
        val collected = LinkedHashMap<String, Map<String, Any?>>()
        file.listFiles()?.forEach { collected.putAll(loadLoreFormatFile(it, regex)) }
        return collected
    }
    if (!checkRegexMatch(file.name, regex)) {
        devLog("${file.name} not match regex, skipping...")
        return emptyMap()
    }
    val raw = multiExtensionLoader(file) ?: return emptyMap()
    val map = TemplateManager.resolveInMap(raw)
    if (map == null) return emptyMap()
    if (map.isEmpty()) {
        severeL("Config_Load_Error_Empty", file.name)
        return emptyMap()
    }
    // 普通格式先行：switchable 的 when.then 引用的目标格式必须已加载
    val normal = LinkedHashMap<String, Map<String, Any?>>()
    val switchable = LinkedHashMap<String, Map<String, Any?>>()
    map.forEach { (key, value) ->
        val m = value as? Map<String, Any?> ?: linkedMapOf()
        if (m["mode"]?.toString() == "switch") switchable[key] = m else normal[key] = m
    }
    // 单个格式解析失败只记录错误并跳过，不中断整个文件（否则其余格式与后续文件全部加载失败）
    normal.forEach { (key, m) ->
        try {
            loadLoreFormat(key, m)
        } catch (ex: Exception) {
            severeL("LoreFormat_Load_Error_Parse", key, ex.message ?: "Unknown error")
        }
    }
    return switchable
}

fun loadLoreFormat(key: String, arg: Map<String, Any?>) {
    val c = ConfigUtil
    val elementConfigs = c.getDeep(arg, "elements") as? List<*> ?: emptyList<Any?>()
    val elements = mutableListOf<LoreElement>()
    elementConfigs.forEach {
        elements.add(
            if (it is String) LoreElement(it, null)
            else if (it is Map<*,*>) LoreElement(
                it["content"].asListedStringEnhanced() ?: "",
                it["key"] as? String,
                (it["lineMode"] as? String)?.let { value -> runCatching { LineMode.valueOf(value) }.getOrNull() },
                it["lineRequire"] as? List<String>
            ) else throw IllegalArgumentException("Invaild lore format data type!")
        )
    }
    val loreFormat = LoreFormat(
        settings = LoreSetting(
            overwrite = c.getDeep(arg, "settings.overwrite") as? Boolean ?: true,
            visual = c.getDeep(arg, "settings.visual") as? Boolean ?: true,
            skipBlank = c.getDeep(arg, "settings.auto-skip-blank") as? Boolean ?: true,
        ),
        elements = elements
    )
    loreFormats[key] = loreFormat
}

/**
 * 迭代加载 switchable lore 格式：when.then 引用的目标格式必须先已加载。
 * 支持同文件普通格式，也支持 switchable 链式引用（下一轮迭代解析）。
 */
private fun loadSwitchableFormats(switchable: Map<String, Map<String, Any?>>, fileName: String) {
    // 结构损坏（无 when）直接报错移除，避免死循环
    val pending = switchable.toMutableMap()
    val pendingIt = pending.entries.iterator()
    while (pendingIt.hasNext()) {
        val (key, m) = pendingIt.next()
        val whenList = ConfigUtil.getDeep(m, "when") as? List<*>
        if (whenList.isNullOrEmpty()) {
            severeL("LoreFormat_Switch_Load_No_When", key, fileName)
            pendingIt.remove()
        }
    }
    var progressed = true
    while (progressed && pending.isNotEmpty()) {
        progressed = false
        val iterator = pending.entries.iterator()
        while (iterator.hasNext()) {
            val (key, m) = iterator.next()
            if (isSwitchableReady(m)) {
                try {
                    loadSwitchableLoreFormat(key, m)
                } catch (ex: Exception) {
                    severeL("LoreFormat_Switch_Load_Error", key, fileName, ex.message ?: "Unknown error")
                }
                iterator.remove()
                progressed = true
            }
        }
    }
    // 剩余：when.then 引用了尚未加载的格式（可能引用其他文件里更晚加载的格式）
    pending.forEach { (key, m) ->
        val missing = missingSwitchTargets(m)
        severeL("LoreFormat_Switch_Load_Unresolved", key, fileName, missing.joinToString(", "))
    }
}

private fun isSwitchableReady(arg: Map<String, Any?>): Boolean {
    val whenList = ConfigUtil.getDeep(arg, "when") as? List<*> ?: return false
    if (whenList.isEmpty()) return false
    return whenList.all { item ->
        val then = (item as? Map<*, *>)?.get("then")?.toString()
        then != null && loreFormats.containsKey(then)
    }
}

private fun missingSwitchTargets(arg: Map<String, Any?>): List<String> {
    return (ConfigUtil.getDeep(arg, "when") as? List<*>)?.mapNotNull { item ->
        val then = (item as? Map<*, *>)?.get("then")?.toString()
        then?.takeUnless { loreFormats.containsKey(it) }
    }?.distinct() ?: emptyList()
}

/**
 * 解析一个 switchable 格式：
 * - overwriteable-settings：switchable 根对分支目标 settings 的部分覆盖（仅显式声明的字段生效）
 * - pre-variables：JS 表达式预变量，编译为 [CompiledScript]，运行时求值并注入条件求值上下文
 * - when：按顺序执行条件，第一个命中分支的 then 目标为最终格式；无 if 的分支视为默认分支（编译为 true）
 */
private fun loadSwitchableLoreFormat(key: String, arg: Map<String, Any?>) {
    val c = ConfigUtil
    // overwriteable-settings 段缺失时沿用分支目标自身的 settings；存在时仅覆盖显式声明的字段
    val settingsOverride = if (c.getDeep(arg, "overwriteable-settings") != null) LoreSettingOverride(
        overwrite = c.getDeep(arg, "overwriteable-settings.overwrite") as? Boolean,
        visual = c.getDeep(arg, "overwriteable-settings.visual") as? Boolean,
        skipBlank = c.getDeep(arg, "overwriteable-settings.auto-skip-blank") as? Boolean,
    ) else null
    val settings = LoreSetting(
        overwrite = settingsOverride?.overwrite ?: true,
        visual = settingsOverride?.visual ?: true,
        skipBlank = settingsOverride?.skipBlank ?: true,
    )
    // LinkedHashMap：pre-variables 按声明顺序求值，后声明的变量可引用先声明的变量
    val preVariables = LinkedHashMap<String, CompiledScript>()
    (c.getDeep(arg, "pre-variables") as? Map<*, *>)?.forEach { (name, expr) ->
        val compiled = expr?.toString()?.trim()?.takeIf { it.isNotEmpty() }?.compileJS()
        if (compiled == null) {
            warningL("LoreFormat_Switch_Load_PreVar_Compile", key, name?.toString() ?: "?", expr?.toString() ?: "")
        } else {
            preVariables[name.toString()] = compiled
        }
    }
    val forks = mutableListOf<SwitchFork>()
    val whenList = c.getDeep(arg, "when") as? List<*> ?: emptyList<Any?>()
    for (item in whenList) {
        val m = item as? Map<*, *> ?: continue
        val thenKey = m["then"]?.toString() ?: continue
        val target = loreFormats[thenKey] ?: throw IllegalArgumentException("target \"$thenKey\" not loaded")
        val conditionCompiled = (m["if"]?.toString() ?: "true").compileJS()
        if (conditionCompiled == null) {
            warningL("LoreFormat_Switch_Load_Condition_Compile", key, thenKey, m["if"]?.toString() ?: "")
            continue
        }
        forks += SwitchFork(conditionCompiled, target)
    }
    if (forks.isEmpty()) {
        throw IllegalArgumentException("no valid fork in \"when\"")
    }
    loreFormats[key] = SwitchableLoreFormat(settings, preVariables, forks, settingsOverride)
    devLog("Loaded switchable lore format \"$key\" with ${forks.size} fork(s)")
}