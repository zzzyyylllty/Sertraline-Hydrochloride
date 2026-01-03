package io.github.zzzyyylllty.sertraline.util

import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.debugMode.devLogSync
import io.github.zzzyyylllty.sertraline.logger.severeS
import io.github.zzzyyylllty.sertraline.logger.severeSSync
import org.bukkit.Bukkit
import org.tabooproject.reflex.ReflexClass
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import net.minecraft.core.registries.BuiltInRegistries
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

object ItemTagManager {


//    private val registryGetValueMethod by lazy {
//        val registryClass = getClazz("net.minecraft.core.Registry")
//        registryClass.getDeclaredMethod("getValue", getClazz("net.minecraft.resources.ResourceLocation"))
//    }

    val `clazz$ResourceLocation` by lazy {
            getClazz(
                assembleMCClass("resources.ResourceLocation")
            )
    }


    // 1. 获取核心接口 IRegistry (注意：源代码显示是 IRegistry 而非 Registry)
    val `clazz$IRegistry` by lazy {
        getClazz("net.minecraft.core.IRegistry")
    }

    // 2. 获取 MinecraftKey 类 (注意：源代码显示是 MinecraftKey 而非 ResourceLocation)
    val `clazz$MinecraftKey` by lazy {
        getClazz("net.minecraft.resources.MinecraftKey")
    }

    // 3. 获取 BuiltInRegistries 类
    private val builtInRegistriesClass by lazy {
        getClazz("net.minecraft.core.registries.BuiltInRegistries")
    }

    // 4. 获取物品注册表实例 (BuiltInRegistries.ITEM)
    private val itemIRegistryField by lazy {
        getDeclaredField(builtInRegistriesClass, "ITEM") ?: throw NullPointerException("ITEM Registries is null!")
    }

    // 4. 获取物品注册表实例 (BuiltInRegistries.ITEM)
    private val itemIRegistry by lazy {
        itemIRegistryField.get(null)
    }

    // 5. 获取 getValue 方法: T getValue(@Nullable MinecraftKey var1)
    val registryGetValueMethod by lazy {
        `clazz$IRegistry`.getDeclaredMethod("getValue", `clazz$MinecraftKey`)
    }

    // 6. 获取 getKey 方法: @Nullable MinecraftKey getKey(T var1)
    // 关键修正：在 Java 反射中，泛型 T 被擦除为 Object，所以参数必须是 Object (Any)
    private val registryGetKeyMethod by lazy {
        `clazz$IRegistry`.getDeclaredMethod("getKey", Any::class.java)
    }

    // 7. (可选) 获取 get 方法 (替代 getHolder)
    // 源代码显示: Optional<Holder.c<T>> get(MinecraftKey var1)
    // 如果你需要通过 Key 获取 Holder，应该使用这个方法
    private val registryGetOptionalMethod by lazy {
        `clazz$IRegistry`.getDeclaredMethod("get", `clazz$MinecraftKey`)
    }

    private val registryGetHolderMethod by lazy {
        val registryClass = getClazz("net.minecraft.core.Registry")
        registryClass.getDeclaredMethod("getHolder", getClazz("net.minecraft.resources.ResourceKey"))
    }

    private val resourceKeyCreateMethod by lazy {
        val resourceKeyClass = getClazz("net.minecraft.resources.ResourceKey")
        resourceKeyClass.getDeclaredMethod("create", getClazz("net.minecraft.resources.ResourceLocation"), getClazz("net.minecraft.resources.ResourceLocation"))
    }

    private val resourceLocationClass by lazy {
        getClazz("net.minecraft.resources.ResourceLocation")
    }
    private val resourceKeyClass by lazy {
        getClazz("net.minecraft.resources.ResourceKey")
    }


    private val resourceLocationConstructor by lazy {
        resourceLocationClass.getDeclaredConstructor(String::class.java, String::class.java)
    }

    private val holderReferenceTagsField by lazy {
        val holderReferenceClass = getClazz("net.minecraft.core.Holder\$Reference")
        val tagsField = holderReferenceClass.getDeclaredField("tags")
        tagsField.isAccessible = true
        tagsField
    }

    private val tagKeyLocationField by lazy {
        val tagKeyClass = getClazz("net.minecraft.tags.TagKey")
        val locationField = tagKeyClass.getDeclaredField("location")
        locationField.isAccessible = true
        locationField
    }

