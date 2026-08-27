package io.github.zzzyyylllty.sertraline.util

import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.debugMode.devLogSync
import java.util.concurrent.ConcurrentHashMap

object ItemTagManager {


//    private val registryGetValueMethod by lazy {
//        val registryClass = getClazz("net.minecraft.core.Registry")
//        registryClass.getDeclaredMethod("getValue", getClazz("net.minecraft.resources.ResourceLocation"))
//    }

    // Location class: Paper = ResourceLocation, Spigot = MinecraftKey
    val `clazz$ResourceLocation` by lazy {
        getClazzCompat(
            "net.minecraft.resources.ResourceLocation",
            "net.minecraft.resources.MinecraftKey"
        )
    }

    // 1. Modern registry interface: Paper = Registry, Spigot = IRegistry
    // Note: on Spigot net.minecraft.core.Registry is the old getId/byId interface, so IRegistry must be tried first
    val `clazz$IRegistry` by lazy {
        getClazzCompat(
            "net.minecraft.core.IRegistry",
            "net.minecraft.core.Registry"
        )
    }

    // 2. Location class (same resolution as clazz$ResourceLocation, name kept)
    val `clazz$MinecraftKey` by lazy {
        getClazzCompat(
            "net.minecraft.resources.ResourceLocation",
            "net.minecraft.resources.MinecraftKey"
        )
    }

    // 3. Get the BuiltInRegistries class (same name on both platforms)
    private val builtInRegistriesClass by lazy {
        getClazz("net.minecraft.core.registries.BuiltInRegistries")
    }

    // 4. Get the item registry instance (BuiltInRegistries.ITEM / Spigot obfuscated field name: g)
    private val itemIRegistryField by lazy {
        getDeclaredFieldCompat(builtInRegistriesClass, "ITEM", "g")
    }

    // 4. Get the item registry instance (BuiltInRegistries.ITEM)
    private val itemIRegistry by lazy {
        itemIRegistryField.get(null)
    }

    // 5. Get the getValue method: T getValue(@Nullable MinecraftKey var1)
    val registryGetValueMethod by lazy {
        getDeclaredMethodCompat(`clazz$IRegistry`, listOf("getValue", "a"), null, `clazz$MinecraftKey`)
    }

    // 6. Get the getKey method: @Nullable MinecraftKey getKey(T var1)
    // Key fix: in Java reflection the generic T is erased to Object, so the parameter must be Object (Any)
    private val registryGetKeyMethod by lazy {
        getDeclaredMethodCompat(`clazz$IRegistry`, "getKey", `clazz$MinecraftKey`, Any::class.java)
    }

    // 7. (Optional) Get the get method (replacement for getHolder)
    // Source code shows: Optional<Holder.c<T>> get(MinecraftKey var1)
    // If you need to obtain a Holder by Key, you should use this method
    private val registryGetOptionalMethod by lazy {
        getDeclaredMethodCompat(`clazz$IRegistry`, listOf("get", "c"), null, `clazz$MinecraftKey`)
    }

    // 8. Get the wrapAsHolder method: Holder<T> wrapAsHolder(T var1)
    // On 1.21.4 neither platform's Registry interface has getHolder(ResourceKey) (the old implementation would always throw NoSuchMethodException),
    // so use wrapAsHolder instead: it returns a registered Holder (with its own tag set), and only falls back to Direct when not registered.
    // Paper: Registry.wrapAsHolder(Object); Spigot: IRegistry.e(T)
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
        // Spigot obfuscated field name: b (type Set<TagKey>)
        getDeclaredFieldCompat(holderReferenceClass, "tags", "b", type = java.util.Set::class.java)
    }

    private val tagKeyLocationField by lazy {
        val tagKeyClass = getClazz("net.minecraft.tags.TagKey")
        // Spigot obfuscated field name: b (type MinecraftKey)
        getDeclaredFieldCompat(tagKeyClass, "location", "b", type = resourceLocationClass)
    }

    private val tagKeyCreateMethod by lazy {
        val tagKeyClass = getClazz("net.minecraft.tags.TagKey")
        val registryKeyClass = getClazz("net.minecraft.resources.ResourceKey")
        getDeclaredMethodCompat(tagKeyClass, listOf("create", "a"), null, registryKeyClass, resourceLocationClass)
    }

    // 4. Get the item registry instance (BuiltInRegistries.ITEM / Spigot obfuscated field name: g)
    private val itemRegistryKeyField by lazy {
        getDeclaredFieldCompat(builtInRegistriesClass, "ITEM", "g")
    }

    // 4. Get the item registry instance (BuiltInRegistries.ITEM)
    private val itemRegistryKey by lazy {
        itemIRegistryField.get(null)
    }

