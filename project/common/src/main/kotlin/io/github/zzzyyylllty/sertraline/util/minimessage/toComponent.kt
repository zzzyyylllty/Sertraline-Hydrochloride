package io.github.zzzyyylllty.sertraline.util.minimessage

import net.kyori.adventure.text.Component

fun String.toComponent(): Component {
    return mmUtil.deserialize(this)
}