package io.github.zzzyyylllty.sertraline.data

import io.github.zzzyyylllty.sertraline.Sertraline.tiers
import io.github.zzzyyylllty.sertraline.util.toBooleanTolerance

/**
 * 品质工具类，可在 JS/Kether 等脚本中通过 TierUtil 调用
 */
object TierUtil {

    /**
     * 获取品质对象
     * @param tierId 品质ID
     * @return 品质对象，不存在时返回 null
     */
    fun getTier(tierId: String): Tier? {
        return tiers[tierId]
    }

    /**
     * 获取品质元数据
     * @param tierId 品质ID
     * @param key meta 键
     * @return meta 值，品质或键不存在时返回 null
     */
    fun getMeta(tierId: String, key: String): Any? {
        return tiers[tierId]?.meta?.get(key)
    }

    /**
     * 获取品质元数据并转为 Boolean（宽容解析 true/yes/1 等）
     * @param tierId 品质ID
     * @param key meta 键
     * @return 解析结果，不存在时返回 false
     */
    fun getMetaAsBoolean(tierId: String, key: String): Boolean {
        return getMeta(tierId, key)?.toBooleanTolerance() ?: false
    }

    /**
     * 获取品质元数据并转为 String
     * @param tierId 品质ID
     * @param key meta 键
     * @return 字符串值，不存在时返回 null
     */
    fun getMetaAsString(tierId: String, key: String): String? {
        return getMeta(tierId, key)?.toString()
    }

    /**
     * 获取品质元数据并转为 Int
     * @param tierId 品质ID
     * @param key meta 键
     * @return 整数值，不存在或非数字时返回 null
     */
    fun getMetaAsInt(tierId: String, key: String): Int? {
        return (getMeta(tierId, key) as? Number)?.toInt()
            ?: getMetaAsString(tierId, key)?.toIntOrNull()
    }

    /**
     * 获取品质元数据并转为 Double
     * @param tierId 品质ID
     * @param key meta 键
     * @return 浮点值，不存在或非数字时返回 null
     */
    fun getMetaAsDouble(tierId: String, key: String): Double? {
        return (getMeta(tierId, key) as? Number)?.toDouble()
            ?: getMetaAsString(tierId, key)?.toDoubleOrNull()
    }

    /**
     * 获取品质元数据并转为 List
     * @param tierId 品质ID
     * @param key meta 键
     * @return 列表值，不存在或非列表时返回 null
     */
    @Suppress("UNCHECKED_CAST")
    fun getMetaAsList(tierId: String, key: String): List<Any?>? {
        return getMeta(tierId, key) as? List<Any?>
    }
}
