package io.github.zzzyyylllty.sertraline.manager

import io.github.zzzyyylllty.sertraline.database.DatabaseManager
import io.github.zzzyyylllty.sertraline.database.PrivateItem
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.data.deserializeSItem
import io.github.zzzyyylllty.sertraline.util.jsonUtils
import java.util.concurrent.ConcurrentHashMap

class PrivateManager {

    private val temporaryCache = ConcurrentHashMap<String, ConcurrentHashMap<String, ModernSItem>>()
    private val persistentCache = ConcurrentHashMap<String, ConcurrentHashMap<String, ModernSItem>>()

    fun getItem(uuid: String, id: String, sub: SubManagerType): ModernSItem? {
        val normalizedId = normalizePrivateItemId(id)
        return when (sub) {
            SubManagerType.TEMPORARY -> temporaryCache[uuid]?.get(normalizedId)
            SubManagerType.PERSISTENT -> {
                // DB 加载结果回写缓存，避免重复 getItem 反复打数据库
                val map = persistentCache.computeIfAbsent(uuid) { ConcurrentHashMap() }
                map[normalizedId] ?: loadFromDB(
                    uuid,
                    normalizedId,
                    id.takeUnless { it == normalizedId }
                )?.also { map[normalizedId] = it }
            }
        }
    }

    fun getAll(uuid: String, sub: SubManagerType): Map<String, ModernSItem> {
        return when (sub) {
            SubManagerType.TEMPORARY -> temporaryCache[uuid] ?: emptyMap()
            SubManagerType.PERSISTENT -> {
                val cached = persistentCache[uuid]
                if (cached != null) return cached
                loadAllFromDB(uuid)
                persistentCache[uuid] ?: emptyMap()
            }
        }
    }

    fun createItem(uuid: String, id: String, item: ModernSItem, sub: SubManagerType) {
        val normalizedId = normalizePrivateItemId(id)
        val normalizedItem = normalizeItem(normalizedId, item)
        when (sub) {
            SubManagerType.TEMPORARY -> {
                val map = temporaryCache.computeIfAbsent(uuid) { ConcurrentHashMap() }
                if (map.containsKey(normalizedId)) {
                    throw IllegalStateException("Item $normalizedId already exists in private/temporary for UUID $uuid")
                }
                map[normalizedId] = normalizedItem
            }
            SubManagerType.PERSISTENT -> {
                val map = persistentCache.computeIfAbsent(uuid) { ConcurrentHashMap() }
                if (map.containsKey(normalizedId)) {
                    throw IllegalStateException("Item $normalizedId already exists in private/persistent for UUID $uuid")
                }
                map[normalizedId] = normalizedItem
                saveToDB(uuid, normalizedId, normalizedItem, "")
            }
        }
    }

    fun deleteItem(uuid: String, id: String, sub: SubManagerType): Boolean {
        val normalizedId = normalizePrivateItemId(id)
        val legacyId = id.takeUnless { it == normalizedId }
        return when (sub) {
            SubManagerType.TEMPORARY -> {
                temporaryCache[uuid]?.remove(normalizedId) != null
            }
            SubManagerType.PERSISTENT -> {
                persistentCache[uuid]?.remove(normalizedId)
                deleteFromDB(uuid, normalizedId, legacyId)
                true
            }
        }
    }

    fun clearAll(uuid: String) {
        temporaryCache.remove(uuid)
        persistentCache.remove(uuid)
    }

    fun shutdown() {
        temporaryCache.clear()
        persistentCache.clear()
    }

    private fun loadFromDB(uuid: String, id: String, legacyId: String? = null): ModernSItem? {
        return try {
            val ids = sequenceOf(id, legacyId).filterNotNull().distinct()
            for (candidateId in ids) {
                val probe = PrivateItem(uuid = uuid, itemId = candidateId, itemData = "", subPath = "", createdAt = 0L)
                val result = DatabaseManager.privateItemMapper.findOneByKey(probe) ?: continue
                return normalizeItem(id, deserializeSItem(result.itemData))
            }
            null
        } catch (_: Exception) {
            null
        }
    }

    private fun loadAllFromDB(uuid: String) {
        try {
            val items = DatabaseManager.privateItemMapper.findAll(uuid)
            val map = ConcurrentHashMap<String, ModernSItem>()
            for (pi in items) {
                try {
                    val normalizedId = normalizePrivateItemId(pi.itemId)
                    map[normalizedId] = normalizeItem(normalizedId, deserializeSItem(pi.itemData))
                } catch (_: Exception) { }
            }
            persistentCache[uuid] = map
        } catch (_: Exception) { }
    }

    private fun saveToDB(uuid: String, id: String, item: ModernSItem, subPath: String) {
        try {
            val json = item.serialize() ?: throw IllegalStateException("Failed to serialize item $id")
            val entity = PrivateItem(
                uuid = uuid,
                itemId = id,
                itemData = json,
                subPath = subPath,
                createdAt = System.currentTimeMillis()
            )
            DatabaseManager.privateItemMapper.insert(entity)
        } catch (e: Exception) {
            throw IllegalStateException("Failed to save item $id to database: ${e.message}")
        }
    }

    private fun deleteFromDB(uuid: String, id: String, legacyId: String? = null) {
        try {
            val ids = sequenceOf(id, legacyId).filterNotNull().distinct()
            ids.forEach { candidateId ->
                val probe = PrivateItem(uuid = uuid, itemId = candidateId, itemData = "", subPath = "", createdAt = 0L)
                DatabaseManager.privateItemMapper.deleteByKey(probe)
            }
        } catch (_: Exception) { }
    }

    private fun normalizeItem(id: String, item: ModernSItem): ModernSItem {
        if (item.key == id) return item
        val copy = item.deepCopy()
        return ModernSItem(id, copy.data, copy.config)
    }
}
