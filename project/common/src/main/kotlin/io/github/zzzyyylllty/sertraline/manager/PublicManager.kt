package io.github.zzzyyylllty.sertraline.manager

import io.github.zzzyyylllty.sertraline.Sertraline
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.data.deserializeSItem
import java.util.concurrent.ConcurrentHashMap

class PublicManager {

    private val temporaryCache = ConcurrentHashMap<String, ModernSItem>()
    private val survivalBuffer = LinkedHashMap<String, String>()

    fun getItem(id: String, sub: SubManagerType): ModernSItem? {
        return when (sub) {
            SubManagerType.PERSISTENT -> Sertraline.itemMap[id]
            SubManagerType.TEMPORARY -> temporaryCache[id] ?: Sertraline.itemMap[id]
        }
    }

    fun getItem(id: String): ModernSItem? {
        return Sertraline.itemMap[id] ?: temporaryCache[id]
    }

    fun getAll(sub: SubManagerType): Map<String, ModernSItem> {
        return when (sub) {
            SubManagerType.PERSISTENT -> Sertraline.itemMap
            SubManagerType.TEMPORARY -> temporaryCache
        }
    }

    fun createTemporary(id: String, item: ModernSItem) {
        if (temporaryCache.containsKey(id) || Sertraline.itemMap.containsKey(id)) {
            throw IllegalStateException("Item $id already exists in public manager")
        }
        temporaryCache[id] = item
        Sertraline.itemMap[id] = item
    }

    fun remove(id: String, sub: SubManagerType): Boolean {
        return when (sub) {
            SubManagerType.PERSISTENT -> {
                if (!ManagerConfig.allowDeletePublicPersistent) {
                    throw IllegalStateException("Deleting public-persistent items is not allowed")
                }
                Sertraline.itemMap.remove(id) != null
            }
            SubManagerType.TEMPORARY -> {
                temporaryCache.remove(id)
                Sertraline.itemMap.remove(id) != null
            }
        }
    }

    fun preReload() {
        survivalBuffer.clear()
        temporaryCache.forEach { (id, item) ->
            item.serialize()?.let { survivalBuffer[id] = it }
        }
        temporaryCache.clear()
    }

    fun postReload() {
        survivalBuffer.forEach { (id, json) ->
            try {
                val item = deserializeSItem(json)
                temporaryCache[id] = item
                Sertraline.itemMap[id] = item
            } catch (_: Exception) {
                // skip corrupted serialized items
            }
        }
        survivalBuffer.clear()
    }
}
