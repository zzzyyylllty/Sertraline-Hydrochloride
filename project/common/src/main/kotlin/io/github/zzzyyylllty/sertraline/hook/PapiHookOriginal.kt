package io.github.zzzyyylllty.sertraline.hook

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.gjsScriptCache
import io.github.zzzyyylllty.sertraline.Sertraline.jexlScriptCache
import io.github.zzzyyylllty.sertraline.Sertraline.jsScriptCache
import io.github.zzzyyylllty.sertraline.data.defaultData
import io.github.zzzyyylllty.sertraline.function.fluxon.FluxonShell
import io.github.zzzyyylllty.sertraline.function.kether.evalKether
import io.github.zzzyyylllty.sertraline.util.GraalJsUtil
import io.github.zzzyyylllty.sertraline.util.JexlUtil.prodJexlCompiler
import io.github.zzzyyylllty.sertraline.util.data.DataUtil
import io.github.zzzyyylllty.sertraline.util.serialize.generateHash
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.pluginVersion
import taboolib.common.util.random
import taboolib.common5.compileJS
import taboolib.expansion.getDataContainer
import taboolib.platform.BukkitPlugin
import javax.script.SimpleBindings
import kotlin.Boolean
import kotlin.collections.set
import kotlin.math.round
import kotlin.math.roundToInt

@Awake(LifeCycle.ENABLE)
fun registerPapi() {
    PapiHookOriginal(BukkitPlugin.getInstance()).register()
}


class PapiHookOriginal(plugin: BukkitPlugin) : PlaceholderExpansion() {
    private val plugin: BukkitPlugin

    init {
        this.plugin = plugin
    }

    override fun getAuthor(): kotlin.String {
        return "AkaCandyKAngel"
    }

    override fun getIdentifier(): kotlin.String {
        return "sertraline"
    }

    override fun getVersion(): kotlin.String {
        return pluginVersion
    }

    override fun persist(): Boolean {
        return true //
    }

