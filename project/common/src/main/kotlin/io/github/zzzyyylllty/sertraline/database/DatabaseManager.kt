package io.github.zzzyyylllty.sertraline.database

import io.github.zzzyyylllty.sertraline.Sertraline
import taboolib.expansion.mapper
import java.io.File

object DatabaseManager {

    private val dbConfig: Any by lazy {
        val cfg = Sertraline.config
        if (cfg.getBoolean("database.enable", false)) {
            cfg.getConfigurationSection("database")!!
        } else {
            File("${cfg.getString("database.filename") ?: "data"}.db")
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
