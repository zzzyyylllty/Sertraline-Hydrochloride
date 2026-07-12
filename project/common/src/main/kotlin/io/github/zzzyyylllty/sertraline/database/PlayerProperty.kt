package io.github.zzzyyylllty.sertraline.database

import taboolib.expansion.Id
import taboolib.expansion.Key
import taboolib.expansion.Length

data class PlayerProperty(
    @Id val uuid: String,
    @Key @Length(64) val propKey: String,
    @Length(-1) var propValue: String = ""
)