//    private val itemRegistryKey by lazy {
//        val registryKeyClass = getClazz("net.minecraft.resources.ResourceKey")
//        val registryKeyField = registryKeyClass.getDeclaredField("ITEM")
//        registryKeyField.isAccessible = true
//        registryKeyField.get(null)
//    }

    // Stores vanilla item tags
    private val vanillaItemTags = ConcurrentHashMap<String, MutableList<String>>()

    // Stores custom item tags
    private val customItemTags = ConcurrentHashMap<String, MutableList<String>>()

    // Initialize vanilla item tags
//    @Awake(LifeCycle.ENABLE)
    fun initializeVanillaTags() {
//        try {

            `clazz$ResourceLocation`.javaClass.fields.forEach { field ->
                devLogSync("clazz\$ResourceLocation fields | 字段: ${field.name}, 类型: ${field.type.name}")
            }

            `clazz$ResourceLocation`.javaClass.methods.forEach { methods ->
                devLogSync("clazz\$ResourceLocation methods | 字段: ${methods.name}")
            }

            // Get the iterable of the item registry
            val items = itemIRegistry as Iterable<*>

            for (item in items) {
                // Get the item's ResourceLocation
                val resourceLocation = registryGetKeyMethod.invoke(itemIRegistry, item) as Any
                // Use toString() directly ("namespace:path"):
                // On Spigot getNamespace/getPath are the obfuscated names b()/a(), misaligned with the Mojang names yet sharing the same signature, so reliable matching is impossible
                val itemKey = resourceLocation.toString()

                // Get the item's Holder (wrapAsHolder returns the registered Holder, which carries its own tag set)
                val holder = registryWrapAsHolderMethod.invoke(itemIRegistry, item)

                // Get the Holder's tag set
                val tags = holderReferenceTagsField.get(holder) as Set<*>

                for (tag in tags) {
                    // Get the tag's ResourceLocation
                    val tagLocation = tagKeyLocationField.get(tag)
                    val tagKey = tagLocation.toString()

                    // Add the item to the corresponding tag list
                    vanillaItemTags.computeIfAbsent(tagKey) { mutableListOf() }.add(itemKey)
                }
            }
//        } catch (e: Exception) {
//            severeSSync("Failed to initialize vanilla item tags: ${e.message}")
//            throw e
//        }
    }

    // Get all items for a given tag
    fun getItemsByTag(tagKey: String): List<String> {
        val vanillaItems = vanillaItemTags[tagKey] ?: emptyList()
        val customItems = customItemTags[tagKey] ?: emptyList()
        return (vanillaItems + customItems).distinct()
    }

    // Register a custom tag
    fun registerCustomTag(tagKey: String, items: List<String>) {
        customItemTags.computeIfAbsent(tagKey) { mutableListOf() }.addAll(items)
    }

    // Add an item to a custom tag
    fun addItemToCustomTag(itemKey: String, tagKey: String) {
        customItemTags.computeIfAbsent(tagKey) { mutableListOf() }.add(itemKey)
    }

    // Check whether an item has a given tag
    fun hasItemTag(itemKey: String, tagKey: String): Boolean {
        return getItemsByTag(tagKey).contains(itemKey)
    }

    // Get all tags
    fun getAllTags(): Set<String> {
        return (vanillaItemTags.keys + customItemTags.keys).toSet()
    }

    /**
     * Parse a tag string, supporting multiple formats and extracting the amount
     * @param tagString the tag string, e.g. "tag:planks 2", "#minecraft:planks", "minecraft:planks"
     * @return Pair(normalized tag Key, amount), amount defaults to 1
     */
    fun parseTagString(tagString: String): Pair<String, Int> {
        var normalized = tagString.trim()

        // strip the # prefix
        if (normalized.startsWith("#")) {
            normalized = normalized.substring(1)
        }

        // check whether it contains an amount (space-separated)
        val parts = normalized.split("\\s+".toRegex())
        val base = parts[0]
        val amount = if (parts.size > 1) {
            parts[1].toIntOrNull() ?: 1
        } else {
            1
        }

        // convert the tag: prefix to minecraft:
        var tagKey = base
        if (tagKey.startsWith("tag:")) {
            tagKey = "minecraft:" + tagKey.substring(4)
        }

        // if there is no namespace, add minecraft:
        if (!tagKey.contains(":")) {
            tagKey = "minecraft:$tagKey"
        }

        return Pair(tagKey, amount)
    }
}
