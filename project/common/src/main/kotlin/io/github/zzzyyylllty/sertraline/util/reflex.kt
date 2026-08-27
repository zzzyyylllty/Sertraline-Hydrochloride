package io.github.zzzyyylllty.sertraline.util

import io.github.zzzyyylllty.sertraline.logger.severeS
import io.github.zzzyyylllty.sertraline.logger.severeSSync
import taboolib.common.ClassAppender
import taboolib.library.reflex.Reflex
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier

val ref = Reflex
const val MC_PREFIX = "net.minecraft."
const val CB_PREFIX = "org.bukkit.craftbukkit."

fun assembleMCClass(className: String): String {
    return MC_PREFIX + className
}

fun assembleCBClass(className: String): String {
    // Because this is Paper 1.21.4+, so no need to consider v1_x_R_x
    return CB_PREFIX + className
}


val holderClass by lazy { getClazz("net.minecraft.core.Holder") }

fun unwrapValue(obj: Any): Any {
    if (obj is java.util.Optional<*>) {
        if (obj.isPresent) return unwrapValue(obj.get())
        throw IllegalArgumentException("Optional empty")
    }
    if (holderClass.isInstance(obj)) {
        val methodNameCandidates = listOf("get", "value")
        val getMethod = methodNameCandidates.asSequence()
            .mapNotNull {
                try {
                    holderClass.getDeclaredMethod(it)
                } catch (_: NoSuchMethodException) {
                    null
                }
            }
            .firstOrNull()

        if (getMethod != null) {
            getMethod.isAccessible = true
            return unwrapValue(getMethod.invoke(obj)!!)
        } else {
            throw IllegalStateException("No suitable get method found on Holder class")
        }
    }
    return obj
}


fun getClazz(className: String): Class<*> {
    return try {
        Class.forName(className)
    } catch (e: Throwable) {
        severeSSync("GetClazz for $className failed. Stacktrace: $e")
        throw e
    }
}

// ============ Dual-platform NMS reflection ============
// The Paper server uses Mojang mappings (net.minecraft.resources.ResourceLocation, etc.);
// the Spigot server uses Bukkit/Spigot mappings (Mojang package names + CraftBukkit 1.16 legacy class names +
// proguard-obfuscated short method/field names). The utilities below resolve "Paper names first, Spigot names as fallback".

/** Tries each candidate class name in order, returning the first that loads successfully; throws if all fail */
fun getClazzCompat(vararg candidates: String): Class<*> {
    for (candidate in candidates) {
        try {
            return Class.forName(candidate)
        } catch (_: ClassNotFoundException) {
        }
    }
    val message = "None of these classes exist: ${candidates.joinToString(", ")}"
    severeSSync("GetClazz failed. $message")
    throw ClassNotFoundException(message)
}

/** Name first; on failure, match by parameter signature (Spigot method names are obfuscated short names) */
fun getDeclaredMethodCompat(
    clazz: Class<*>,
    name: String,
    returnType: Class<*>? = null,
    vararg paramTypes: Class<*>,
): Method = getDeclaredMethodCompat(clazz, listOf(name), returnType, *paramTypes)

/**
 * Tries candidate names in order (Paper names first, Spigot obfuscated short names last); if all fail,
 * matches by "parameter signature + return type" (excluding constructors).
 * returnType is used for disambiguation: on Spigot multiple methods may share the same parameter signature
 * (e.g. IRegistry's getValue/getOptional/get all take a ResourceLocation), so the return type is needed to
 * distinguish them precisely.
 */
fun getDeclaredMethodCompat(
    clazz: Class<*>,
    names: List<String>,
    returnType: Class<*>? = null,
    vararg paramTypes: Class<*>,
): Method {
    for (name in names) {
        runCatching {
            val method = clazz.getDeclaredMethod(name, *paramTypes)
            method.isAccessible = true
            return method
        }
    }
    val matched = clazz.declaredMethods.firstOrNull { m ->
        m.name != "<init>" && m.name != "equals" && m.name != "hashCode" && m.name != "toString" &&
            (returnType == null || m.returnType == returnType) &&
            m.parameterTypes.contentEquals(paramTypes)
    }
    if (matched != null) {
        matched.isAccessible = true
        return matched
    }
    throw NoSuchMethodException(
        "$names(${paramTypes.joinToString { it.name }}) in ${clazz.name}"
    )
}

/** Tries each candidate field name in order, then matches by field type on failure (Spigot field names are obfuscated short names) */
fun getDeclaredFieldCompat(
    clazz: Class<*>,
    vararg names: String,
    type: Class<*>? = null,
): Field {
    for (name in names) {
        runCatching {
            val field = clazz.getDeclaredField(name)
            field.isAccessible = true
            return field
        }
    }
    if (type != null) {
        val matched = clazz.declaredFields.firstOrNull { type.isAssignableFrom(it.type) }
        if (matched != null) {
            matched.isAccessible = true
            return matched
        }
    }
    throw NoSuchFieldException("$names in ${clazz.name}")
}

/**
 * The full craftbukkit class name, compatible with Spigot that carries a version suffix (org.bukkit.craftbukkit.v1_21_R3.*).
 * Paper 1.21.4+ has no suffix; the no-suffix form is preferred, and on failure the version suffix is extracted from
 * the server implementation class package name.
 */
fun assembleCBClassCompat(className: String): String {
    val noSuffix = CB_PREFIX + className
    if (classExistsSafe(noSuffix)) return noSuffix
    val suffix = craftbukkitVersionSuffix
    if (suffix.isNotEmpty()) {
        val withSuffix = CB_PREFIX + suffix + "." + className
        if (classExistsSafe(withSuffix)) return withSuffix
    }
    return noSuffix
}

private fun classExistsSafe(name: String): Boolean =
    runCatching { Class.forName(name, false, ItemTagManager::class.java.classLoader) }.isSuccess

private val craftbukkitVersionSuffix: String by lazy {
    runCatching {
        val pkg = org.bukkit.Bukkit.getServer().javaClass.name.substringBeforeLast('.')
        if (pkg.startsWith(CB_PREFIX)) pkg.removePrefix(CB_PREFIX).substringBefore('.') else ""
    }.getOrDefault("")
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
    vararg parameterTypes: Class<*>,
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
    vararg parameterTypes: Class<*>,
): Method? =
    clazz.methods
        .filter { method ->
            method.parameterCount == parameterTypes.size &&
                    method.parameterTypes.contentEquals(parameterTypes) &&
                    returnType.isAssignableFrom(method.returnType)
        }
        .getOrNull(index)

fun isClassExistsSafety(path: String?): Boolean {
    return try {
        ClassAppender.isExists(path)
    } catch (ignored: ClassNotFoundException) {
        false
    } catch (ignored: NoClassDefFoundError) {
        false
    }
}