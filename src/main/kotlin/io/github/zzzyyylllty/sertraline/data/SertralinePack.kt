package io.github.zzzyyylllty.sertraline.data

import ink.ptms.adyeshach.impl.description.Description
import io.github.zzzyyylllty.sertraline.function.sertralize.AnySerializer
import kotlinx.serialization.Serializable

@Serializable
data class SertralinePack(
    val enabled: Boolean,
    val name: String,
    val namespace: String,
    val description: String,
    val authors: List<String>,
    val version: String,
) {
}
