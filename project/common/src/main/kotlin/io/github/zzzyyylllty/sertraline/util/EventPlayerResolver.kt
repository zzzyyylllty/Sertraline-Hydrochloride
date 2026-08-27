package io.github.zzzyyylllty.sertraline.util

import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerEvent
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

/**
 * Shared utility for resolving the Player from an event via reflection.
 *
 * Used by the custom event system ([io.github.zzzyyylllty.sertraline.listener.custom.CustomEventListener])
 * and the global listener system ([io.github.zzzyyylllty.sertraline.listener.global.GlobalListenerManager]),
 * ensuring both systems resolve the player from the same event consistently, and share the getter method cache.
 */
object EventPlayerResolver {

    private val getterCache = ConcurrentHashMap<String, Method?>()

    /**
     * Prefer the "action initiator" (damager/killer/shooter/attacker), falling back to getEntity.
 * For events such as EntityDamageByEntityEvent, getEntity is the victim, so preferring the initiator avoids resolving the wrong player.
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

    /** Fallback: scan the fields of the event class and its superclasses for a value of type Player */
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
