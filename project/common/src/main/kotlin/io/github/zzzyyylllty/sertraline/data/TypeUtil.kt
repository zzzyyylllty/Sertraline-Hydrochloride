package io.github.zzzyyylllty.sertraline.data

import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.Sertraline.types
import io.github.zzzyyylllty.sertraline.util.toBooleanTolerance
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.getItemTag

/**
 * 类型工具类，提供类型继承、元数据相关功能，可在 JS/Kether 等脚本中通过 TypeUtil 调用
 */
object TypeUtil {

    /**
     * 大小写不敏感地解析类型ID，兼容大小写不匹配的配置
     */
    private fun resolveTypeId(typeId: String): String? {
        if (types.containsKey(typeId)) return typeId
        return types.keys.firstOrNull { it.equals(typeId, ignoreCase = true) }
    }

    /**
     * 获取类型对象（大小写不敏感）
     * @param typeId 类型ID
     * @return 类型对象，不存在时返回 null
     */
    fun getType(typeId: String): Type? {
        return resolveTypeId(typeId)?.let { types[it] }
    }

    /**
     * 获取类型元数据
     * @param typeId 类型ID
     * @param key meta 键
     * @return meta 值，类型或键不存在时返回 null
     */
    fun getMeta(typeId: String, key: String): Any? {
        return getType(typeId)?.meta?.get(key)
    }

    /**
     * 获取类型元数据并转为 Boolean（宽容解析 true/yes/1 等）
     * @param typeId 类型ID
     * @param key meta 键
     * @return 解析结果，不存在时返回 false
     */
    fun getMetaAsBoolean(typeId: String, key: String): Boolean {
        return getMeta(typeId, key)?.toBooleanTolerance() ?: false
    }

    /**
     * 获取类型元数据并转为 String
     * @param typeId 类型ID
     * @param key meta 键
     * @return 字符串值，不存在时返回 null
     */
    fun getMetaAsString(typeId: String, key: String): String? {
        return getMeta(typeId, key)?.toString()
    }

    /**
     * 获取类型元数据并转为 Int
     * @param typeId 类型ID
     * @param key meta 键
     * @return 整数值，不存在或非数字时返回 null
     */
    fun getMetaAsInt(typeId: String, key: String): Int? {
        return (getMeta(typeId, key) as? Number)?.toInt()
            ?: getMetaAsString(typeId, key)?.toIntOrNull()
    }

    /**
     * 获取类型元数据并转为 Double
     * @param typeId 类型ID
     * @param key meta 键
     * @return 浮点值，不存在或非数字时返回 null
     */
    fun getMetaAsDouble(typeId: String, key: String): Double? {
        return (getMeta(typeId, key) as? Number)?.toDouble()
            ?: getMetaAsString(typeId, key)?.toDoubleOrNull()
    }

    /**
     * 获取类型元数据并转为 List
     * @param typeId 类型ID
     * @param key meta 键
     * @return 列表值，不存在或非列表时返回 null
     */
    @Suppress("UNCHECKED_CAST")
    fun getMetaAsList(typeId: String, key: String): List<Any?>? {
        return getMeta(typeId, key) as? List<Any?>
    }


    /**
     * 获取类型的所有祖先（包括自身，大小写不敏感）
     * @param typeId 类型ID
     * @return 祖先类型ID列表，按从自身到根的顺序（第一个元素是自身）
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
     * 检查类型是否继承自另一个类型（或相同）
     * @param typeId 要检查的类型ID
     * @param potentialAncestorId 可能的祖先类型ID
     * @return 如果typeId继承自potentialAncestorId（或相同）则返回true
     */
    fun isAssignableFrom(typeId: String, potentialAncestorId: String): Boolean {
        val resolved = resolveTypeId(potentialAncestorId) ?: potentialAncestorId
        return getAncestors(typeId).any { it.equals(resolved, ignoreCase = true) }
    }

    /**
     * 获取类型的所有后代（包括自身）
     * 注意：此操作需要遍历所有类型，性能较差，仅适用于类型数量较少的情况
     * @param typeId 类型ID
     * @return 后代类型ID列表
     */
    fun getDescendants(typeId: String): List<String> {
        return types.keys.filter { isAssignableFrom(it, typeId) }
    }

    /**
     * 从物品数据中获取类型ID
     * @param itemData 物品数据
     * @return 类型ID，如果不存在则返回null
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
     * 从物品数据中获取类型对象
     * @param itemData 物品数据
     * @return 类型对象，如果不存在则返回null
     */
    fun getTypeFromItemData(itemData: Map<String, Any?>): Type? {
        return getTypeIdFromItemData(itemData)?.let { getType(it) }
    }

    /**
     * 从物品（ItemStack / ModernSItem / 物品数据Map）中获取类型ID
     * @param item 物品
     * @return 类型ID，无法识别时返回 null
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
     * 检查物品的类型是否与指定类型完全一致（不包含继承，大小写不敏感）
     * @param item 物品（ItemStack / ModernSItem / 物品数据Map）
     * @param typeId 要匹配的类型ID，例如 "weapon"
     * @return 完全一致返回 true；否则返回 false
     */
    fun isItemIsType(item: Any, typeId: String): Boolean {
        val itemTypeId = getItemTypeId(item) ?: return false
        return itemTypeId.equals(typeId, ignoreCase = true)
    }

    /**
     * 检查物品的类型是否继承自指定类型（包含继承，例如 sword 继承 weapon 时返回 true，大小写不敏感）
     * @param item 物品（ItemStack / ModernSItem / 物品数据Map）
     * @param typeId 要匹配的类型ID，例如 "weapon"
     * @return 物品类型为 typeId 或继承自 typeId 时返回 true；否则返回 false
     */
    fun isItemIsExtendType(item: Any, typeId: String): Boolean {
        val itemTypeId = getItemTypeId(item) ?: return false
        return isAssignableFrom(itemTypeId, typeId)
    }
}