    private val tagKeyCreateMethod by lazy {
        val tagKeyClass = getClazz("net.minecraft.tags.TagKey")
        val registryKeyClass = getClazz("net.minecraft.resources.ResourceKey")
        tagKeyClass.getDeclaredMethod("create", registryKeyClass, resourceLocationClass)
    }

    // 4. 获取物品注册表实例 (BuiltInRegistries.ITEM)
    private val itemRegistryKeyField by lazy {
        getDeclaredField(builtInRegistriesClass, "ITEM") ?: throw NullPointerException("ITEM Registries is null!")
    }

    // 4. 获取物品注册表实例 (BuiltInRegistries.ITEM)
    private val itemRegistryKey by lazy {
        itemIRegistryField.get(null)
    }

//    private val itemRegistryKey by lazy {
//        val registryKeyClass = getClazz("net.minecraft.resources.ResourceKey")
//        val registryKeyField = registryKeyClass.getDeclaredField("ITEM")
//        registryKeyField.isAccessible = true
//        registryKeyField.get(null)
//    }

    // 存储原版物品标签
    private val vanillaItemTags = ConcurrentHashMap<String, MutableList<String>>()

    // 存储自定义物品标签
    private val customItemTags = ConcurrentHashMap<String, MutableList<String>>()

    // 初始化原版物品标签
//    @Awake(LifeCycle.ENABLE)
    fun initializeVanillaTags() {
//        try {

            `clazz$ResourceLocation`.javaClass.fields.forEach { field ->
                devLogSync("clazz\$ResourceLocation fields | 字段: ${field.name}, 类型: ${field.type.name}")
            }

            `clazz$ResourceLocation`.javaClass.methods.forEach { methods ->
                devLogSync("clazz\$ResourceLocation methods | 字段: ${methods.name}")
            }

            // 获取物品注册表的可迭代对象
            val items = itemIRegistry as Iterable<*>

            for (item in items) {
                // 获取物品的 ResourceLocation
                val resourceLocation = registryGetKeyMethod.invoke(itemIRegistry, item) as Any
                val itemKey = "${resourceLocation.javaClass.getDeclaredMethod("getNamespace").invoke(resourceLocation)}:${resourceLocation.javaClass.getDeclaredMethod("getPath").invoke(resourceLocation)}"

                // 获取物品的 Holder
                val resourceKey = resourceKeyCreateMethod.invoke(null, itemRegistryKey, resourceLocation)
                val holderOptional = registryGetHolderMethod.invoke(itemIRegistry, resourceKey) as java.util.Optional<*>

                if (holderOptional.isPresent) {
                    val holder = holderOptional.get()
                    // 获取 Holder 的标签集合
                    val tags = holderReferenceTagsField.get(holder) as Set<*>

                    for (tag in tags) {
                        // 获取标签的 ResourceLocation
                        val tagLocation = tagKeyLocationField.get(tag)
                        val tagKey = "${tagLocation.javaClass.getDeclaredMethod("getNamespace").invoke(tagLocation)}:${tagLocation.javaClass.getDeclaredMethod("getPath").invoke(tagLocation)}"

                        // 将物品添加到对应的标签列表中
                        vanillaItemTags.computeIfAbsent(tagKey) { mutableListOf() }.add(itemKey)
                    }
                }
            }
//        } catch (e: Exception) {
//            severeSSync("Failed to initialize vanilla item tags: ${e.message}")
//            throw e
//        }
    }

    // 获取某个标签的所有物品
    fun getItemsByTag(tagKey: String): List<String> {
        val vanillaItems = vanillaItemTags[tagKey] ?: emptyList()
        val customItems = customItemTags[tagKey] ?: emptyList()
        return (vanillaItems + customItems).distinct()
    }

    // 注册自定义标签
    fun registerCustomTag(tagKey: String, items: List<String>) {
        customItemTags.computeIfAbsent(tagKey) { mutableListOf() }.addAll(items)
    }

    // 给物品添加自定义标签
    fun addItemToCustomTag(itemKey: String, tagKey: String) {
        customItemTags.computeIfAbsent(tagKey) { mutableListOf() }.add(itemKey)
    }

    // 检查物品是否有某个标签
    fun hasItemTag(itemKey: String, tagKey: String): Boolean {
        return getItemsByTag(tagKey).contains(itemKey)
    }

    // 获取所有标签
    fun getAllTags(): Set<String> {
        return (vanillaItemTags.keys + customItemTags.keys).toSet()
    }
}