package io.github.zzzyyylllty.sertraline.nms

import org.bukkit.inventory.ItemStack
import java.lang.reflect.Method

// 为了方便，我们先定义一个反射工具类或对象
object NmsReflection {
    // NMS/CraftBukkit 类
    private val craftItemStackClass: Class<*> by lazy {
        // 根据你的服务器版本，路径可能不同
        // Paper 通常会重映射，路径是 net.minecraft.server.v1_20_R4.CraftItemStack
        // 但更好的做法是通过 Bukkit API 获取
        val serverVersion = org.bukkit.Bukkit.getServer().javaClass.getPackage().name.split(".")[3]
        Class.forName("org.bukkit.craftbukkit.$serverVersion.inventory.CraftItemStack")
    }

    val asNmsCopyMethod: Method by lazy {
        // 直接找 org.bukkit.craftbukkit.inventory.CraftItemStack 这个类
        // Paper 会处理好版本映射
        val craftItemStackClass = Class.forName("org.bukkit.craftbukkit.inventory.CraftItemStack")
        craftItemStackClass.getMethod("asNMSCopy", ItemStack::class.java)
    }

    // 将 NMS ItemStack 转换回 Bukkit ItemStack 的方法
    val asBukkitCopyMethod: Method by lazy {
        val craftItemStackClass = Class.forName("org.bukkit.craftbukkit.inventory.CraftItemStack")
        val nmsItemStackClass = Class.forName("net.minecraft.world.item.ItemStack")
        craftItemStackClass.getMethod("asBukkitCopy", nmsItemStackClass)
    }

    // --- 你已经获取到的部分 ---
    // 我在这里重新获取它们，并添加 set 方法，以确保完整性

    // 获取 DataComponentHolder.get(DataComponentType)
    val getComponentMethod: Method by lazy {
        val dataComponentHolderClass = Class.forName("net.minecraft.core.component.DataComponentHolder")
        val dataComponentTypeClass = Class.forName("net.minecraft.core.component.DataComponentType")
        dataComponentHolderClass.getMethod("get", dataComponentTypeClass)
    }

    // 获取 ItemStack.set(DataComponentType, Object)
    val setComponentMethod: Method by lazy {
        val nmsItemStackClass = Class.forName("net.minecraft.world.item.ItemStack")
        val dataComponentTypeClass = Class.forName("net.minecraft.core.component.DataComponentType")
        // set 方法签名是 set(DataComponentType<T>, @Nullable T)
        // 反射时，我们用 Object.class 来匹配泛型 T
        nmsItemStackClass.getMethod("set", dataComponentTypeClass, Object::class.java)
    }

    // 获取 ResourceLocation.fromNamespaceAndPath(String, String)
    val fromNamespaceAndPathMethod: Method by lazy {
        val resourceLocationClass = Class.forName("net.minecraft.resources.ResourceLocation")
        resourceLocationClass.getMethod("fromNamespaceAndPath", String::class.java, String::class.java)
    }

    // 获取数据组件注册表 (Registry)
    val componentRegistry: Any by lazy {
        val builtInRegistriesClass = Class.forName("net.minecraft.core.registries.BuiltInRegistries")
        val dataComponentTypeField = builtInRegistriesClass.getDeclaredField("DATA_COMPONENT_TYPE") // 这是字段名
        dataComponentTypeField.isAccessible = true
        dataComponentTypeField.get(null) // get(null) for static field
    }

