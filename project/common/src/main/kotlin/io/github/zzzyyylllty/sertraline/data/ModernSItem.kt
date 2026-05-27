package io.github.zzzyyylllty.sertraline.data

import com.google.gson.Gson
import ink.ptms.um.Mythic
import io.github.zzzyyylllty.sertraline.Sertraline
import io.github.zzzyyylllty.sertraline.Sertraline._api
import io.github.zzzyyylllty.sertraline.Sertraline.api
import io.github.zzzyyylllty.sertraline.Sertraline.jexlScriptCache
import io.github.zzzyyylllty.sertraline.Sertraline.jsScriptCache
import io.github.zzzyyylllty.sertraline.api.SertralineAPI
import io.github.zzzyyylllty.sertraline.config.ConfigUtil
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.event.SertralineCustomScriptDataLoadEvent
import io.github.zzzyyylllty.sertraline.function.fluxon.FluxonShell
import io.github.zzzyyylllty.sertraline.function.javascript.EventUtil
import io.github.zzzyyylllty.sertraline.function.javascript.ItemStackUtil
import io.github.zzzyyylllty.sertraline.function.javascript.PlayerUtil
import io.github.zzzyyylllty.sertraline.function.javascript.ThreadUtil
import io.github.zzzyyylllty.sertraline.function.kether.evalKether
import io.github.zzzyyylllty.sertraline.function.kether.evalKetherBoolean
import io.github.zzzyyylllty.sertraline.util.DependencyHelper
import io.github.zzzyyylllty.sertraline.util.ExternalItemHelper
import io.github.zzzyyylllty.sertraline.util.GraalJsUtil
import io.github.zzzyyylllty.sertraline.util.JexlUtil.prodJexlCompiler
import io.github.zzzyyylllty.sertraline.util.data.DataUtil
import io.github.zzzyyylllty.sertraline.util.jsonUtils
import io.github.zzzyyylllty.sertraline.util.minimessage.mmJsonUtil
import io.github.zzzyyylllty.sertraline.util.minimessage.mmLegacyAmpersandUtil
import io.github.zzzyyylllty.sertraline.util.minimessage.mmLegacySectionUtil
import io.github.zzzyyylllty.sertraline.util.minimessage.mmUtil
import io.github.zzzyyylllty.sertraline.util.serialize.generateHash
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import org.tabooproject.fluxon.Fluxon
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.submit
import taboolib.common5.compileJS
import taboolib.platform.util.bukkitPlugin
import javax.script.SimpleBindings

var defaultData = LinkedHashMap<String, Any?>()

@Awake(LifeCycle.ENABLE)
fun registerExternalData() {
    defaultData.putAll(
        linkedMapOf(
            "mmUtil" to mmUtil,
            "mmJsonUtil" to mmJsonUtil,
            "mmLegacySectionUtil" to mmLegacySectionUtil,
            "mmLegacyAmpersandUtil" to mmLegacyAmpersandUtil,
            "jsonUtils" to jsonUtils,
            "ItemStackUtil" to ItemStackUtil,
            "EventUtil" to EventUtil,
            "ThreadUtil" to ThreadUtil,
            "PlayerUtil" to PlayerUtil,
            "ExternalItemHelper" to ExternalItemHelper,
            "SertralineAPI" to _api,
            "DataUtil" to DataUtil,
            "Math" to Math::class.java,
            "System" to System::class.java,
            "Bukkit" to Bukkit::class.java,
            "Gson" to Gson::class.java,
            "bukkitPlugin" to bukkitPlugin,
            "scheduler" to Bukkit.getScheduler(),
            "SertralineObj" to Sertraline,
            "FluxonShell" to FluxonShell,
        ))
    val event = SertralineCustomScriptDataLoadEvent(defaultData)
    event.call()

    defaultData = event.defaultData
}

data class RevisionData(
    val firstBuild: Long,
    val lastUpdated: Long,
    val revisionId: Long,
    val firstBuildPlayer: String,
    val firstBuildPlayerUUID: String,
)

