package io.github.zzzyyylllty.sertraline.reflect

import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.JavaOps
import com.mojang.serialization.JsonOps
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.logger.severeS
import io.github.zzzyyylllty.sertraline.util.assembleCBClass
import io.github.zzzyyylllty.sertraline.util.assembleMCClass
import io.github.zzzyyylllty.sertraline.util.getStaticMethod
import io.github.zzzyyylllty.sertraline.util.getClazz
import io.github.zzzyyylllty.sertraline.util.getDeclaredField
import io.github.zzzyyylllty.sertraline.util.getMethod
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Method
import java.util.Objects.requireNonNull
import java.util.Optional


val `clazz$ResourceLocation` = requireNonNull(getClazz(
    assembleMCClass("resources.ResourceLocation")
))!!

val `clazz$Registry` = requireNonNull(getClazz(
    assembleMCClass("core.Registry")
))!!

val `clazz$BuiltInRegistries` = requireNonNull(getClazz(
    assembleMCClass("core.registries.BuiltInRegistries")
))!!

val `clazz$DataComponentType` = requireNonNull(getClazz(
    assembleMCClass("core.component.DataComponentType")
))!!

val `clazz$DataComponentHolder` = requireNonNull(getClazz(
    assembleMCClass("core.component.DataComponentHolder")
))!!

val `clazz$MinecraftServer` = requireNonNull(getClazz(
    assembleMCClass("server.MinecraftServer")
))!!

val `field$BuiltInRegistries$DATA_COMPONENT_TYPE` = requireNonNull(getDeclaredField(
    `clazz$BuiltInRegistries`, "DATA_COMPONENT_TYPE"
))!!

val `instance$BuiltInRegistries$DATA_COMPONENT_TYPE` = `field$BuiltInRegistries$DATA_COMPONENT_TYPE`.get(null)!!

val `method$ResourceLocation$fromNamespaceAndPath` = requireNonNull(getStaticMethod(
    `clazz$ResourceLocation`, `clazz$ResourceLocation`, String::class.java, String::class.java
))!!

val `method$Registry$getValue` = requireNonNull(getMethod(
    `clazz$Registry`, Any::class.java, 0, `clazz$ResourceLocation`
))!!

val `clazz$RegistryOps` = requireNonNull(getClazz(
    assembleMCClass("resources.RegistryOps")
))!!

val `clazz$HolderLookup$Provider` = requireNonNull(getClazz(
    assembleMCClass("core.HolderLookup\$Provider")
))!!

val `method$RegistryOps$create` = requireNonNull(getMethod(
    `clazz$RegistryOps`, `clazz$RegistryOps`, 0, DynamicOps::class.java, `clazz$HolderLookup$Provider`
))!!

val `method$MinecraftServer$getServer` = requireNonNull(getStaticMethod(
    `clazz$MinecraftServer`, `clazz$MinecraftServer`
))!!

val `instance$MinecraftServer$SERVER` = `method$MinecraftServer$getServer`.invoke(null)!!

val `clazz$RegistryAccess$Frozen` = requireNonNull(getClazz(
    assembleMCClass("core.RegistryAccess\$Frozen")
))!!

val `method$MinecraftServer$registryAccess` = requireNonNull(getMethod(
    `clazz$MinecraftServer`, `clazz$RegistryAccess$Frozen`, 0
))!!

val `instance$MinecraftServer$registryAccess` = `method$MinecraftServer$registryAccess`.invoke(`instance$MinecraftServer$SERVER`)!!

val `method$DataComponentType$codec` = requireNonNull(getMethod(
    `clazz$DataComponentType`, Codec::class.java, 0
))!!

val `method$DataComponentHolder$getDataComponentType` = requireNonNull(getMethod(
    `clazz$DataComponentHolder`, Any::class.java, 0, `clazz$DataComponentType`
))!!

val `clazz$ItemStack` = requireNonNull(getClazz(
    assembleMCClass("world.item.ItemStack")
))!!

val `method$ItemStack$setComponent` = requireNonNull(getMethod(
    `clazz$ItemStack`, Any::class.java, 0, `clazz$DataComponentType`, Any::class.java
))!!

val `clazz$CraftItemStack` = requireNonNull(getClazz(
    assembleCBClass("inventory.CraftItemStack")
))!!

