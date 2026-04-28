package io.github.zzzyyylllty.sertraline.data

import io.github.zzzyyylllty.sertraline.Sertraline.types

/**
 * 类型工具类，提供类型继承相关功能
 */
object TypeUtil {

    /**
     * 获取类型的所有祖先（包括自身）
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
            current = types[current]?.parent
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
        return getAncestors(typeId).contains(potentialAncestorId)
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
        val typeData = itemData["sertraline:type"]
        return when (typeData) {
            is Type -> typeData
            is String -> types[typeData]
            else -> null
        }
    }
}