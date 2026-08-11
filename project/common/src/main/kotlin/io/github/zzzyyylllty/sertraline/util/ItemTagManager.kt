package io.github.zzzyyylllty.sertraline.util

import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.debugMode.devLogSync
import java.util.concurrent.ConcurrentHashMap

object ItemTagManager {


//    private val registryGetValueMethod by lazy {
//        val registryClass = getClazz("net.minecraft.core.Registry")
//        registryClass.getDeclaredMethod("getValue", getClazz("net.minecraft.resources.ResourceLocation"))
//    }

    // 位置类：Paper = ResourceLocation，Spigot = MinecraftKey
    val `clazz$ResourceLocation` by lazy {
        getClazzCompat(
            "net.minecraft.resources.ResourceLocation",
            "net.minecraft.resources.MinecraftKey"
        )
    }

    // 1. 现代注册表接口：Paper = Registry，Spigot = IRegistry
    // 注意：Spigot 上 net.minecraft.core.Registry 是旧的 getId/byId 接口，必须先试 IRegistry
    val `clazz$IRegistry` by lazy {
        getClazzCompat(
            "net.minecraft.core.IRegistry",
            "net.minecraft.core.Registry"
        )
    }

    // 2. 位置类（与 clazz$ResourceLocation 相同解析，保留原名）
    val `clazz$MinecraftKey` by lazy {
        getClazzCompat(
            "net.minecraft.resources.ResourceLocation",
            "net.minecraft.resources.MinecraftKey"
        )
    }

    // 3. 获取 BuiltInRegistries 类（双平台同名）
    private val builtInRegistriesClass by lazy {
        getClazz("net.minecraft.core.registries.BuiltInRegistries")
    }

    // 4. 获取物品注册表实例 (BuiltInRegistries.ITEM / Spigot 混淆字段名: g)
    private val itemIRegistryField by lazy {
        getDeclaredFieldCompat(builtInRegistriesClass, "ITEM", "g")
    }

    // 4. 获取物品注册表实例 (BuiltInRegistries.ITEM)
    private val itemIRegistry by lazy {
        itemIRegistryField.get(null)
    }

    // 5. 获取 getValue 方法: T getValue(@Nullable MinecraftKey var1)
    val registryGetValueMethod by lazy {
        getDeclaredMethodCompat(`clazz$IRegistry`, listOf("getValue", "a"), null, `clazz$MinecraftKey`)
    }

    // 6. 获取 getKey 方法: @Nullable MinecraftKey getKey(T var1)
    // 关键修正：在 Java 反射中，泛型 T 被擦除为 Object，所以参数必须是 Object (Any)
    private val registryGetKeyMethod by lazy {
        getDeclaredMethodCompat(`clazz$IRegistry`, "getKey", `clazz$MinecraftKey`, Any::class.java)
    }

    // 7. (可选) 获取 get 方法 (替代 getHolder)
    // 源代码显示: Optional<Holder.c<T>> get(MinecraftKey var1)
    // 如果你需要通过 Key 获取 Holder，应该使用这个方法
    private val registryGetOptionalMethod by lazy {
        getDeclaredMethodCompat(`clazz$IRegistry`, listOf("get", "c"), null, `clazz$MinecraftKey`)
    }

    // 8. 获取 wrapAsHolder 方法: Holder<T> wrapAsHolder(T var1)
    // 1.21.4 双平台 Registry 接口均无 getHolder(ResourceKey)（旧实现必然 NoSuchMethodException），
    // 改用 wrapAsHolder：返回已注册的 Holder（自带标签集），无注册才兜底 Direct。
    // Paper: Registry.wrapAsHolder(Object)；Spigot: IRegistry.e(T)
    private val registryWrapAsHolderMethod by lazy {
        val holderClass = getClazz("net.minecraft.core.Holder")
        getDeclaredMethodCompat(`clazz$IRegistry`, listOf("wrapAsHolder", "e"), holderClass, Any::class.java)
    }

