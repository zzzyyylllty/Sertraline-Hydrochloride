package io.github.zzzyyylllty.sertraline.data

import io.github.zzzyyylllty.sertraline.Sertraline.tiers
import io.github.zzzyyylllty.sertraline.util.toBooleanTolerance

/**
 * Quality utility, callable from JS/Kether and other scripts via TierUtil
 */
object TierUtil {

    /**
     * Gets a quality object
     * @param tierId the quality ID
     * @return the quality object, or null if it does not exist
     */
    fun getTier(tierId: String): Tier? {
        return tiers[tierId]
    }

    /**
     * Gets quality metadata
     * @param tierId the quality ID
     * @param key the meta key
     * @return the meta value, or null if the quality or key does not exist
     */
    fun getMeta(tierId: String, key: String): Any? {
        return tiers[tierId]?.meta?.get(key)
    }

    /**
     * Gets quality metadata and converts it to a Boolean (lenient parsing of true/yes/1 etc.)
     * @param tierId the quality ID
     * @param key the meta key
     * @return the parse result, or false if it does not exist
     */
    fun getMetaAsBoolean(tierId: String, key: String): Boolean {
        return getMeta(tierId, key)?.toBooleanTolerance() ?: false
    }

    /**
     * Gets quality metadata and converts it to a String
     * @param tierId the quality ID
     * @param key the meta key
     * @return the string value, or null if it does not exist
     */
    fun getMetaAsString(tierId: String, key: String): String? {
        return getMeta(tierId, key)?.toString()
    }

    /**
     * Gets quality metadata and converts it to an Int
     * @param tierId the quality ID
     * @param key the meta key
     * @return the integer value, or null if it does not exist or is not numeric
     */
    fun getMetaAsInt(tierId: String, key: String): Int? {
        return (getMeta(tierId, key) as? Number)?.toInt()
            ?: getMetaAsString(tierId, key)?.toIntOrNull()
    }

    /**
     * Gets quality metadata and converts it to a Double
     * @param tierId the quality ID
     * @param key the meta key
     * @return the floating-point value, or null if it does not exist or is not numeric
     */
    fun getMetaAsDouble(tierId: String, key: String): Double? {
        return (getMeta(tierId, key) as? Number)?.toDouble()
            ?: getMetaAsString(tierId, key)?.toDoubleOrNull()
    }

    /**
     * Gets quality metadata and converts it to a List
     * @param tierId the quality ID
     * @param key the meta key
     * @return the list value, or null if it does not exist or is not a list
     */
    @Suppress("UNCHECKED_CAST")
    fun getMetaAsList(tierId: String, key: String): List<Any?>? {
        return getMeta(tierId, key) as? List<Any?>
    }
}
