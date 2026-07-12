package io.github.zzzyyylllty.sertraline.database

import taboolib.expansion.Id
import taboolib.expansion.Length

data class CraftingSession(
    @Id val uuid: String,
    @Length(64) var stationId: String = "",
    @Length(64) var recipeId: String = "",
    var startTime: Long = 0,
    var totalSeconds: Double = 0.0,
    @Length(-1) var consumedItemsBlob: String = ""
)
