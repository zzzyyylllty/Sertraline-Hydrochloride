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
    // 因为是paper 1.21.4+所以说不考虑v1_x_R_x
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

// ============ 双平台 NMS 反射 ============
// Paper 服务端是 Mojang 映射（net.minecraft.resources.ResourceLocation 等）；
// Spigot 服务端是 Bukkit/Spigot 映射（Mojang 包名 + CraftBukkit 1.16 legacy 类名 +
// proguard 混淆方法/字段短名）。以下工具按「Paper 名优先，Spigot 名回退」解析。

/** 依次尝试候选类名，返回第一个加载成功的；全部失败则抛异常 */
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

/** 名字优先，失败后按参数签名匹配（Spigot 方法名为混淆短名） */
fun getDeclaredMethodCompat(
    clazz: Class<*>,
    name: String,
    returnType: Class<*>? = null,
    vararg paramTypes: Class<*>,
): Method = getDeclaredMethodCompat(clazz, listOf(name), returnType, *paramTypes)

/**
 * 依次尝试候选名字（Paper 名在前，Spigot 混淆短名在后），
 * 全部失败后按「参数签名 + 返回类型」匹配（排除构造器）。
 * returnType 用于消歧：Spigot 上多个方法共享同一参数签名（如 IRegistry 有
 * getValue/getOptional/get 三个方法参数都是 ResourceLocation），需返回类型精确区分。
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

/** 依次尝试候选字段名，失败后按字段类型匹配（Spigot 字段名为混淆短名） */
fun getDeclaredFieldCompat(
    clazz: Class<*>,
    vararg names: String,
    type: Class<*>? = null,
): Field {
    for (name in names) {
        runCatching { return clazz.getDeclaredField(name) }
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
 * craftbukkit 类全名，兼容带版本后缀的 Spigot（org.bukkit.craftbukkit.v1_21_R3.*）。
 * Paper 1.21.4+ 无后缀；优先无后缀，失败后从服务端实现类包名提取版本后缀。
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