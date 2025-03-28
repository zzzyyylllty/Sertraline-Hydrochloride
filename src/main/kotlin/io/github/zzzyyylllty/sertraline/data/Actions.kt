package io.github.zzzyyylllty.sertraline.data

import taboolib.library.xseries.XMaterial
data class Action(
    var trigger: String,
    var async: Boolean,
    var actions: List<String>
)
enum class ActionType {
    KETHER,
    SKILL_MYTHIC,
    JAVASCRIPT
}