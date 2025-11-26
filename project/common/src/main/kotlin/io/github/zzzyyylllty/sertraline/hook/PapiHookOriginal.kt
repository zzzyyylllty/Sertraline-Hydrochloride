package io.github.zzzyyylllty.sertraline.hook

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.jexlScriptCache
import io.github.zzzyyylllty.sertraline.Sertraline.jsScriptCache
import io.github.zzzyyylllty.sertraline.function.fluxon.FluxonShell
import io.github.zzzyyylllty.sertraline.function.kether.evalKether
import io.github.zzzyyylllty.sertraline.util.JexlUtil.prodJexlCompiler
import io.github.zzzyyylllty.sertraline.util.serialize.generateHash
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.util.random
import taboolib.common5.compileJS
import taboolib.platform.BukkitPlugin
import java.lang.String
import javax.script.SimpleBindings
import kotlin.Boolean
import kotlin.collections.set

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
        return "1.0.0"
    }

    override fun persist(): Boolean {
        return true //
    }

    override fun onRequest(player: OfflinePlayer?, params: kotlin.String): kotlin.String? {
        val data = mapOf("player" to player)
        if (params.startsWith("kether:") || params.startsWith("ke:")) {
            return params.removePrefix("kether:").removePrefix("ke:").evalKether(player as? CommandSender?, data)?.get().toString()

        } else if (params.startsWith("javascript:") || params.startsWith("js:")) {
            val javaScript = params.removePrefix("javascript:").removePrefix("js:")

            return javaScript?.let {
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
            }.toString()

        } else if (params.startsWith("jexl:") || params.startsWith("je:")) {
            val jexl = params.removePrefix("jexl:").removePrefix("je:")

            return jexl?.let {
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
            }.toString()

        } else if (params.startsWith("fluxon:") || params.startsWith("fl:")) {
            return params.removePrefix("fluxon:").removePrefix("fl:").let {
                FluxonShell.invoke(it) {
                    root.rootVariables += data
                }
            }.toString()

        } else if (params.startsWith("config:")) {
            return config[params.removePrefix("config:")].toString()

        } else if (params.startsWith("random:") || params.startsWith("rand:")) {
            val param = params.removePrefix("random:").removePrefix("rand:")
            val split = param.split("to")
            return run {
                if (split.toString().contains(".")) random(split[0].toDouble(), split[1].toDouble()) else random(
                    split[0].toInt(),
                    split[1].toInt()
                )
            }.toString()

        }
        return null
    }
}