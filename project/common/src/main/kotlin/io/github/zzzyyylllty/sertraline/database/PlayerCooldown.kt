package io.github.zzzyyylllty.sertraline.database

import taboolib.expansion.Alias
import taboolib.expansion.Id
import taboolib.expansion.Key
import taboolib.expansion.Length

data class PlayerCooldown(
    @Id val uuid: String,
    @Key @Length(64) @Alias("cooldown_id") val cooldownId: String,
    @Alias("expiry") var expiry: Long
)
