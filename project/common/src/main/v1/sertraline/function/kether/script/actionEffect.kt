package io.github.zzzyyylllty.sertraline.function.kether.script

import ink.ptms.zaphkiel.impl.feature.getCurrentDurability
import ink.ptms.zaphkiel.impl.feature.kether.itemStream
import io.github.zzzyyylllty.sertraline.data.DepazItemInst
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.function.kether.getScriptItem
import net.kyori.adventure.audience.Audience
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import taboolib.common.platform.function.submit
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.actionNow
import taboolib.module.kether.script
import taboolib.module.kether.scriptParser
import taboolib.module.kether.switch
import java.util.concurrent.CompletableFuture


/**
 * effect give SPEED 10 10
 *
 * Code from [ink.ptms.zaphkiel.impl.feature.kether.ActionPotion]
 */
@KetherParser(["effect","effects"], shared = true)
fun actionEffect() = scriptParser {
    it.switch {
        case("give","g","add") { GiveEff(it.nextParsedAction(), it.nextParsedAction(), it.nextParsedAction()) }
        case("remove","rem","r") { RemoveEff(it.nextParsedAction()) }
        case("clear","c") { ClearEff() }
    }
}

class GiveEff(val name: ParsedAction<*>, val duration: ParsedAction<*>, val amplifier: ParsedAction<*>) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val viewer = frame.script().sender?.castSafely<Player>() ?: error("No player selected.")
        frame.run(name).str { name ->
            frame.run(duration).int { duration ->
                frame.run(amplifier).int { amplifier ->
                    val effectType = PotionEffectType.getByName(name.uppercase())
                    if (effectType != null) {
                        submit { viewer.addPotionEffect(PotionEffect(effectType, duration, amplifier)) }
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }
}

class RemoveEff(val name: ParsedAction<*>) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val viewer = frame.script().sender?.castSafely<Player>() ?: error("No player selected.")
        frame.run(name).str { name ->
            val effectType = PotionEffectType.getByName(name.uppercase())
            if (effectType != null) {
                submit { viewer.removePotionEffect(effectType) }
            }
        }
        return CompletableFuture.completedFuture(null)
    }
}

class ClearEff : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val viewer = frame.script().sender?.castSafely<Player>() ?: error("No player selected.")
        submit { viewer.activePotionEffects.toList().forEach { viewer.removePotionEffect(it.type) } }
        return CompletableFuture.completedFuture(null)
    }
}