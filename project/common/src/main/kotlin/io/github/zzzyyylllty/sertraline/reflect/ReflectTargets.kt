package io.github.zzzyyylllty.sertraline.reflect

import com.mojang.serialization.JavaOps
import com.mojang.serialization.JsonOps


class ReflectTargets {

    val javaOps by lazy { JavaOps.INSTANCE }
    val jsonOps by lazy { JsonOps.INSTANCE }
    //val nbtOps: Class<*>? by lazy { simpleGetClazz("net.minecraft.nbt.DynamicOpsNBT") }
//
//    val `BuiltInRegistries#DATA_COMPONENT_TYPE` by lazy { `BuiltInRegistries#DATA_COMPONENT_TYPE`() }
//    val `DataComponentHolder#get` by lazy { `getDataComponentHolder#get`() }
//    val `ResourceLocation#fromNamespaceAndPath` by lazy { `getResourceLocation#fromNamespaceAndPath`() }
//    val `Registry#getValue` by lazy { `getRegistry#getId`() }
//    val componentCodecValue by lazy { `getDataComponentType#CODEC#Field`() }
//    val componentCodecField by lazy { `getDataComponentType#CODEC#Field`() }
//
//    val registryCreateOps by lazy { getRegistryOpsCreateMethod() }
//    val BuiltInRegistries by lazy { getBuiltInRegistries() }
//
//    val registryAccess = { registryAccess() }
}
/*
object MRegistryOps {
    val NBT: DynamicOps<Any?>?
    val SPARROW_NBT: DynamicOps<Tag?>?
    val JAVA: DynamicOps<Any?>?
    val JSON: DynamicOps<JsonElement?>?
    val HASHCODE: DynamicOps<HashCode?>? // 1.21.5+

    // 1.20.5+
    val `clazz$JavaOps`: Class<*>? = ReflectionUtils.getClazz("com.mojang.serialization.JavaOps")

    val `clazz$NbtOps`: Class<*> = Objects.requireNonNull(
        BukkitReflectionUtils.findReobfOrMojmapClass(
            "nbt.DynamicOpsNBT",
            "nbt.NbtOps"
        )
    )

    init {
        try {
            if (`clazz$JavaOps` != null) {
                // 1.20.5+
                val javaOps = ReflectionUtils.getDeclaredField(`clazz$JavaOps`, `clazz$JavaOps`, 0)!!.get(null)
                JAVA = CoreReflections.`method$RegistryOps$create`.invoke(
                    null,
                    javaOps,
                    FastNMS.INSTANCE.registryAccess()
                ) as DynamicOps<Any?>?
            } else if (!VersionHelper.isOrAbove1_20_5()) {
                // 1.20.1-1.20.4
                JAVA = CoreReflections.`method$RegistryOps$create`.invoke(
                    null,
                    LegacyJavaOps.INSTANCE,
                    FastNMS.INSTANCE.registryAccess()
                ) as DynamicOps<Any?>?
            } else {
                throw ReflectionInitException("Could not find JavaOps")
            }
            NBT = CoreReflections.`method$RegistryOps$create`.invoke(
                null,
                ReflectionUtils.getDeclaredField(`clazz$NbtOps`, `clazz$NbtOps`, 0)!!.get(null),
                FastNMS.INSTANCE.registryAccess()
            ) as DynamicOps<Any?>?
            JSON = CoreReflections.`method$RegistryOps$create`.invoke(
                null,
                JsonOps.INSTANCE,
                FastNMS.INSTANCE.registryAccess()
            ) as DynamicOps<JsonElement?>?
            SPARROW_NBT = CoreReflections.`method$RegistryOps$create`.invoke(
                null,
                if (VersionHelper.isOrAbove1_20_5()) NBTOps.INSTANCE else LegacyNBTOps.INSTANCE,
                FastNMS.INSTANCE.registryAccess()
            ) as DynamicOps<Tag?>?
            HASHCODE = if (VersionHelper.isOrAbove1_21_5()) CoreReflections.`method$RegistryOps$create`.invoke(
                null,
                CoreReflections.`instance$HashOps$CRC32C_INSTANCE`,
                FastNMS.INSTANCE.registryAccess()
            ) as DynamicOps<HashCode?>? else null
        } catch (e: ReflectiveOperationException) {
            throw ReflectionInitException("Failed to init DynamicOps", e)
        }
    }
}*/