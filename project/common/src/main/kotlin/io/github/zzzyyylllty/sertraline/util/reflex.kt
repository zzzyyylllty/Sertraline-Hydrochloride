package io.github.zzzyyylllty.sertraline.util

import io.github.zzzyyylllty.sertraline.logger.severeS
import net.momirealms.craftengine.core.util.ReflectionUtils
import taboolib.library.reflex.Reflex
import java.lang.reflect.Method
import java.lang.reflect.Modifier

val ref = Reflex

fun simpleReflect(className: String, method: String): Method? {
    return try {
        val registryClass = Class.forName(className)
        val method = registryClass.getMethod(method, Any::class.java, io.github.zzzyyylllty.sertraline.reflect.MCKeyClassName)
        method
    } catch (e: Exception) {
        severeS("$className#$method reflect getMethod failed.")
        e.printStackTrace()
        null
    }
}

fun simpleGetClazz(className: String): Class<*> {
    return Class.forName(className)
}

fun getStaticMethod(clazz: Class<*>, returnType: Class<*>, vararg parameterTypes: Class<*>?): Method? {
    outer@ for (method in clazz.getMethods()) {
        if (method.parameterCount != parameterTypes.size) {
            continue
        }
        if (!Modifier.isStatic(method.modifiers)) {
            continue
        }
        val types = method.parameterTypes
        for (i in types.indices) {
            if (types[i] != parameterTypes[i]) {
                continue@outer
            }
        }
        if (returnType.isAssignableFrom(method.returnType)) return ReflectionUtils.setAccessible(method)
    }
    return null
}