data class ModernSItem(
    val key: String,
    val data: LinkedHashMap<String, Any?> = linkedMapOf(),
    val config: LinkedHashMap<String, Any?> = linkedMapOf(),
) {
    fun serialize(): String? {
        return jsonUtils.toJson(this)
    }

    /**
     * 深拷贝，不使用 JSON 序列化/反序列化，性能远优于 serialize+deserialize
     */
    fun deepCopy(): ModernSItem = ModernSItem(
        key = key,
        data = deepCopyMap(data),
        config = deepCopyMap(config)
    )

    /**
     * 配置中是否包含 ${...}$ 占位符
     * 在 item 加载时延迟计算一次并缓存
     */
    @Transient
    private var _hasPlaceholders: Boolean? = null
    val hasPlaceholders: Boolean
        get() {
            if (_hasPlaceholders == null) {
                _hasPlaceholders = configContainsPlaceholders(config) || configContainsPlaceholders(data)
            }
            return _hasPlaceholders!!
        }

    /**
     * 配置中是否包含 sertraline:dynamics 动态数据
     */
    @Transient
    private var _hasDynamics: Boolean? = null
    val hasDynamics: Boolean
        get() {
            if (_hasDynamics == null) {
                _hasDynamics = getDeepData("sertraline:dynamics") != null
            }
            return _hasDynamics!!
        }


    fun getDeepData(location: String): Any? {
        val colonIndex = location.indexOf(':')

        if (colonIndex <= 0) {
            // minecraft -> data["minecraft"]
            return data[location]
        }

        // 获取主键对应的数据
        val major = location.substring(0, colonIndex)
        val majorData = data[major] as? Map<*, *> ?: return null

        // 获取section部分
        val section = location.substring(colonIndex + 1)

        return if ('.' in section) {
            // minecraft:food.saturation 深层读取
            ConfigUtil.getDeep(majorData, section)  // ✓ 正确
        } else {
            // minecraft:food 浅层读取
            majorData[section]  // ✓ 正确
        }
    }
    fun getMajorData(location: String): Any? {
        return data[location] as? Map<*,*>?
    }

    fun setDeepData(location: String, value: Any?) {
        val split = location.split(":")
        if (split.size >= 2) {
            val major = split[0]
            val section = location.removePrefix("${split[0]}:")
            val cvalue = (data[major] as? Map<*,*>?)?.toMutableMap()
            cvalue?.set(section, value)
            data[major] = cvalue
        } else data[location] = value
    }
    fun setMajorData(location: String, value: Any?) {
        data[location] = value
    }
}

fun deserializeSItem(string: String): ModernSItem {
    return jsonUtils.fromJson(string, ModernSItem::class.java)
}

/**
 * 深拷贝 LinkedHashMap
 */
@Suppress("UNCHECKED_CAST")
fun deepCopyMap(original: LinkedHashMap<String, Any?>): LinkedHashMap<String, Any?> {
    val copy = LinkedHashMap<String, Any?>(original.size)
    for ((key, value) in original) {
        copy[key] = deepCopyValue(value)
    }
    return copy
}

@Suppress("UNCHECKED_CAST")
private fun deepCopyValue(value: Any?): Any? {
    return when (value) {
        is LinkedHashMap<*, *> -> deepCopyMap(value as LinkedHashMap<String, Any?>)
        is Map<*, *> -> LinkedHashMap(value as Map<String, Any?>)
        is ArrayList<*> -> ArrayList(value.map { deepCopyValue(it) })
        is List<*> -> value.map { deepCopyValue(it) }
        is MutableList<*> -> value.map { deepCopyValue(it) }.toMutableList()
        else -> value // String, Number, Boolean, null are immutable
    }
}

/**
 * 递归检查配置中是否包含 ${...}$ 占位符
 */
