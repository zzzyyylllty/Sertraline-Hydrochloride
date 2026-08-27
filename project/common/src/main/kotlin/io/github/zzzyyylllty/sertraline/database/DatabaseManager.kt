package io.github.zzzyyylllty.sertraline.database

import io.github.zzzyyylllty.sertraline.Sertraline
import taboolib.common.io.newFile
import taboolib.common.platform.function.getDataFolder
import taboolib.expansion.mapper

object DatabaseManager {

    private val dbConfig: Any by lazy {
        val cfg = Sertraline.config
        if (cfg.getBoolean("database.enable", false)) {
            cfg.getConfigurationSection("database")!!
        } else {
            // SQLite 文件必须落在插件数据目录（plugins/Sertraline/）下，并用 newFile 预先创建；
            // 之前直接 File("...").db 使用相对路径，SQLite 驱动会把它建到服务器工作目录甚至因父目录不存在而无法创建
            newFile(getDataFolder(), "${cfg.getString("database.filename") ?: "data"}.db")
        }
    }

    val propertyMapper by mapper<PlayerProperty>(dbConfig)
    val cooldownMapper by mapper<PlayerCooldown>(dbConfig)
    val sessionMapper by mapper<CraftingSession>(dbConfig)
    val privateItemMapper by mapper<PrivateItem>(dbConfig)

    fun init() {
        propertyMapper.count()
        cooldownMapper.count()
        sessionMapper.count()
        privateItemMapper.count()
    }
}