val `field$CraftItemStack$handle` = requireNonNull(getDeclaredField(
    `clazz$CraftItemStack`, `clazz$ItemStack`, 0
))!!

val `clazz$NbtOps` = requireNonNull(getClazz(
    assembleMCClass("nbt.NbtOps")
))!!

val `field$NbtOps$INSTANCE` = requireNonNull(getDeclaredField(
    `clazz$NbtOps`, `clazz$NbtOps`, 0
))!!

val `instance$NbtOps$INSTANCE` = `field$NbtOps$INSTANCE`.get(null)!!

@Suppress("UNCHECKED_CAST")
val `instance$DynamicOps$NBT` = `method$RegistryOps$create`.invoke(null, `instance$NbtOps$INSTANCE`, `instance$MinecraftServer$registryAccess`)!! as DynamicOps<Any>

@Suppress("UNCHECKED_CAST")
val `instance$DynamicOps$JAVA` = `method$RegistryOps$create`.invoke(null, JavaOps.INSTANCE, `instance$MinecraftServer$registryAccess`)!! as DynamicOps<Any>

@Suppress("UNCHECKED_CAST")
val `instance$DynamicOps$JSON` = `method$RegistryOps$create`.invoke(null, JsonOps.INSTANCE, `instance$MinecraftServer$registryAccess`)!! as DynamicOps<JsonElement>

val `clazz$Tag` = requireNonNull(getClazz(
    assembleMCClass("nbt.Tag")
))!!

val `method$ResourceLocation$tryParse` = requireNonNull(getStaticMethod(
    `clazz$ResourceLocation`, `clazz$ResourceLocation`, String::class.java
))!!

val `method$ItemStack$removeComponent` = requireNonNull(getMethod(
    `clazz$ItemStack`, Any::class.java, 1, `clazz$DataComponentType`, Any::class.java
))!!

@Suppress("UNCHECKED_CAST")
fun <T> getComponent(itemStack: Any, type: Any, ops: DynamicOps<*>): Optional<T> {
    val codec = `method$DataComponentType$codec`.invoke(ensureDataComponentType(type)) as Codec<Any>
    val componentData = `method$DataComponentHolder$getDataComponentType`.invoke(itemStack, type)
    return (componentData?.let {
        codec.encodeStart(ops as DynamicOps<Any>, it).result().orElseGet { Optional.empty<T>() }
    } ?: Optional.empty<T>()) as Optional<T>
}

@Suppress("UNCHECKED_CAST")
fun setComponent(itemStack: Any, type: Any, ops: DynamicOps<*>, value: Any) {
    val codec = `method$DataComponentType$codec`.invoke(ensureDataComponentType(type)) as Codec<Any>
    val result = codec.parse(ops as DynamicOps<Any>, value)
    if (result.isError) throw IllegalArgumentException(result.toString())
    result.result().ifPresent { `method$ItemStack$setComponent`.invoke(itemStack, type, it) }
}

fun ensureDataComponentType(type: Any): Any = when {
    `clazz$DataComponentType`.isInstance(type) -> type
    `clazz$ResourceLocation`.isInstance(type) ->
        `method$Registry$getValue`.invoke(`instance$BuiltInRegistries$DATA_COMPONENT_TYPE`, type)
    else -> {
        val rl = `method$ResourceLocation$tryParse`.invoke(type.toString())
        `method$Registry$getValue`.invoke(`instance$BuiltInRegistries$DATA_COMPONENT_TYPE`, rl)
    }
}

fun setComponent(itemStack: Any, type: Any, value: Any) {
    when (value) {
        is JsonElement -> setComponent(itemStack, type, `instance$DynamicOps$JSON`, value)
        `clazz$Tag`.isInstance(value) -> setComponent(itemStack, type, `instance$DynamicOps$NBT`, value)
        else -> setComponent(itemStack, type, `instance$DynamicOps$JAVA`, value)
    }
}

fun <T> getJavaComponent(itemStack: Any, type: Any): Optional<T> {
    return getComponent(itemStack, type, `instance$DynamicOps$JAVA`)
}

fun getJsonComponent(itemStack: Any, type: Any): Optional<JsonElement> {
    return getComponent(itemStack, type, `instance$DynamicOps$JSON`)
}