    override fun onRequest(player: OfflinePlayer?, paramRaw: kotlin.String): kotlin.String? {
        
        val split = paramRaw.split("?:")
        val def = if (split.size >= 2) split.last() else null
        val params = if (def != null) paramRaw.removeSuffix("?:$def") else paramRaw
        
        
        if (params.startsWith("kether:") || params.startsWith("ke:")) {
            val data = mutableMapOf<String, Any?>()
            data.putAll(defaultData)
            data["player"] = player
            return params.removePrefix("kether:").removePrefix("ke:").evalKether(player as? CommandSender?, data).get().defaultValue(def)

        } else if (params.startsWith("javascript:") || params.startsWith("js:")) {
            val data = mutableMapOf<String, Any?>()
            data.putAll(defaultData)
            data["player"] = player
            val javaScript = params.removePrefix("javascript:").removePrefix("js:")

            return javaScript.let {
                val hash = it.generateHash()
                val cache = jsScriptCache[hash]
                if (cache != null) {
                    cache.eval(SimpleBindings(data))
                } else {
                    val compiled = it.compileJS()
                    compiled?.let { it ->
                        jsScriptCache[hash] = it
                        it.eval(SimpleBindings(data))
                    }
                }
            }.defaultValue(def)

        } else if (params.startsWith("jexl:") || params.startsWith("je:")) {
            val data = mutableMapOf<String, Any?>()
            data.putAll(defaultData)
            data["player"] = player
            val jexl = params.removePrefix("jexl:").removePrefix("je:")

            return jexl.let {
                val hash = it.generateHash()
                val cache = jexlScriptCache[hash]
                if (cache != null) {
                    cache.eval(data)
                } else {
                    val compiled = prodJexlCompiler.compileToScript(it)
                    compiled.let { it ->
                        jexlScriptCache[hash] = it
                        it.eval(data)
                    }
                }
            }.defaultValue(def)

        } else if (params.startsWith("graaljs:") || params.startsWith("gjs:")) {
            val data = mutableMapOf<String, Any?>()
            data.putAll(defaultData)
            data["player"] = player
            val graaljs = params.removePrefix("graaljs:").removePrefix("gjs:")

            graaljs?.let {
                GraalJsUtil.cachedEval(it, data)
            }

        } else if (params.startsWith("fluxon:") || params.startsWith("fl:")) {
            val data = mutableMapOf<String, Any?>()
            data.putAll(defaultData)
            data["player"] = player
            return params.removePrefix("fluxon:").removePrefix("fl:").let {
                FluxonShell.invoke(it) {
                    root.rootVariables += data
                }
            }.defaultValue(def)

        } else if (params.startsWith("config:")) {
            return config[params.removePrefix("config:")].defaultValue(def)

        } else if (params.startsWith("random:") || params.startsWith("rand:")) {
            val param = params.removePrefix("random:").removePrefix("rand:")
            val split = param.split("to")
            return run {
                if (split.defaultValue(def).contains(".")) random(split[0].toDouble(), split[1].toDouble()) else random(
                    split[0].toInt(),
                    split[1].toInt()
                )
            }.defaultValue(def)

        } else if (params.startsWith("data:")) {
            val param = params.removePrefix("data:")
            return run {
                player?.player?.let { DataUtil.getDataRaw(it, param) }
            }.defaultValue(def)
        } else if (params.startsWith("data1:")) {
            val param = params.removePrefix("data1:")
            return run {
                player?.player?.let { round((DataUtil.getDataAsDouble(it, param) ?: 0.0)*1000 / 100.0)/10 }
            }.defaultValue(def)
        } else if (params.startsWith("data2:")) {
            val param = params.removePrefix("data2:")
            return run {
                player?.player?.let { round((DataUtil.getDataAsDouble(it, param) ?: 0.0)*1000 / 10.0)/100 }
            }.defaultValue(def)
        } else if (params.startsWith("data1a:")) {
            val param = params.removePrefix("data1a:")
            return run {
                player?.player?.let { round((DataUtil.getDataAsDouble(it, param) ?: 0.0)*1000 / 100.0)/10 }
            }.defaultValue(def).removeSuffix(".0")
        } else if (params.startsWith("data2a:")) {
            val param = params.removePrefix("data2a:")
            return run {
                player?.player?.let { round((DataUtil.getDataAsDouble(it, param) ?: 0.0)*1000 / 10.0)/100 }
            }.defaultValue(def).removeSuffix(".0")
        } else if (params.startsWith("cdleftmil:")) {
            val param = params.removePrefix("cdleftmil:")
            return run {
                player?.player?.let { DataUtil.getCooldownLeftLong(it, param) }
            }.defaultValue(def)
        } else if (params.startsWith("cdleftsec:")) {
            val param = params.removePrefix("cdleftsec:")
            return run {
                player?.player?.let { round((DataUtil.getCooldownLeftLong(it, param) ?: 0) / 1000.0).roundToInt() }
            }.defaultValue(def)
        } else if (params.startsWith("cdleftsec1:")) {
            val param = params.removePrefix("cdleftsec1:")
            return run {
                player?.player?.let { round((DataUtil.getCooldownLeftLong(it, param) ?: 0) / 100.0)/10 }
            }.defaultValue(def)
        } else if (params.startsWith("cdleftsec2:")) {
            val param = params.removePrefix("cdleftsec2:")
            return run {
                player?.player?.let { round((DataUtil.getCooldownLeftLong(it, param) ?: 0) / 10.0)/100 }
            }.defaultValue(def)
        } else if (params.startsWith("cdleftsec1a:")) {
            val param = params.removePrefix("cdleftsec1a:")
            return run {
                player?.player?.let { round((DataUtil.getCooldownLeftLong(it, param) ?: 0) / 100.0)/10 }
            }.defaultValue(def).removeSuffix(".0")
        } else if (params.startsWith("cdleftsec2a:")) {
            val param = params.removePrefix("cdleftsec2a:")
            return run {
                player?.player?.let { round((DataUtil.getCooldownLeftLong(it, param) ?: 0) / 10.0)/100 }
            }.defaultValue(def).removeSuffix(".0")
        }

        return null
    }
}

fun Any?.defaultValue(def: String?): String {
    return this?.toString() ?: def.toString()
}