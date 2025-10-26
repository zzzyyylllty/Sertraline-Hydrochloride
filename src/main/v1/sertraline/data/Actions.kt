package io.github.zzzyyylllty.sertraline.data




data class Action(
    var trigger: String,
    var async: Boolean,
    var actionType: ActionType,
    var require: List<String>,
    var actions: List<String>
)

enum class ActionType {
    KETHER,
    JAVASCRIPT
}