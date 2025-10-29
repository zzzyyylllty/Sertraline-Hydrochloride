package io.github.zzzyyylllty.sertraline.reflect

import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import com.mojang.serialization.DynamicOps
import io.github.zzzyyylllty.sertraline.Sertraline.reflects
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.logger.severeS
import io.github.zzzyyylllty.sertraline.util.getStaticMethod
import io.github.zzzyyylllty.sertraline.util.jsonUtils
import io.github.zzzyyylllty.sertraline.util.parseJsonStringWithMCGson
import io.github.zzzyyylllty.sertraline.util.parseStringToMinecraftJsonElement
import io.github.zzzyyylllty.sertraline.util.simpleGetClazz
import io.github.zzzyyylllty.sertraline.util.simpleReflect
import io.papermc.paper.datacomponent.DataComponentType
import taboolib.library.reflex.Reflex.Companion.getProperty
import java.lang.reflect.Method
// import net.minecraft.core.component.DataComponentType.CODEC

val MCKeyClassName = Class.forName("net.minecraft.resources.MinecraftKey")
fun `getBuiltInRegistries#DATA_COMPONENT_TYPE`(): Any? {
    return try {
        val builtInRegistriesClass = Class.forName("net.minecraft.core.registries.BuiltInRegistries")
        val component = builtInRegistriesClass.getProperty<Any?>("DATA_COMPONENT_TYPE", isStatic = true)
        component
    } catch (e: Exception) {
        severeS("BuiltInRegistries#DATA_COMPONENT_TYPE reflect getMethod failed.")
        e.printStackTrace()
        null
    }
}

fun `getResourceLocation#fromNamespaceAndPath`(): Method? {
    return try {
        MCKeyClassName.getMethod("fromNamespaceAndPath", String::class.java, String::class.java)
    } catch (e: Exception) {
        severeS("MinecraftKey#fromNamespaceAndPath reflect getMethod failed.")
        e.printStackTrace()
        null
    }
}


fun `getRegistry#getId`(): java.lang.reflect.Method? {
    return simpleReflect("net.minecraft.core.Registry","getId")
}

fun getRegistryOpsCreateMethod(): Method? {
    return getStaticMethod(registryOps, registryOps, DynamicOps::class.java, holderLookupProvider)
}

fun getRegistryOpsFun(): Class<*> {
    return registryOps
}
val registryOps by lazy {
    simpleGetClazz("net.minecraft.resources.RegistryOps")
}
val holderLookupProvider by lazy {
    simpleGetClazz("net.minecraft.core.HolderLookup")
}

fun `getDataComponentType#CODEC#Field`(): java.lang.reflect.Field? {
    return try {
        val compClass = Class.forName("net.minecraft.core.component.DataComponentType")
        val codecField = compClass.getField("CODEC")

        // 3. (可选) 如果你需要设置字段可访问（对于 private 字段是必须的，对于 public 字段是好习惯）
        // codecField.isAccessible = true

        codecField
    } catch (e: Exception) {
        severeS("DataComponentType#CODEC reflect getField failed.")
        e.printStackTrace()
        null
    }
}

val registryAccess by lazy { getMinecraftServer()!!::class.java.getMethod("registryAccess") }

fun registryAccess(): Any? {
    return registryAccess
}

fun getMinecraftServer(): Any? {
    try {
        val nmsClassName = "net.minecraft.server.MinecraftServer"
        val minecraftServerClass = Class.forName(nmsClassName)
        val getServerMethod = minecraftServerClass.getMethod("getServer")
        return getServerMethod.invoke(null)
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
        return null
    }
}

fun `getDataComponentType#CODEC#Value`(): Codec<DataComponentType>? {
    val codecField = `getDataComponentType#CODEC#Field`()
    if (codecField != null) {
        try {
            // 因为 CODEC 是静态字段，所以在 get() 方法中传入 null
            // 将返回的 Object 转型为你需要的 Codec 类型
            @Suppress("UNCHECKED_CAST")
            return codecField.get(null) as? Codec<DataComponentType>
        } catch (e: Exception) {
            severeS("Failed to get value from DataComponentType#CODEC field.")
            e.printStackTrace()
        }
    }
    return null
}



