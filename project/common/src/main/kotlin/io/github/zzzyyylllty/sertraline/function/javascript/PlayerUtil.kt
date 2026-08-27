package io.github.zzzyyylllty.sertraline.function.javascript

import com.github.retrooper.packetevents.protocol.dialog.input.Input
import io.github.zzzyyylllty.sertraline.compat.PlatformCompat
import io.github.zzzyyylllty.sertraline.function.kether.evalKether
import io.github.zzzyyylllty.sertraline.function.kether.evalKetherValue
import io.github.zzzyyylllty.sertraline.item.adapter.transferBooleanToByte
import io.github.zzzyyylllty.sertraline.item.rebuildLore
import io.github.zzzyyylllty.sertraline.logger.severeS
import io.github.zzzyyylllty.sertraline.util.minimessage.mmUtil
import io.github.zzzyyylllty.sertraline.util.toBooleanTolerance
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import taboolib.common.platform.function.submit
import taboolib.module.kether.KetherShell
import taboolib.module.nms.ItemTag
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag

object PlayerUtil {
    fun addPotionEffect(player: Player, type: String, duration: Int = 30, amplifier: Int = 0, ambient: Boolean = true, particles: Boolean = true, icon: Boolean = true) {
        // the 6-boolean-param constructor is 1.13+; v11200 only has the (..., Color) 6-param variant; unify via PlatformCompat (downgrades to 5/4 params on low versions)
        submit {
            PlatformCompat.addPotionEffect(player, type, duration, amplifier, ambient, particles, icon)
        }
    }
    fun addPotionEffect(player: Player, type: String, duration: Int = 30, amplifier: Int = 0) {
        submit {
            player.addPotionEffect(
                PotionEffect(
                    PotionEffectType.getByName(type)!!,
                    duration,
                    amplifier
                )
            )
        }
    }

    fun parsePlaceholders(player: Player, string: String): String {
        return PlaceholderAPI.setPlaceholders(player, string)
    }

    fun parseKether(player: Player, string: String, vars: Map<String, Any?>): Any? {
        return string.evalKetherValue(player, vars)
    }

    fun parseKetherList(player: Player, string: List<String>, vars: Map<String, Any?>): Any? {
        return string.evalKether(player, vars).get()
    }

    fun removePotionEffect(player: Player, type: String) {
        submit {
            player.removePotionEffect(
                PotionEffectType.getByName(type)!!
            )
        }
    }

    // unified message interface: Paper's CraftPlayer has a sendMessage(Component) overload,
    // Spigot only has the String overload, so calling player.sendMessage(mmUtil.deserialize(...)) directly from a script throws NoSuchMethod.
    // Always go through PlatformCompat for consistent behavior on both platforms.
    fun sendMessage(player: Player, component: Component) {
        PlatformCompat.sendComponent(player, component)
    }

    fun sendActionBar(player: Player, component: Component) {
        PlatformCompat.sendActionBar(player, component)
    }

    fun showTitle(player: Player, title: Component, subTitle: Component, durationIn: Int = 30, duration: Int = 30, durationOut: Int = 30) {
        PlatformCompat.sendTitle(player, title, subTitle, durationIn, duration, durationOut)
    }

    // -- Component / String / AsComponent triple interface (GraalJS does not resolve same-named overloads; disambiguate with suffixes; original methods stay unchanged) --

    /** Sends an adventure [Component] message directly (explicitly named version of sendMessage). */
    fun sendMessageComponent(player: Player, component: Component) {
        sendMessage(player, component)
    }

    /** Sends a legacy §-formatted string via Bukkit's old `player.sendMessage(String)` (not Component path). */
    fun sendMessageString(player: Player, string: String) {
        player.sendMessage(string)
    }

    /** Sends a MiniMessage-formatted string, equivalent to `sendMessage(player, mmUtil.deserialize(string))`. */
    fun sendMessageAsComponent(player: Player, string: String) {
        sendMessage(player, mmUtil.deserialize(string))
    }

    /** Sends an adventure [Component] ActionBar directly (explicitly named version of sendActionBar). */
    fun sendActionBarComponent(player: Player, component: Component) {
        sendActionBar(player, component)
    }

    /** Sends a legacy §-formatted string ActionBar via PlatformCompat (not Component path). */
    fun sendActionBarString(player: Player, string: String) {
        PlatformCompat.sendActionBarString(player, string)
    }

    /** Sends a MiniMessage-formatted string ActionBar, equivalent to `sendActionBar(player, mmUtil.deserialize(string))`. */
    fun sendActionBarAsComponent(player: Player, string: String) {
        sendActionBar(player, mmUtil.deserialize(string))
    }

    /** Sends an adventure [Component] Title directly (explicitly named version of showTitle). */
    fun showTitleComponent(
        player: Player,
        title: Component,
        subTitle: Component,
        durationIn: Int = 30,
        duration: Int = 30,
        durationOut: Int = 30
    ) {
        showTitle(player, title, subTitle, durationIn, duration, durationOut)
    }

    /** Sends a legacy §-formatted string Title via Bukkit's old `player.sendTitle(String, String, ...)` (not Component path). */
    fun showTitleString(
        player: Player,
        title: String,
        subTitle: String,
        durationIn: Int = 30,
        duration: Int = 30,
        durationOut: Int = 30
    ) {
        player.sendTitle(title, subTitle, durationIn, duration, durationOut)
    }

    /** Sends a MiniMessage-formatted string Title, equivalent to `showTitle(player, mmUtil.deserialize(title), mmUtil.deserialize(subTitle), ...)`. */
    fun showTitleAsComponent(
        player: Player,
        title: String,
        subTitle: String,
        durationIn: Int = 30,
        duration: Int = 30,
        durationOut: Int = 30
    ) {
        showTitle(player, mmUtil.deserialize(title), mmUtil.deserialize(subTitle), durationIn, duration, durationOut)
    }
}