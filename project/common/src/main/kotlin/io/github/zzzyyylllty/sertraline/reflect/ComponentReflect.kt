package io.github.zzzyyylllty.sertraline.reflect

import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.JavaOps
import com.mojang.serialization.JsonOps
import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.logger.severeS
import io.github.zzzyyylllty.sertraline.logger.warningS
import io.github.zzzyyylllty.sertraline.util.assembleCBClass
import io.github.zzzyyylllty.sertraline.util.assembleMCClass
import io.github.zzzyyylllty.sertraline.util.getStaticMethod
import io.github.zzzyyylllty.sertraline.util.getClazz
import io.github.zzzyyylllty.sertraline.util.getDeclaredField
import io.github.zzzyyylllty.sertraline.util.getMethod
import io.github.zzzyyylllty.sertraline.util.unwrapValue
import org.bukkit.Material
import org.bukkit.Registry
import org.bukkit.inventory.ItemStack
import taboolib.module.lang.asLangText
import taboolib.module.nms.NMSItemTag.Companion.asBukkitCopy
import taboolib.module.nms.NMSItemTag.Companion.asNMSCopy
import java.util.Objects.requireNonNull
import java.util.Optional
//import net.minecraft.core.component.DataComponentType
//import net.minecraft.core.RegistryAccess

val `clazz$ResourceLocation` = requireNonNull(getClazz(
    assembleMCClass("resources.ResourceLocation")
))!!

val `clazz$Registry` = requireNonNull(
        getClazz(
            assembleMCClass("core.IRegistryWritable")
        )
    )!!

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

val `method$Registry$getValue` = run {
    requireNonNull(
        getMethod(
            `clazz$Registry`, Any::class.java, 0, `clazz$ResourceLocation`
        )
    )!!
}

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

val holderClass by lazy { getClazz("net.minecraft.core.Holder")!! }
val holderMethod by lazy { holderClass.getDeclaredMethod("get") }

@Suppress("UNCHECKED_CAST")
fun <T> getComponent(itemStack: Any, type: Any, ops: DynamicOps<T>): Optional<T> {
    val res = ensureDataComponentType(type)
    val codec = `method$DataComponentType$codec`.invoke(res) as Codec<T>
    val componentData = `method$DataComponentHolder$getDataComponentType`.invoke(itemStack, res)
        ?: return Optional.empty<T>() as Optional<T>
    val castComponentData = componentData as T
    val dataResult = codec.encodeStart(ops, castComponentData)
    return dataResult.result()
}


@Suppress("UNCHECKED_CAST")
fun setComponentInternal(itemStack: Any, type: Any, ops: DynamicOps<*>, value: Any) {
    val res = ensureDataComponentType(type)
    if (res == null) {
        warningS(console.asLangText("Warning_Component_Setting_Failed", type, value))
        return
    }
    val codec = `method$DataComponentType$codec`.invoke(res) as Codec<Any>
    val result = codec.parse(ops as DynamicOps<Any>, value)
    if (result.isError) throw IllegalArgumentException(result.toString())
    result.result().ifPresent {
        `method$ItemStack$setComponent`.invoke(itemStack, res, it)
    }
}

fun ensureDataComponentType(type: Any): Any? {
    val rawResult = when {
        `clazz$DataComponentType`.isInstance(type) -> type
        `clazz$ResourceLocation`.isInstance(type) -> `method$Registry$getValue`.invoke(`instance$BuiltInRegistries$DATA_COMPONENT_TYPE`, type)
        else -> {
            val rl = `method$ResourceLocation$tryParse`.invoke(null, type.toString())
            `method$Registry$getValue`.invoke(`instance$BuiltInRegistries$DATA_COMPONENT_TYPE`, rl)
        }
    }
    return unwrapValue(rawResult)
}





