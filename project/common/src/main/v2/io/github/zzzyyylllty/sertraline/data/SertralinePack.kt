package io.github.zzzyyylllty.sertraline.data


data class SertralinePack(
    val enabled: Boolean,
    val name: String,
    val namespace: String,
    val description: String,
    val authors: List<String>,
    val version: String,
) {
}
