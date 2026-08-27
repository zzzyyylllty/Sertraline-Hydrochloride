package io.github.zzzyyylllty.sertraline.database

import taboolib.expansion.Alias
import taboolib.expansion.Id
import taboolib.expansion.Key
import taboolib.expansion.Length

data class PrivateItem(
    @Id val uuid: String,
    @Key @Length(64) @Alias("item_id") val itemId: String,
    @Length(-1) @Alias("item_data") var itemData: String,
    @Alias("sub_path") var subPath: String,
    @Alias("created_at") var createdAt: Long
)
