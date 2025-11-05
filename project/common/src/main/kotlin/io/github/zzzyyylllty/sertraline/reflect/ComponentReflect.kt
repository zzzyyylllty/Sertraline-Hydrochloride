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
import org.bukkit.inventory.ItemStack
import taboolib.module.lang.asLangText
import taboolib.module.nms.NMSItemTag.Companion.asBukkitCopy
import taboolib.module.nms.NMSItemTag.Companion.asNMSCopy
import java.util.Objects.requireNonNull
import java.util.Optional

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