fun setComponentInternal(itemStack: Any, type: Any, value: Any) {
    when (value) {
        is JsonElement -> setComponentInternal(itemStack, type, `instance$DynamicOps$JSON`, value)
        `clazz$Tag`.isInstance(value) -> setComponentInternal(itemStack, type, `instance$DynamicOps$NBT`, value)
        else -> setComponentInternal(itemStack, type, `instance$DynamicOps$JAVA`, value)
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> getJavaComponent(itemStack: Any, type: Any): Optional<T> {
    return getComponent(itemStack, type, `instance$DynamicOps$JAVA`) as Optional<T>
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


val craftItemStackClass by lazy { getClazz(assembleCBClass("inventory.CraftItemStack"))!! }
val asNMSCopyMethod by lazy { craftItemStackClass.getMethod("asNMSCopy", ItemStack::class.java) }
val asBukkitCopyMethod by lazy { craftItemStackClass.getMethod("asBukkitCopy", `clazz$ItemStack`) }

fun ItemStack.setComponent(componentId: String,value: Any): ItemStack {

    val itemStack = this
    // 转换Bukkit ItemStack 为 NMS ItemStack
    try {
        val nmsStack = asNMSCopy(itemStack)

        setComponentInternal(nmsStack, componentId, value)
        val bukkitStack = asBukkitCopy(nmsStack)
        return bukkitStack
    } catch (e: Exception) {
        warningS(console.asLangText("Warning_Component_Setting_Failed_Exception",componentId,value,e))
        return itemStack
    }
}
fun Any.setComponentNMS(componentId: String,value: Any): Any {
    try {
        setComponentInternal(this, componentId, value)
        return this
    } catch (e: Exception) {
        warningS(console.asLangText("Warning_Component_Setting_Failed_Exception",componentId,value,e))
        return this
    }
}

fun Any.getComponentNMS(componentId: String): JsonElement? {
    return getJsonComponent(this, componentId).orElse(null)
}
fun ItemStack.getComponent(componentId: String): JsonElement? {

    val itemStack = this

    val nmsStack = try {
        asNMSCopy(itemStack)
    }
    catch (e: Exception) {
        warningS(console.asLangText("Warning_Component_Getting_Failed_Exception",componentId,e))
        throw e
    }

    return getJsonComponent(nmsStack, componentId).orElse(null)
}
@Suppress("UNCHECKED_CAST")
fun Any.getComponentsNMS(): Map<String, JsonElement> {
    val result = mutableMapOf<String, JsonElement>()

    if (!`clazz$DataComponentHolder`.isInstance(this)) {
        warningS(console.asLangText("Warning_Not_DataComponentHolder", this))
        return result
    }

    val getComponentsMethod = `clazz$DataComponentHolder`.getMethod("getComponents")
    val dataComponentMap = getComponentsMethod.invoke(this)!!

    // 获取 iterator() 方法
    val iteratorMethod = dataComponentMap.javaClass.getMethod("iterator")
    val iterator = iteratorMethod.invoke(dataComponentMap) as Iterator<Any>

    while (iterator.hasNext()) {
        val typedDataComponent = iterator.next()

        // typedDataComponent 需要通过反射访问 type() 和 value()
        val typeMethod = typedDataComponent.javaClass.getMethod("type")
        val valueMethod = typedDataComponent.javaClass.getMethod("value")

        val componentType = typeMethod.invoke(typedDataComponent)
        val componentValue = valueMethod.invoke(typedDataComponent)

        val id = componentType.toString()

        val codec = `method$DataComponentType$codec`.invoke(componentType) as Codec<Any>
        val encodedResult = codec.encodeStart(`instance$DynamicOps$JSON`, componentValue)
        val jsonElement = encodedResult.result().orElse(null) ?: continue

        result[id] = jsonElement
    }

    return result
}
@Suppress("UNCHECKED_CAST")
fun Any.getComponentsNMSFilteredLegacy(): Map<String, Any?> {
    val result = mutableMapOf<String, Any?>()

    if (!`clazz$DataComponentHolder`.isInstance(this)) {
        warningS(console.asLangText("Warning_Not_DataComponentHolder", this))
        return result
    }

    val getComponentsMethod = `clazz$DataComponentHolder`.getMethod("getComponents")
    val dataComponentMapInstance = getComponentsMethod.invoke(this) ?: return result

    val iteratorMethod = dataComponentMapInstance.javaClass.getMethod("iterator")
    val iterator = iteratorMethod.invoke(dataComponentMapInstance) as Iterator<Any>

    while (iterator.hasNext()) {
        val typedDataComponent = iterator.next()

        val typeMethod = typedDataComponent.javaClass.getMethod("type")
        val componentTypeRaw = typeMethod.invoke(typedDataComponent)

        // 反射调用 toString 得到 resourceLocation字符串
        val resourceLocationStr = componentTypeRaw.toString()

        // 用注册表去获取对应完整组件类型实例，避免版本差异或者动态生成的子类导致反射异常
        val resourceLocation = `method$ResourceLocation$tryParse`.invoke(null, resourceLocationStr)
        val componentTypeOptional = `method$Registry$getValue`.invoke(`instance$BuiltInRegistries$DATA_COMPONENT_TYPE`, resourceLocation)
            ?: continue
        val componentType = unwrapValue(componentTypeOptional)

        val valueMethod = typedDataComponent.javaClass.getMethod("value")
        val componentValue = valueMethod.invoke(typedDataComponent)

        println("componentType class: ${componentType?.javaClass?.name}")
        println("expected clazz: ${`clazz$DataComponentType`.name}")
        println("isInstance: ${`clazz$DataComponentType`.isInstance(componentType)}")

        val codecMethod = try {
            componentType.javaClass.getDeclaredMethod("codec")
        } catch (_: NoSuchMethodException) {
            componentType.javaClass.getDeclaredMethod("codecOrThrow") // 备用
        }
        codecMethod.isAccessible = true

        val codec = codecMethod.invoke(componentType) as Codec<Any>


        // 序列化当前数据
        val encodedResult = codec.encodeStart(`instance$DynamicOps$NBT`, componentValue)
        if (encodedResult.isError) continue
        val currentNbtTag = encodedResult.result().orElse(null) ?: continue

        // 比较默认值，反序列化空Json
        val emptyJson = com.google.gson.JsonObject()
        val defaultParseResult = codec.parse(`instance$DynamicOps$JSON`, emptyJson)
        val defaultValue = if (!defaultParseResult.isError)
            defaultParseResult.result().orElse(null)
        else null

        if (defaultValue != null) {
            val defaultEncoded = codec.encodeStart(`instance$DynamicOps$NBT`, defaultValue)
            if (!defaultEncoded.isError) {
                val defaultNbtTag = defaultEncoded.result().orElse(null)
                if (defaultNbtTag != null && defaultNbtTag == currentNbtTag) {
                    // 当前组件等价于默认值，过滤掉
                    continue
                }
            }
        }

        // 转换NBT数据为Json
        val jsonResult = codec.encodeStart(`instance$DynamicOps$JAVA`, componentValue)
        if (jsonResult.isError) continue
        val componentJson = jsonResult.result().orElse(null) ?: continue

        result[resourceLocationStr] = componentJson
    }

    return result
}

@Suppress("UNCHECKED_CAST")
fun Any.getComponentsNMSFilteredWithoutCache(): Map<String, JsonElement> {
    val result = mutableMapOf<String, JsonElement>()

    if (!`clazz$DataComponentHolder`.isInstance(this)) {
        warningS(console.asLangText("Warning_Not_DataComponentHolder", this))
        return result
    }

    val getComponentsPatchMethod = try {
        this.javaClass.getMethod("getComponentsPatch")
    } catch (e: NoSuchMethodException) {
        warningS(console.asLangText("Warning_Method_NotFound_getComponentsPatch", e))
        return result
    }

    val patch = getComponentsPatchMethod.invoke(this) ?: return result

    val getItemMethod = try {
        this.javaClass.getMethod("getItem")
    } catch (e: NoSuchMethodException) {
        warningS(console.asLangText("Warning_Method_NotFound_getItem", e))
        return result
    }

    val item = getItemMethod.invoke(this) ?: return result

    val getComponentsMethodOfItem = try {
        item.javaClass.getMethod("components")
    } catch (e: NoSuchMethodException) {
        warningS(console.asLangText("Warning_Method_NotFound_components", e))
        return result
    }

    val prototype = getComponentsMethodOfItem.invoke(item) ?: return result


    // 获取 patch.entrySet()
    val entrySetMethod = patch.javaClass.getMethod("entrySet")
    val entrySet = entrySetMethod.invoke(patch) as Set<*>

    for (entryObj in entrySet) {
        val entry = entryObj as Map.Entry<*, *>

        val componentTypeRaw = entry.key ?: continue
        val componentValue = entry.value?.let { unwrapValue(it) } ?: continue

        val resourceLocationStr = componentTypeRaw.toString()
        val resourceLocation = `method$ResourceLocation$tryParse`.invoke(null, resourceLocationStr) ?: continue

        val componentTypeOptional = `method$Registry$getValue`.invoke(
            `instance$BuiltInRegistries$DATA_COMPONENT_TYPE`,
            resourceLocation
        ) ?: continue
        val componentType = unwrapValue(componentTypeOptional)

        println("componentType class: ${componentType.javaClass.name}")
        println("isInstance: ${`clazz$DataComponentType`.isInstance(componentType)}")


        val prototypeGetTypedMethod = prototype.javaClass.getMethod("getTyped", `clazz$DataComponentType`)
        val prototypeTyped = prototypeGetTypedMethod.invoke(prototype, componentType)



        if (prototypeTyped != null) {
            val prototypeValueMethod = prototypeTyped.javaClass.getMethod("value")
            val prototypeValue = prototypeValueMethod.invoke(prototypeTyped)
            if (prototypeValue == componentValue) {
                // 补丁值和原型相同，跳过
                continue
            }
        }

        val codecMethod = try {
            componentType.javaClass.getDeclaredMethod("codec")
        } catch (e: NoSuchMethodException) {
            componentType.javaClass.getDeclaredMethod("codecOrThrow")
        }
        codecMethod.isAccessible = true
        val codec = codecMethod.invoke(componentType) as Codec<Any>

        val encodedResultNBT = codec.encodeStart(`instance$DynamicOps$NBT`, componentValue)
        if (encodedResultNBT.isError) {
            warningS(console.asLangText("Warning_Codec_Encoding_Error", resourceLocationStr, encodedResultNBT.error()))
            continue
        }
        val nbtTag = encodedResultNBT.result().orElse(null) ?: continue

        val encodedResultJson = codec.encodeStart(`instance$DynamicOps$JSON`, componentValue)
        if (encodedResultJson.isError) {
            warningS(console.asLangText("Warning_Codec_Encoding_Error", resourceLocationStr, encodedResultJson.error()))
            continue
        }
        val componentJson = encodedResultJson.result().orElse(null) ?: continue

        result[resourceLocationStr] = componentJson
    }

    return result
}
// ========== 缓存反射方法 ==========
private val `method$ItemStack$getComponentsPatch` by lazy {
    `clazz$ItemStack`.getMethod("getComponentsPatch")
}

private val `method$ItemStack$getItem` by lazy {
    `clazz$ItemStack`.getMethod("getItem")
}

private val `method$Item$components` by lazy {
    val itemClass = getClazz(assembleMCClass("world.item.Item"))!!
    itemClass.getMethod("components")
}

private val `method$DataComponentPatch$entrySet` by lazy {
    val patchClass = getClazz(assembleMCClass("core.component.DataComponentPatch"))!!
    patchClass.getMethod("entrySet")
}

private val `method$DataComponentMap$getTyped` by lazy {
    val mapClass = getClazz(assembleMCClass("core.component.DataComponentMap"))!!
    mapClass.getMethod("getTyped", `clazz$DataComponentType`)
}

private val `method$TypedDataComponent$value` by lazy {
    val typedClass = getClazz(assembleMCClass("core.component.TypedDataComponent"))!!
    typedClass.getMethod("value")
}

// ========== 优化后的主函数 ==========
@Suppress("UNCHECKED_CAST")
fun Any.getComponentsNMSFiltered(): Map<String, Any?> {
    val result = mutableMapOf<String, Any?>()

    if (!`clazz$DataComponentHolder`.isInstance(this)) {
        warningS(console.asLangText("Warning_Not_DataComponentHolder", this))
        return result
    }

    // 使用缓存的反射方法
    val patch = `method$ItemStack$getComponentsPatch`.invoke(this) ?: return result
    val item = `method$ItemStack$getItem`.invoke(this) ?: return result
    val prototype = `method$Item$components`.invoke(item) ?: return result

    // 获取 patch.entrySet()
    val entrySet = `method$DataComponentPatch$entrySet`.invoke(patch) as Set<*>

    for (entryObj in entrySet) {
        val entry = entryObj as? Map.Entry<*, *> ?: continue

        val componentTypeRaw = entry.key ?: continue
        val componentValue = entry.value?.let { unwrapValue(it) } ?: continue

        // 转换资源位置字符串
        val resourceLocationStr = componentTypeRaw.toString()
        val resourceLocation = `method$ResourceLocation$tryParse`.invoke(null, resourceLocationStr) ?: continue

        // 从注册表获取组件类型
        val componentTypeOptional = `method$Registry$getValue`.invoke(
            `instance$BuiltInRegistries$DATA_COMPONENT_TYPE`,
            resourceLocation
        ) ?: continue
        val componentType = unwrapValue(componentTypeOptional)

        // 比较原型值，过滤未修改的组件
        val prototypeTyped = `method$DataComponentMap$getTyped`.invoke(prototype, componentType)
        if (prototypeTyped != null) {
            val prototypeValue = `method$TypedDataComponent$value`.invoke(prototypeTyped)
            if (prototypeValue == componentValue) {
                continue
            }
        }

        // 获取 codec（使用缓存的方法查找逻辑）
        val codec = getCodecForComponentType(componentType) ?: continue

        // 使用 JAVA DynamicOps 编码
        val encodedResultJava = codec.encodeStart(`instance$DynamicOps$JAVA`, componentValue)
        if (encodedResultJava.isError) {
            warningS(console.asLangText("Warning_Codec_Encoding_Error", resourceLocationStr, encodedResultJava.error()))
            continue
        }
        val componentJavaObject = encodedResultJava.result().orElse(null) ?: continue

        result[resourceLocationStr] = componentJavaObject
    }

    return result
}

// ========== 辅助函数：获取 Codec（带缓存） ==========
private val codecCache = mutableMapOf<Any, Codec<Any>>()

@Suppress("UNCHECKED_CAST")
private fun getCodecForComponentType(componentType: Any): Codec<Any>? {
    return codecCache.getOrPut(componentType) {
        try {
            val codecMethod = componentType.javaClass.getDeclaredMethod("codec").apply {
                isAccessible = true
            }
            codecMethod.invoke(componentType) as Codec<Any>
        } catch (e: NoSuchMethodException) {
            try {
                val codecMethod = componentType.javaClass.getDeclaredMethod("codecOrThrow").apply {
                    isAccessible = true
                }
                codecMethod.invoke(componentType) as Codec<Any>
            } catch (e2: Exception) {
                null
            }
        } catch (e: Exception) {
            null
        } ?: return null
    }
}
