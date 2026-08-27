package io.github.zzzyyylllty.sertraline.function.update

import io.github.zzzyyylllty.sertraline.Sertraline
import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.dataFolder
import io.github.zzzyyylllty.sertraline.Sertraline.itemExpectedRevision
import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.logger.infoL
import io.github.zzzyyylllty.sertraline.logger.warningL
import io.github.zzzyyylllty.sertraline.util.jsonUtils
import java.io.File
import java.security.MessageDigest

private data class RevisionEntry(
    val hash: String,
    val revision: Int
)

/**
 * Initializes the revision auto-tracker.
 * Called in reloadCustomConfig after all items are loaded.
 *
 * Computes a content hash for each item in itemMap and compares it with the last stored hash.
 * If the hash changed, increments the revision number and updates itemExpectedRevision.
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
        // skip auto-tracking if a revision-id is explicitly specified
        val explicit = sItem.getDeepData("sertraline:revision-id")
        if (explicit is Number) {
            itemExpectedRevision[itemId] = explicit.toInt()
            continue
        }

        val hash = computeItemHash(sItem)
        current[itemId] = RevisionEntry(hash, 0)

        val storedEntry = stored[itemId]
        if (storedEntry == null) {
            // new item, initialize to revision 1
            current[itemId] = RevisionEntry(hash, 1)
            itemExpectedRevision[itemId] = 1
            updatedCount++
        } else if (storedEntry.hash != hash) {
            // item content changed, increment revision
            val newRevision = storedEntry.revision + 1
            current[itemId] = RevisionEntry(hash, newRevision)
            itemExpectedRevision[itemId] = newRevision
            updatedCount++
        } else {
            // unchanged, keep the previous revision
            current[itemId] = RevisionEntry(hash, storedEntry.revision)
            itemExpectedRevision[itemId] = storedEntry.revision
        }
    }

    // save the updated tracking data
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
        warningL("Warning_Revision_Tracker_Save_Failed", e.message ?: e.javaClass.simpleName)
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
 * Resets the revision tracker (forces every item to be treated as "changed" next time).
 * Used for an admin-triggered full update.
 */
fun resetRevisionAutoTracker() {
    val revisionDir = getRevisionDir()
    if (revisionDir.exists()) {
        val trackerFile = File(revisionDir, "auto-track.json")
        if (trackerFile.exists()) trackerFile.delete()
    }
    itemExpectedRevision.clear()
}
