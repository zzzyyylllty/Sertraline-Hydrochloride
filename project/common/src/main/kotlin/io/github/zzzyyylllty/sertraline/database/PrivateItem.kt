package io.github.zzzyyylllty.sertraline.database

import taboolib.expansion.Id
import taboolib.expansion.Key
import taboolib.expansion.Length

data class PrivateItem(
    @Id val uuid: String,
    @Key @Length(64) val itemId: String,
    @Length(-1) var itemData: String = "",
    var subPath: String = "",
    var createdAt: Long = 0
)