fun configContainsPlaceholders(config: Map<String, Any?>): Boolean {
    for ((_, value) in config) {
        when (value) {
            is String -> {
                if (value.contains("\${")) return true
            }
            is Map<*, *> -> {
                @Suppress("UNCHECKED_CAST")
                if (configContainsPlaceholders(value as Map<String, Any?>)) return true
            }
            is List<*> -> {
                for (element in value) {
                    if (element is String && element.contains("\${")) return true
                    if (element is Map<*, *>) {
                        @Suppress("UNCHECKED_CAST")
                        if (configContainsPlaceholders(element as Map<String, Any?>)) return true
                    }
                }
            }
        }
    }
    return false
}

data class Action(
    val condition: List<String>? = null,
    val async: Boolean? = null,
    val kether: List<String>? = null,
    val javaScript: String? = null,
    val jexl: String? = null,
    val fluxon: String? = null,
    val gjs: String? = null,
    val mythic: List<String>? = null,
//    val kotlinScript: String? = null,
) {



    fun runAction(player: Player, data: Map<String, Any?>, i: ItemStack?, e: Event?, cancellableEvent: Cancellable?, sqlI: ModernSItem, abItem: ItemStack?) {

        val parsedData = data.toMutableMap()
        parsedData["sItem"] = sqlI
        parsedData["bItem"] = i
        parsedData["abItem"] = abItem
        parsedData["event"] = e
        parsedData["cancellableEvent"] = cancellableEvent
        parsedData["player"] = player
        parsedData.putAll(defaultData)

        if (condition?.evalKetherBoolean(player, parsedData) ?: true) {
            kether?.evalKether(player, parsedData)

            javaScript?.let {
                val hash = it.generateHash()
                val cache = jsScriptCache[hash]
                if (cache != null) {
                    cache.eval(SimpleBindings(parsedData))
                } else {
                    val compiled = it.compileJS()
                    compiled?.let { it ->
                        jsScriptCache[hash] = it
                        it.eval(SimpleBindings(parsedData))
                    }
                }
            }

            jexl?.let {
                val hash = it.generateHash()
                val cache = jexlScriptCache[hash]
                if (cache != null) {
                    cache.eval(parsedData)
                } else {
                    val compiled = prodJexlCompiler.compileToScript(it)
                    compiled.let { it ->
                        jexlScriptCache[hash] = it
                        it.eval(parsedData)
                    }
                }
            }

            fluxon?.let {
                FluxonShell.invoke(it) {
                    root.rootVariables += parsedData
                }
            }

            gjs?.let {
                GraalJsUtil.cachedEval(it, parsedData)
            }

            if (DependencyHelper.mm) mythic?.let {
                it.forEach { str ->
                    val filterData = parsedData.filter { (key, value) -> value != null } as Map<String, Any>
                    val skillLine = str.split("~")
                    val triggerName = skillLine.getOrNull(1)
                    val trigger = triggerName?.let { name -> Mythic.API.getSkillTrigger(name) } ?: Mythic.API.getDefaultSkillTrigger()
                    val skill = Mythic.API.getSkillMechanic(skillLine[0])
                    devLog("skill: $skill | skillLine: $skillLine | trigger: $trigger | triggerName: $triggerName")
                    skill?.execute(
                        trigger,
                        player,
                        player,
                        emptySet(),
                        emptySet(),
                        0f,
                        filterData
                    )
                }
            }

//        kotlinScript?.let {
//            runKotlinScriptJsr223(it, parsedData, bukkitPlugin::class.java.classLoader)
//        }

        }
    }
}

data class ItemData(
    val itemVal: Map<String, Any?>? = mapOf(),
    val itemVar: Map<String, Any?>? = mapOf(),
    val itemDynamic: Map<String, Any?>? = mapOf(),
    val itemId: String? = null,
) {
    fun collect(): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        itemVal?.let { map.putAll(it) }
        itemVar?.let { map.putAll(it) }
        itemDynamic?.let { map.putAll(it) }
        itemId?.let { map.put("_itemname", it) }
        return map
    }

}
