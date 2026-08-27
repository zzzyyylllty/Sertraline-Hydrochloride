package io.github.zzzyyylllty.sertraline.database

import taboolib.expansion.Alias
import taboolib.expansion.Id
import taboolib.expansion.Length

data class CraftingSession(
    @Id val uuid: String,
    @Length(64) @Alias("station_id") var stationId: String,
    @Length(64) @Alias("recipe_id") var recipeId: String,
    @Alias("start_time") var startTime: Long,
    @Alias("total_seconds") var totalSeconds: Double,
    @Length(-1) @Alias("consumed_items_blob") var consumedItemsBlob: String
)