    val registryGetMethod: Method by lazy {
        val targetClass = NmsReflection.componentRegistry.javaClass
        val resourceLocationClass = Class.forName("net.minecraft.resources.ResourceLocation")

        // 创建一个递归函数来深度搜索接口
        fun findMethodInInterfaces(clazz: Class<*>): Method? {
            // 1. 优先在当前类的接口中查找
            for (iface in clazz.interfaces) {
                try {
                    // 尝试在当前接口直接查找 getOptional
                    val method = iface.getDeclaredMethod("getOptional", resourceLocationClass)
                    println("[MyPlugin Debug] Found 'getOptional' in interface ${iface.name}!")
                    return method
                } catch (e: NoSuchMethodException) {
                    // 当前接口没有，递归查找这个接口的父接口
                    val methodInSuperInterface = findMethodInInterfaces(iface)
                    if (methodInSuperInterface != null) {
                        return methodInSuperInterface
                    }
                }
            }
            // 2. 如果当前类的所有接口及其父接口都没有，返回 null
            return null
        }

        // 主搜索逻辑
        var currentClass: Class<*>? = targetClass
        while (currentClass != null) {
            // a. 在当前类自身查找 getOptional
            try {
                val method = currentClass.getDeclaredMethod("getOptional", resourceLocationClass)
                method.isAccessible = true
                println("[MyPlugin Debug] Found 'getOptional' directly in class ${currentClass.name}!")
                return@lazy method
            } catch (e: NoSuchMethodException) {
                // 类自身没有，就深度搜索它的所有接口
                val methodInInterfaces = findMethodInInterfaces(currentClass)
                if (methodInInterfaces != null) {
                    methodInInterfaces.isAccessible = true
                    return@lazy methodInInterfaces
                }
            }
            // b. 继续向上查找父类
            currentClass = currentClass.superclass
        }

        // 如果 getOptional 彻底找不到，我们才回退到 get，但这次也用深度搜索
        println("[MyPlugin Warn] Could not find 'getOptional'. Falling back to 'get'. This might be unstable.")
        currentClass = targetClass
        while (currentClass != null) {
            try {
                val method = currentClass.getDeclaredMethod("get", resourceLocationClass)
                method.isAccessible = true
                println("[MyPlugin Debug] Found fallback 'get' in class ${currentClass.name}!")
                return@lazy method
            } catch (e: NoSuchMethodException) {
                // get 方法通常不会在接口中，所以这里简化了，只查类本身
            }
            currentClass = currentClass.superclass
        }


        throw NoSuchMethodException("FATAL: Could not find any suitable 'get' or 'getOptional' method in the entire hierarchy of ${targetClass.name}")
    }



    // NBT 相关类的反射 (用于操作 CustomData)
    val nbtTagCompoundClass: Class<*> by lazy {
        Class.forName("net.minecraft.nbt.CompoundTag")
    }
    val nbtTagCompound_putString: Method by lazy {
        nbtTagCompoundClass.getMethod("putString", String::class.java, String::class.java)
    }
    val nbtTagCompound_getString: Method by lazy {
        nbtTagCompoundClass.getMethod("getString", String::class.java)
    }

    private val codecClass: Class<*> by lazy {
        Class.forName("com.mojang.serialization.Codec")
    }

    // 获取附魔组件类型，用于获取其编解码器
    val dataComponentTypeClass: Class<*> by lazy {
        Class.forName("net.minecraft.core.component.DataComponentType")
    }

    // 获取 DataComponentType.codec() 方法
    val getCodecMethod: Method by lazy {
        dataComponentTypeClass.getMethod("codec")
    }

    // 获取 DynamicOps<NBTBase> (用于NBT)
    val nbtOpsInstance: Any by lazy {
        val nbtOpsClass = Class.forName("net.minecraft.nbt.NbtOps")
        // NbtOps.INSTANCE 是一个静态字段
        nbtOpsClass.getField("INSTANCE").get(null)
    }

    // 获取 DynamicOps<JsonElement> (用于JSON)
    val jsonOpsInstance: Any by lazy {
        val jsonOpsClass = Class.forName("com.mojang.serialization.JsonOps")
        // JsonOps.INSTANCE 是一个静态字段
        jsonOpsClass.getField("INSTANCE").get(null)
    }

    // 获取 NBTBase 类 (所有NBT标签的父类)
    private val nbtBaseClass: Class<*> by lazy {
        Class.forName("net.minecraft.nbt.Tag") // 在新版本中是 Tag
    }

    // 获取 Codec.encodeStart(DynamicOps, T) 方法
    val codecEncodeMethod: Method by lazy {
        val dynamicOpsClass = Class.forName("com.mojang.serialization.DynamicOps")
        codecClass.getMethod("encodeStart", dynamicOpsClass, Object::class.java)
    }

    // 获取 Codec.parse(DynamicOps, T) 方法
    val codecParseMethod: Method by lazy {
        val dynamicOpsClass = Class.forName("com.mojang.serialization.DynamicOps")
        val nbtBaseClass = Class.forName("net.minecraft.nbt.Tag") // 或者其他输入类型
        codecClass.getMethod("parse", dynamicOpsClass, nbtBaseClass)
    }

    // 获取 DataResult.getOrThrow() 方法，用于从解析结果中取出对象
    val dataResultGetOrThrowMethod: Method by lazy {
        val dataResultClass = Class.forName("com.mojang.serialization.DataResult")
        // getOrThrow() 在1.20.5+中是无参的
        dataResultClass.getMethod("getOrThrow")
    }

    val iChatBaseComponentClass: Class<*> by lazy {
        Class.forName("net.minecraft.network.chat.Component")
    }
}
