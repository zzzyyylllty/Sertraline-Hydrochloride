package io.github.zzzyyylllty.sertraline.util.serialize

import java.util.UUID

fun String.toUUID(): UUID {
    return UUID.fromString(this)
}