fun getNBTComponent(itemStack: Any, type: Any): Optional<Any> {
    return getComponent(itemStack, type, `instance$DynamicOps$NBT`)
}

fun removeComponent(itemStack: Any, type: Any) {
    `method$ItemStack$removeComponent`.invoke(itemStack, type)
}

class Test {
    fun main() {
        val itemStack = ItemStack(Material.DIAMOND_AXE);
        val nmsStack = `field$CraftItemStack$handle`.get(itemStack)
        setComponent(nmsStack, "minecraft:damage", 10)
        val json = getJsonComponent(nmsStack, "minecraft:damage").orElse(null)
        devLog(json.toString())
    }
}

//private fun ensureDataComponentType(type: Any): Any? {
//    val dataComponentTypeClass = getClazz("net.minecraft.core.component.DataComponentType")
//
//    if (!dataComponentTypeClass.isInstance(type)) {
//        val key = type.toString().split(":")
//        if (key.size < 2) return null
//        val fromNamespaceAndPath = `getResourceLocation#fromNamespaceAndPath`() ?: return null
//
//        val resourceLocation = fromNamespaceAndPath.invoke(null, key[0], key[1])
//
//        val registry = `getBuiltInRegistries#DATA_COMPONENT_TYPE`() ?: return null
//
//        // 这里调用实际存在的 get 方法
//        val getMethod = registry.javaClass.getMethod("get", resourceLocation.javaClass)
//        return getMethod.invoke(registry, resourceLocation)
//    }
//    return type
//}

/*
private fun setComponentInternal(itemStack: Any, type: Any, ops: DynamicOps<*>?, value: Any?) {
    if (value == null) return

    val componentType = ensureDataComponentType(type) ?: return

    val codec = getCodecFromComponentType(componentType)
        ?: throw RuntimeException("Cannot get codec for component type: $componentType")

    try {
        val parseMethod = codec.javaClass.getMethod("parse", DynamicOps::class.java, Any::class.java)
        val dataResultRaw = parseMethod.invoke(codec, ops, jsonUtils.toJson(value))

        // 通过反射调用 result() 获取 Optional
        val resultMethod = dataResultRaw.javaClass.getMethod("result")
        val optionalResult = resultMethod.invoke(dataResultRaw) as java.util.Optional<*>

        require(optionalResult.isPresent) { dataResultRaw.toString() }

        optionalResult.ifPresent { parsedComponent ->
            setItemStackComponent(itemStack, componentType, parsedComponent)
        }
    } catch (t: Throwable) {
        throw RuntimeException("Cannot parse component $type", t)
    }
}


// 直接用注册表的 get(ResourceKey) 而非 getValue(ResourceKey, ResourceLocation)



/**
 * 通过 keyName（String，例如 "minecraft:health"）获取 DataComponentType 实例
 */
fun getDataComponentTypeByKey(keyName: String): Any? {
    println("Looking up DataComponentType by key: $keyName") // 日志
    val builtInRegistriesClass = Class.forName("net.minecraft.core.registries.BuiltInRegistries")
    val dataComponentTypeRegistry = builtInRegistriesClass.getDeclaredField("DATA_COMPONENT_TYPE").apply {
        isAccessible = true
    }.get(null)

    // MinecraftKey 使用静态 parse(String) 来构造
    val parseMethod = MCKeyClassName.getMethod("parse", String::class.java)
    val minecraftKey = parseMethod.invoke(null, keyName)

    val getMethod = dataComponentTypeRegistry.javaClass.getMethod("get", MCKeyClassName)
    return getMethod.invoke(dataComponentTypeRegistry, minecraftKey)
}

/**
 * 根据 componentType 获取对应的 Codec<Any?>
 */
@Suppress("UNCHECKED_CAST")
fun getCodecFromComponentType(componentType: Any): Codec<Any?>? {
    // 优先尝试调用 componentType.codec() 方法
    return try {
        val codecMethod = componentType.javaClass.getMethod("codec")
        codecMethod.invoke(componentType) as Codec<Any?>
    } catch (e: Exception) {
        // fallback：使用静态 CODEC 字段
        val dataComponentTypeClass = Class.forName("net.minecraft.core.component.DataComponentType")
        val codecField = dataComponentTypeClass.getDeclaredField("CODEC").apply { isAccessible = true }
        codecField.get(null) as Codec<Any?>
    }
}
*/