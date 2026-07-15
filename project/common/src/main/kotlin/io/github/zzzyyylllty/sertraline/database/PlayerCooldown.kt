package io.github.zzzyyylllty.sertraline.database

import taboolib.expansion.Id
import taboolib.expansion.Key
import taboolib.expansion.Length

data class PlayerCooldown(
    @Id val uuid: String,
    @Key @Length(64) val cooldownId: String,
    var expiry: Long
)
