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
        return when (sub) {
            SubManagerType.TEMPORARY -> temporaryCache[uuid]?.get(id)
            SubManagerType.PERSISTENT -> {
                persistentCache[uuid]?.get(id) ?: loadFromDB(uuid, id)
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
        when (sub) {
            SubManagerType.TEMPORARY -> {
                val map = temporaryCache.computeIfAbsent(uuid) { ConcurrentHashMap() }
                if (map.containsKey(id)) {
                    throw IllegalStateException("Item $id already exists in private/temporary for UUID $uuid")
                }
                map[id] = item
            }
            SubManagerType.PERSISTENT -> {
                val map = persistentCache.computeIfAbsent(uuid) { ConcurrentHashMap() }
                if (map.containsKey(id)) {
                    throw IllegalStateException("Item $id already exists in private/persistent for UUID $uuid")
                }
                map[id] = item
                saveToDB(uuid, id, item, "")
            }
        }
    }

    fun deleteItem(uuid: String, id: String, sub: SubManagerType): Boolean {
        return when (sub) {
            SubManagerType.TEMPORARY -> {
                temporaryCache[uuid]?.remove(id) != null
            }
            SubManagerType.PERSISTENT -> {
                persistentCache[uuid]?.remove(id)
                deleteFromDB(uuid, id)
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

    private fun loadFromDB(uuid: String, id: String): ModernSItem? {
        return try {
            val probe = PrivateItem(uuid = uuid, itemId = id, itemData = "")
            val result = DatabaseManager.privateItemMapper.findOneByKey(probe) ?: return null
            deserializeSItem(result.itemData)
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
                    map[pi.itemId] = deserializeSItem(pi.itemData)
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

    private fun deleteFromDB(uuid: String, id: String) {
        try {
            val probe = PrivateItem(uuid = uuid, itemId = id, itemData = "")
            DatabaseManager.privateItemMapper.deleteByKey(probe)
        } catch (_: Exception) { }
    }
}
