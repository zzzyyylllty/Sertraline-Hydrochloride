package io.github.zzzyyylllty.sertraline.manager

const val PRIVATE_ITEM_PREFIX = "__"
const val PRIVATE_OWNER_TAG = "sertraline_private_owner"

fun normalizePrivateItemId(id: String): String =
    if (id.startsWith(PRIVATE_ITEM_PREFIX)) id else "$PRIVATE_ITEM_PREFIX$id"

fun isPrivateItemId(id: String): Boolean = id.startsWith(PRIVATE_ITEM_PREFIX)
