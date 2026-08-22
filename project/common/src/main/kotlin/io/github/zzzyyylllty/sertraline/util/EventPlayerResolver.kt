package io.github.zzzyyylllty.sertraline.util

import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerEvent
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

/**
 * 从事件中反射解析 Player 的共享工具。
 *
 * 供自定义事件系统（[io.github.zzzyyylllty.sertraline.listener.custom.CustomEventListener]）
 * 与全局监听器系统（[io.github.zzzyyylllty.sertraline.listener.global.GlobalListenerManager]）复用，
 * 保证两种系统对同一事件的玩家解析行为一致，并共享 getter 方法缓存。
 */
object EventPlayerResolver {

    private val getterCache = ConcurrentHashMap<String, Method?>()

    /**
     * 优先解析「动作发起方」（damager/killer/shooter/attacker），兜底再取 getEntity。
     * 对 EntityDamageByEntityEvent 等事件，getEntity 是受害者，发起方优先可避免解析到错误玩家。
     */
    private val playerGetterNames = arrayOf(
        "getPlayer",
        "getDamager", "getKiller", "getShooter", "getAttacker",
        "getEntity",
        "getVictim",
        "getWhoClicked", "getInventoryHolder", "getEntityClicked", "getClickedEntity",
        "getEntityTarget", "getActor", "getOwner"
    )

    fun resolvePlayer(event: Event): Player? {
        if (event is PlayerEvent) return event.player
        for (name in playerGetterNames) {
            when (val v = invokeGetter(event, name)) {
                is Player -> return v
                is Projectile -> (v.shooter as? Player)?.let { return it }
            }
        }
        return scanPlayerFields(event)
    }

    fun invokeGetter(target: Any, name: String): Any? {
        val method = getterCache.computeIfAbsent("${target.javaClass.name}#$name") {
            try {
                target.javaClass.getMethod(name).takeIf { m -> m.parameterCount == 0 }
            } catch (_: Throwable) {
                null
            }
        } ?: return null
        return try {
            method.invoke(target)
        } catch (_: Throwable) {
            null
        }
    }

    /** 兜底：扫描事件类及其父类的字段，找出 Player 类型的值 */
    private fun scanPlayerFields(event: Event): Player? {
        var clazz: Class<*>? = event.javaClass
        while (clazz != null && clazz != Any::class.java) {
            for (field in clazz.declaredFields) {
                if (!Player::class.java.isAssignableFrom(field.type)) continue
                try {
                    field.isAccessible = true
                    val v = field.get(event)
                    if (v is Player) return v
                } catch (_: Throwable) {
                }
            }
            clazz = clazz.superclass
        }
        return null
    }
}
