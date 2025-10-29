package io.github.zzzyyylllty.sertraline.util.component

import com.google.common.hash.HashCode
import com.google.gson.JsonElement
import com.mojang.serialization.DynamicOps
import net.momirealms.craftengine.bukkit.plugin.reflection.minecraft.CoreReflections
import org.bukkit.inventory.ItemStack
import java.util.*


abstract class UnitItemComponentProvider(item: ItemStack) {
    open val item: ItemStack? = null

    open val NBTS: DynamicOps<Any?>? = null
    open val JAVA: DynamicOps<Any?>? = null
    open val JSON: DynamicOps<JsonElement?>? = null
    open val HASHCODE: DynamicOps<HashCode?>? = null // 1.21.5+

    abstract fun getComponent(component: String)
    abstract fun getComponents()

    abstract fun setJsonComponent(component: String, value: Any?)
    abstract fun setNBTComponent(component: String, value: Any?)
    abstract fun setJavaComponent(component: String, value: Any?)

    fun setComponent(component: String, value: Any?) {
        if (value is JsonElement) {
            setJsonComponent(component, value)
        } else if (CoreReflections.`clazz$Tag`.isInstance(value)) {
            setNBTComponent(component, value)
        } else {
            setJavaComponent(component, value)
        }
    }
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
            NBT = CoreReflections.`method$$create`.invoke(
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
}
*/