fun `getDataComponentHolder#get`(): Method? {
    return try {
        val holderClass = Class.forName("net.minecraft.core.component.DataComponentHolder")
        val paramClass = Class.forName("net.minecraft.core.component.DataComponentType")

        holderClass.getMethod("get", paramClass)

    } catch (e: Exception) {
        severeS("DataComponentHolder#get reflect getMethod failed.")
        e.printStackTrace()
        null
    }
}
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
private fun ensureDataComponentType(type: Any): Any? {
    val dataComponentTypeClass = simpleGetClazz("net.minecraft.core.component.DataComponentType")

    if (!dataComponentTypeClass.isInstance(type)) {
        val key = type.toString().split(":")
        if (key.size < 2) return null
        val fromNamespaceAndPath = `getResourceLocation#fromNamespaceAndPath`() ?: return null

        val resourceLocation = fromNamespaceAndPath.invoke(null, key[0], key[1])

        val registry = `getBuiltInRegistries#DATA_COMPONENT_TYPE`() ?: return null

        // 这里调用实际存在的 get 方法
        val getMethod = registry.javaClass.getMethod("get", resourceLocation.javaClass)
        return getMethod.invoke(registry, resourceLocation)
    }
    return type
}


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

/**
 * 通过 codec 解析输入（value，通常是 Dynamic<?> JSON 或类似格式）成组件实例
 */
fun parseComponent(codec: Codec<Any?>, ops: DynamicOps<*>?, value: String?): Any? {
    val parseMethod = codec.javaClass.getMethod("parse", DynamicOps::class.java, Any::class.java)

    val mcJsonElement = value?.let { parseJsonStringWithMCGson(it) }

    val dataResultRaw = parseMethod.invoke(codec, ops, mcJsonElement)

    val resultMethod = dataResultRaw.javaClass.getMethod("result")
    val optionalResult = resultMethod.invoke(dataResultRaw) as java.util.Optional<*>

    require(optionalResult.isPresent) { "Parsing component failed: $dataResultRaw" }

    return optionalResult.get()
}

/**
 * 通过反射调用 ItemStack.setComponent 将组件写入物品
 */
fun setItemStackComponent(itemStack: Any, componentType: Any, componentInstance: Any) {
    val itemStackClass = itemStack.javaClass
    val setComponentMethod = itemStackClass.getMethod(
        "setComponent",
        componentType.javaClass,
        Object::class.java
    )
    setComponentMethod.invoke(itemStack, componentType, componentInstance)
}

/**
 * 一个示范调用全过程：
 *
 * @param itemStack Minecraft 的 ItemStack 实例
 * @param componentKey 组件对应注册表key，比如 "minecraft:health"
 * @param ops DynamicOps 实例，用于 Codec 解析
 * @param value 需要解析的输入值（通常是某种JSON节点或者NBT）
 */
fun demoSetComponentInternal(itemStack: Any, componentKey: String, ops: DynamicOps<*>?, value: JsonElement?) {
    if (value == null) return
    val componentType = getDataComponentTypeByKey(componentKey)
        ?: throw RuntimeException("Component type for key $componentKey not found")
    val codec = getCodecFromComponentType(componentType)
        ?: throw RuntimeException("Codec not found")
    devLog("Codec class: ${codec.javaClass}")
    devLog("Codec type: ${codec.toString()}")

    val parseMethod = codec.javaClass.getMethod("parse", DynamicOps::class.java, Any::class.java)
    val dataResultRaw = parseMethod.invoke(codec, ops, value) // value 是 JsonElement

    val resultMethod = dataResultRaw.javaClass.getMethod("result")
    val optionalResult = resultMethod.invoke(dataResultRaw) as java.util.Optional<*>

    require(optionalResult.isPresent) { "Parsing failed: $dataResultRaw" }

    val componentInstance = optionalResult.get()

    setItemStackComponent(itemStack, componentType, componentInstance)
}

