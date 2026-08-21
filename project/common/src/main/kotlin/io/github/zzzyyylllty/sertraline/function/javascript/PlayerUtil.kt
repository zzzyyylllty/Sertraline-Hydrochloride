package io.github.zzzyyylllty.sertraline.function.javascript

import com.github.retrooper.packetevents.protocol.dialog.input.Input
import io.github.zzzyyylllty.sertraline.compat.PlatformCompat
import io.github.zzzyyylllty.sertraline.function.kether.evalKether
import io.github.zzzyyylllty.sertraline.function.kether.evalKetherValue
import io.github.zzzyyylllty.sertraline.item.adapter.transferBooleanToByte
import io.github.zzzyyylllty.sertraline.item.rebuildLore
import io.github.zzzyyylllty.sertraline.logger.severeS
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
        // 6 布尔参构造器是 1.13+，v11200 只有 (…, Color) 6 参版；统一走 PlatformCompat（低版本降级 5/4 参）
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

    // 统一消息接口：Paper 的 CraftPlayer 有 sendMessage(Component) 重载，
    // Spigot 只有 String 重载，脚本直接 player.sendMessage(mmUtil.deserialize(...)) 会 NoSuchMethod。
    // 一律经 PlatformCompat，双平台行为一致。
    fun sendMessage(player: Player, component: Component) {
        PlatformCompat.sendComponent(player, component)
    }

    fun sendActionBar(player: Player, component: Component) {
        PlatformCompat.sendActionBar(player, component)
    }

    fun showTitle(player: Player, title: Component, subTitle: Component, durationIn: Int = 30, duration: Int = 30, durationOut: Int = 30) {
        PlatformCompat.sendTitle(player, title, subTitle, durationIn, duration, durationOut)
    }
}