package io.github.zzzyyylllty.sertraline.load

import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.Sertraline.packMap
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.logger.infoL
import io.github.zzzyyylllty.sertraline.logger.infoS
import io.github.zzzyyylllty.sertraline.logger.warningL
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFile
import taboolib.module.lang.asLangText
import java.io.File
import taboolib.module.configuration.Configuration
import kotlin.collections.set


fun loadPackFiles() {
    infoL("PackLoad")
    if (!File(getDataFolder(), "packs").exists()) {
        warningL("PackLoadRegen")
        releaseResourceFile("packs/chotenpack.yml")
    }
    val files = File(getDataFolder(), "packs").listFiles()
    if (files == null) {
        warningL("PackLoadNotFound")
        return
    }
    infoL("PackLoad")
    for (file in files) {
        // If directory load file in it...
        if (file.isDirectory) file.listFiles()?.forEach {
            loadpackFile(it)
        }
        else loadpackFile(file)
    }
}
fun loadpackFile(file: File) {
    val config = Configuration.loadFromFile(file)

    devLog(console.asLangText("DebugLoadingFile", file.name))

    for (iroot in config.getKeys(false)) {

        devLog(console.asLangText("DebugLoadingPack", file.name.toString(), iroot.toString()))
        val pack = loadPack(config, iroot)
        packMap[iroot] = pack

    }
}