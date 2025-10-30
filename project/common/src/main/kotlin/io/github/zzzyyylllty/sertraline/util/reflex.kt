package io.github.zzzyyylllty.sertraline.util

import io.github.zzzyyylllty.sertraline.logger.severeS
import net.momirealms.craftengine.core.util.ReflectionUtils
import taboolib.library.reflex.Reflex
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier

val ref = Reflex
val MC_PREFIX = "net.minecraft."
val CB_PREFIX = "org.bukkit.craftbukkit."

fun assembleMCClass(className: String): String {
    return MC_PREFIX + className
}

fun assembleCBClass(className: String): String {
    // 因为是paper 1.21.4+所以说不考虑v1_x_R_x
    return CB_PREFIX + className
}

fun getClazz(className: String): Class<*>? {
    return try {
        Class.forName(className)
    } catch (e: Throwable) {
        null
    }
}

fun getDeclaredField(clazz: Class<*>, type: Class<*>, index: Int): Field? =
    clazz.declaredFields
        .filter { it.type == type }
        .getOrNull(index)
        ?.apply { isAccessible = true }

fun getDeclaredField(clazz: Class<*>, name: String): Field? =
    runCatching {
        clazz.getDeclaredField(name).apply { isAccessible = true }
    }.getOrNull()

fun getStaticMethod(
    clazz: Class<*>,
    returnType: Class<*>,
    vararg parameterTypes: Class<*>
): Method? {
    outer@ for (method in clazz.methods) {
        if (method.parameterCount != parameterTypes.size) continue
        if (!Modifier.isStatic(method.modifiers)) continue

        val types = method.parameterTypes
        for (i in types.indices) {
            if (types[i] != parameterTypes[i]) continue@outer
        }

        if (returnType.isAssignableFrom(method.returnType)) {
            method.isAccessible = true
            return method
        }
    }
    return null
}

fun getMethod(
    clazz: Class<*>,
    returnType: Class<*>,
    index: Int,
    vararg parameterTypes: Class<*>
): Method? =
    clazz.methods
        .filter { method ->
            method.parameterCount == parameterTypes.size &&
                    method.parameterTypes.contentEquals(parameterTypes) &&
                    returnType.isAssignableFrom(method.returnType)
        }
        .getOrNull(index)
