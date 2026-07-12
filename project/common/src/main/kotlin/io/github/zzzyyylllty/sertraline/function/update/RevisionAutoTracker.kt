package io.github.zzzyyylllty.sertraline.function.update

import io.github.zzzyyylllty.sertraline.Sertraline
import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.dataFolder
import io.github.zzzyyylllty.sertraline.Sertraline.itemExpectedRevision
import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.logger.infoL
import io.github.zzzyyylllty.sertraline.util.jsonUtils
import org.bukkit.Bukkit
import java.io.File
import java.security.MessageDigest

private data class RevisionEntry(
    val hash: String,
    val revision: Int
)

/**
 * 初始化修订自动追踪器。
 * 在 reloadCustomConfig 中、所有物品加载完成后调用。
 *
 * 为 itemMap 中每个物品计算内容哈希，与上次存储的哈希比较。
 * 若哈希变化则递增修订号，并更新 itemExpectedRevision。
 */
fun initRevisionAutoTracker() {
    if (!config.getBoolean("item-update.auto-track", true)) return

    val revisionDir = getRevisionDir()
    if (!revisionDir.exists()) revisionDir.mkdirs()

    val trackerFile = File(revisionDir, "auto-track.json")
    val stored: MutableMap<String, RevisionEntry> = loadTrackerFile(trackerFile)
    val current = mutableMapOf<String, RevisionEntry>()
    var updatedCount = 0

    for ((itemId, sItem) in itemMap) {
        // 如果显式指定了 revision-id，跳过自动追踪
        val explicit = sItem.getDeepData("sertraline:revision-id")
        if (explicit is Number) {
            itemExpectedRevision[itemId] = explicit.toInt()
            continue
        }

        val hash = computeItemHash(sItem)
        current[itemId] = RevisionEntry(hash, 0)

        val storedEntry = stored[itemId]
        if (storedEntry == null) {
            // 新物品，初始化为修订 1
            current[itemId] = RevisionEntry(hash, 1)
            itemExpectedRevision[itemId] = 1
            updatedCount++
        } else if (storedEntry.hash != hash) {
            // 物品内容变更，递增修订
            val newRevision = storedEntry.revision + 1
            current[itemId] = RevisionEntry(hash, newRevision)
            itemExpectedRevision[itemId] = newRevision
            updatedCount++
        } else {
            // 未变更，沿用上次修订
            current[itemId] = RevisionEntry(hash, storedEntry.revision)
            itemExpectedRevision[itemId] = storedEntry.revision
        }
    }

    // 保存更新后的追踪数据
    saveTrackerFile(trackerFile, current)

    if (updatedCount > 0) {
        infoL("Revision auto-tracker: $updatedCount items updated")
    }
    devLog("Revision auto-tracker: ${itemExpectedRevision.size} items tracked")
}

private fun getRevisionDir(): File {
    val dirName = config.getString("item-update.auto-track-dir", "revision") ?: "revision"
    return File(dataFolder, dirName)
}

@Suppress("UNCHECKED_CAST")
private fun loadTrackerFile(file: File): MutableMap<String, RevisionEntry> {
    if (!file.exists()) return mutableMapOf()
    return try {
        val raw = jsonUtils.fromJson(file.readText(), MutableMap::class.java) as? Map<String, Map<String, Any>>
        raw?.mapValues { (_, v) ->
            RevisionEntry(
                hash = v["hash"] as? String ?: "",
                revision = (v["revision"] as? Number)?.toInt() ?: 0
            )
        }?.toMutableMap() ?: mutableMapOf()
    } catch (e: Exception) {
        devLog("Failed to load revision tracker file: ${e.message}")
        mutableMapOf()
    }
}

private fun saveTrackerFile(file: File, data: Map<String, RevisionEntry>) {
    try {
        val raw = data.mapValues { (_, entry) ->
            mapOf("hash" to entry.hash, "revision" to entry.revision)
        }
        file.writeText(jsonUtils.toJson(raw))
    } catch (e: Exception) {
        Bukkit.getLogger().warning("Failed to save revision tracker file: ${e.message}")
    }
}

private fun computeItemHash(sItem: io.github.zzzyyylllty.sertraline.data.ModernSItem): String {
    val configData = sItem.config
    val json = try {
        jsonUtils.toJson(configData)
    } catch (e: Exception) {
        sItem.key // fallback to key if serialization fails
    }
    val digest = MessageDigest.getInstance("SHA-256")
    return digest.digest(json.toByteArray()).joinToString("") { "%02x".format(it) }
}

/**
 * 重置修订追踪（强制所有物品下次被视作"变更"）。
 * 用于管理员手动触发全量更新。
 */
fun resetRevisionAutoTracker() {
    val revisionDir = getRevisionDir()
    if (revisionDir.exists()) {
        val trackerFile = File(revisionDir, "auto-track.json")
        if (trackerFile.exists()) trackerFile.delete()
    }
    itemExpectedRevision.clear()
}
