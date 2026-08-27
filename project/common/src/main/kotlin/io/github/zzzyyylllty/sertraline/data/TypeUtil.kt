package io.github.zzzyyylllty.sertraline.data

import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.Sertraline.types
import io.github.zzzyyylllty.sertraline.util.toBooleanTolerance
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.getItemTag

/**
 * Type utility providing type inheritance and metadata features, usable from JS/Kether scripts via TypeUtil
 */
object TypeUtil {

    /**
     * Resolve a type ID case-insensitively, tolerant of mismatched case in configs
     */
    private fun resolveTypeId(typeId: String): String? {
        if (types.containsKey(typeId)) return typeId
        return types.keys.firstOrNull { it.equals(typeId, ignoreCase = true) }
    }

    /**
     * Get a Type object (case-insensitive)
     * @param typeId Type ID
     * @return Type object, or null if it does not exist
     */
    fun getType(typeId: String): Type? {
        return resolveTypeId(typeId)?.let { types[it] }
    }

    /**
     * Get Type metadata
     * @param typeId Type ID
     * @param key meta key
     * @return meta value, or null if the type or key does not exist
     */
    fun getMeta(typeId: String, key: String): Any? {
        return getType(typeId)?.meta?.get(key)
    }

    /**
     * Get Type metadata as Boolean (leniently parsing true/yes/1 etc.)
     * @param typeId Type ID
     * @param key meta key
     * @return the parsed result, or false if it does not exist
     */
    fun getMetaAsBoolean(typeId: String, key: String): Boolean {
        return getMeta(typeId, key)?.toBooleanTolerance() ?: false
    }

    /**
     * Get Type metadata as String
     * @param typeId Type ID
     * @param key meta key
     * @return the string value, or null if it does not exist
     */
    fun getMetaAsString(typeId: String, key: String): String? {
        return getMeta(typeId, key)?.toString()
    }

    /**
     * Get Type metadata as Int
     * @param typeId Type ID
     * @param key meta key
     * @return the integer value, or null if it does not exist or is not numeric
     */
    fun getMetaAsInt(typeId: String, key: String): Int? {
        return (getMeta(typeId, key) as? Number)?.toInt()
            ?: getMetaAsString(typeId, key)?.toIntOrNull()
    }

    /**
     * Get Type metadata as Double
     * @param typeId Type ID
     * @param key meta key
     * @return the floating-point value, or null if it does not exist or is not numeric
     */
    fun getMetaAsDouble(typeId: String, key: String): Double? {
        return (getMeta(typeId, key) as? Number)?.toDouble()
            ?: getMetaAsString(typeId, key)?.toDoubleOrNull()
    }

    /**
     * Get Type metadata as List
     * @param typeId Type ID
     * @param key meta key
     * @return the list value, or null if it does not exist or is not a list
     */
    @Suppress("UNCHECKED_CAST")
    fun getMetaAsList(typeId: String, key: String): List<Any?>? {
        return getMeta(typeId, key) as? List<Any?>
    }


    /**
     * Get all ancestors of a type (including itself, case-insensitive)
     * @param typeId Type ID
     * @return list of ancestor type IDs, ordered from self up to the root (first element is itself)
     */
    fun getAncestors(typeId: String): List<String> {
        val visited = mutableSetOf<String>()
        val ancestors = mutableListOf<String>()
        var current: String? = typeId
        while (current != null && !visited.contains(current)) {
            visited.add(current)
            ancestors.add(current)
            current = types[current]?.parent?.let { resolveTypeId(it) }
        }
        return ancestors
    }

    /**
     * Check whether a type inherits from another type (or is the same)
     * @param typeId the type ID to check
     * @param potentialAncestorId the potential ancestor type ID
     * @return true if typeId inherits from potentialAncestorId (or is the same)
     */
    fun isAssignableFrom(typeId: String, potentialAncestorId: String): Boolean {
        val resolved = resolveTypeId(potentialAncestorId) ?: potentialAncestorId
        return getAncestors(typeId).any { it.equals(resolved, ignoreCase = true) }
    }

    /**
     * Get all descendants of a type (including itself)
     * Note: this operation traverses all types and is slow, only suitable when the type count is small
     * @param typeId Type ID
     * @return list of descendant type IDs
     */
    fun getDescendants(typeId: String): List<String> {
        return types.keys.filter { isAssignableFrom(it, typeId) }
    }

    /**
     * Get the type ID from item data
     * @param itemData Item data
     * @return the type ID, or null if it does not exist
     */
    fun getTypeIdFromItemData(itemData: Map<String, Any?>): String? {
        val typeData = itemData["sertraline:type"]
        return when (typeData) {
            is Type -> typeData.id
            is String -> typeData
            else -> null
        }
    }

    /**
     * Get a Type object from item data
     * @param itemData Item data
     * @return the Type object, or null if it does not exist
     */
    fun getTypeFromItemData(itemData: Map<String, Any?>): Type? {
        return getTypeIdFromItemData(itemData)?.let { getType(it) }
    }

    /**
     * Get the type ID from an item (ItemStack / ModernSItem / item data Map)
     * @param item the item
     * @return the type ID, or null if it cannot be recognized
     */
    @Suppress("UNCHECKED_CAST")
    fun getItemTypeId(item: Any): String? {
        return when (item) {
            is ModernSItem -> {
                when (val typeData = item.getDeepData("sertraline:type")) {
                    is Type -> typeData.id
                    is String -> typeData
                    else -> null
                }
            }
            is ItemStack -> {
                val tag = item.getItemTag(true)
                tag["sertraline_type"]?.asString()?.let { return it }
                tag["sertraline_id"]?.asString()?.let { sId ->
                    itemMap[sId]?.let { return getItemTypeId(it) }
                }
                null
            }
            is Map<*, *> -> getTypeIdFromItemData(item as Map<String, Any?>)
            else -> null
        }
    }

    /**
     * Check whether an item's type exactly matches the given type ID (no inheritance, case-insensitive)
     * @param item the item (ItemStack / ModernSItem / item data Map)
     * @param typeId the type ID to match, e.g. "weapon"
     * @return true on an exact match; otherwise false
     */
    fun isItemIsType(item: Any, typeId: String): Boolean {
        val itemTypeId = getItemTypeId(item) ?: return false
        return itemTypeId.equals(typeId, ignoreCase = true)
    }

    /**
     * Check whether an item's type inherits from the given type ID (with inheritance, e.g. returns true when sword inherits from weapon, case-insensitive)
     * @param item the item (ItemStack / ModernSItem / item data Map)
     * @param typeId the type ID to match, e.g. "weapon"
     * @return true if the item type is typeId or inherits from typeId; otherwise false
     */
    fun isItemIsExtendType(item: Any, typeId: String): Boolean {
        val itemTypeId = getItemTypeId(item) ?: return false
        return isAssignableFrom(itemTypeId, typeId)
    }
}