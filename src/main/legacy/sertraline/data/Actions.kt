package io.github.zzzyyylllty.sertraline.data

import kotlinx.serialization.Serializable

@Serializable
data class Action(
    var trigger: String,
    var async: Boolean,
    var actionType: ActionType,
    var require: List<String>,
    var actions: List<String>
)
@Serializable
enum class ActionType {
    KETHER,
    JAVASCRIPT
}