    private val resourceKeyCreateMethod by lazy {
        val resourceKeyClass = getClazz("net.minecraft.resources.ResourceKey")
        getDeclaredMethodCompat(resourceKeyClass, listOf("create", "a"), null, `clazz$ResourceLocation`, `clazz$ResourceLocation`)
    }

    private val resourceLocationClass by lazy {
        getClazzCompat(
            "net.minecraft.resources.ResourceLocation",
            "net.minecraft.resources.MinecraftKey"
        )
    }
    private val resourceKeyClass by lazy {
        getClazz("net.minecraft.resources.ResourceKey")
    }

    private val tagKeyLocationClass by lazy {
        getClazzCompat(
            "net.minecraft.resources.ResourceLocation",
            "net.minecraft.resources.MinecraftKey"
        )
    }

    private val holderReferenceTagsField by lazy {
        val holderReferenceClass = getClazzCompat(
            "net.minecraft.core.Holder\$Reference",
            "net.minecraft.core.Holder\$c"
        )
        // Spigot 混淆字段名: b（类型 Set<TagKey>）
        getDeclaredFieldCompat(holderReferenceClass, "tags", "b", type = java.util.Set::class.java)
    }

    private val tagKeyLocationField by lazy {
        val tagKeyClass = getClazz("net.minecraft.tags.TagKey")
        // Spigot 混淆字段名: b（类型 MinecraftKey）
        getDeclaredFieldCompat(tagKeyClass, "location", "b", type = resourceLocationClass)
    }

    private val tagKeyCreateMethod by lazy {
        val tagKeyClass = getClazz("net.minecraft.tags.TagKey")
        val registryKeyClass = getClazz("net.minecraft.resources.ResourceKey")
        getDeclaredMethodCompat(tagKeyClass, listOf("create", "a"), null, registryKeyClass, resourceLocationClass)
    }

    // 4. 获取物品注册表实例 (BuiltInRegistries.ITEM / Spigot 混淆字段名: g)
    private val itemRegistryKeyField by lazy {
        getDeclaredFieldCompat(builtInRegistriesClass, "ITEM", "g")
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
                // 直接用 toString()（"namespace:path"）：
                // Spigot 上 getNamespace/getPath 为混淆名 b()/a()，与 Mojang 名错位且签名相同，无法可靠匹配
                val itemKey = resourceLocation.toString()

                // 获取物品的 Holder（wrapAsHolder 返回注册的 Holder，自带标签集）
                val holder = registryWrapAsHolderMethod.invoke(itemIRegistry, item)

                // 获取 Holder 的标签集合
                val tags = holderReferenceTagsField.get(holder) as Set<*>

                for (tag in tags) {
                    // 获取标签的 ResourceLocation
                    val tagLocation = tagKeyLocationField.get(tag)
                    val tagKey = tagLocation.toString()

                    // 将物品添加到对应的标签列表中
                    vanillaItemTags.computeIfAbsent(tagKey) { mutableListOf() }.add(itemKey)
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

    /**
     * 解析标签字符串，支持多种格式并提取数量
     * @param tagString 标签字符串，如 "tag:planks 2"、"#minecraft:planks"、"minecraft:planks"
     * @return Pair(规范化标签Key, 数量)，数量默认为1
     */
    fun parseTagString(tagString: String): Pair<String, Int> {
        var normalized = tagString.trim()

        // 去除#前缀
        if (normalized.startsWith("#")) {
            normalized = normalized.substring(1)
        }

        // 检查是否包含数量（空格分隔）
        val parts = normalized.split("\\s+".toRegex())
        val base = parts[0]
        val amount = if (parts.size > 1) {
            parts[1].toIntOrNull() ?: 1
        } else {
            1
        }

        // 转换tag:前缀为minecraft:
        var tagKey = base
        if (tagKey.startsWith("tag:")) {
            tagKey = "minecraft:" + tagKey.substring(4)
        }

        // 如果没有命名空间，添加minecraft:
        if (!tagKey.contains(":")) {
            tagKey = "minecraft:$tagKey"
        }

        return Pair(tagKey, amount)
    }
}
