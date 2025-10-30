package io.github.zzzyyylllty.sertraline.util.ce
import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import io.github.zzzyyylllty.sertraline.Sertraline.reflects
import io.github.zzzyyylllty.sertraline.util.getClazz

//object ItemComponentHelper {
//
//    val dataComponentTypeClass by lazy { getClazz("net.minecraft.core.component.DataComponentType") }
//
//    private fun getComponentCodec(registry: Any, componentId: String): Codec<Any>? {
//        val codecField = dataComponentTypeClass.getDeclaredField("CODEC").apply { isAccessible = true }
//        return codecField.get(null) as Codec<Any>
//    }
//
//    private fun encodeComponentJson(codec: Codec<Any>, value: Any): DataResult<JsonElement>? {
//        return codec.encodeStart(reflects.jsonOps, value)
//    }
//    private fun encodeComponentJava(codec: Codec<Any>, value: Any): DataResult<Any>? {
//        return codec.encodeStart(reflects.javaOps, value)
//    }
//
//    private fun getResourceKey(componentId: String): Any {
//        // 构造 ResourceKey 对象
//        // 可能需要通过ResourceKey.minecraft(componentId)方法构造
//        return ""
//    }
//}
//
//// 直接用注册表的 get(ResourceKey) 而非 getValue(ResourceKey, ResourceLocation)
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