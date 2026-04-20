package io.github.zzzyyylllty.sertraline.config

import io.github.zzzyyylllty.sertraline.Sertraline
import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.event.ItemLoadEvent
import io.github.zzzyyylllty.sertraline.logger.infoL
import io.github.zzzyyylllty.sertraline.logger.infoS
import io.github.zzzyyylllty.sertraline.logger.severeL
import io.github.zzzyyylllty.sertraline.logger.warningL
import io.github.zzzyyylllty.sertraline.util.serialize.parseToMap
// import org.yaml.snakeyaml.Yaml
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFile
import taboolib.common.platform.function.releaseResourceFolder
import taboolib.common.platform.function.warning
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.io.File


fun loadItemFiles(incremental: Boolean = config.getBoolean("incremental-loading.enable", false)) {
    infoL("Item_Load")

    if (!File(getDataFolder(), "workspace").exists()) {
        warningL("Item_Load_Regen")
        releaseResourceFolder("workspace/default")
    }

    val workspaceDir = File(getDataFolder(), "workspace")

    if (incremental) {
        loadItemFilesIncremental(workspaceDir)
    } else {
        loadItemFilesFull(workspaceDir)
    }

    infoL("Item_Load_Complete", itemMap.size)
}

private fun loadItemFilesFull(workspaceDir: File) {
    if (!workspaceDir.exists()) {
        severeL("Item_Load_Not_Found")
        return
    }

    workspaceDir.walk()
        .filter { it.isFile }
        .forEach { file ->
            loadItemFile(file)
        }
}

private fun loadItemFilesIncremental(workspaceDir: File) {
    val filePattern = config["file-load.item"]?.toString() ?: ".*"
    val files = workspaceDir.walk()
        .filter { it.isFile && checkRegexMatch(it.name, filePattern) }
        .toList()

    var loadedCount = 0
    var skippedCount = 0

    for (file in files) {
        val lastModified = Sertraline.fileLastModified[file.absolutePath]
        val currentModified = file.lastModified()

        // 只加载新文件或修改过的文件
        if (lastModified == null || lastModified < currentModified) {
            if (loadItemFile(file)) {
                Sertraline.fileLastModified[file.absolutePath] = currentModified
                loadedCount++
            }
        } else {
            skippedCount++
        }
    }

    infoL("Item_Load_Incremental_Stats", loadedCount, skippedCount, files.size)
}

fun loadItemFile(file: File): Boolean {
    if (file.isDirectory) {
        file.listFiles()?.forEach {
            loadItemFile(it)
        }
        return false
    }

    val regex = (config["file-load.item"] ?: ".*").toString()
    if (!checkRegexMatch(file.name, regex)) {
        return false
    }

    return try {
        val map = multiExtensionLoader(file)

        if (map.isNullOrEmpty()) {
            severeL("Item_Load_Error_Empty", file.name)
            return false
        }

        for (entry in map.entries) {
            val key = entry.key
            val value = map[key]
            ItemLoadEvent(key, value as? Map<String, Any?>? ?: linkedMapOf(), linkedMapOf()).call()
        }

        true
    } catch (e: Exception) {
        severeL("Item_Load_Error_Parse", file.name, e.message ?: "Unknown error")
        false
    }
}