package io.github.zzzyyylllty.sertraline.manager

enum class ManagerType(val alias: Set<String>) {
    PUBLIC(setOf("public")),
    PRIVATE(setOf("private"));

    companion object {
        fun fromAlias(s: String): ManagerType? =
            values().firstOrNull { s.lowercase() in it.alias }
    }
}

enum class SubManagerType(val alias: Set<String>) {
    PERSISTENT(setOf("persistent", "pers", "p")),
    TEMPORARY(setOf("temporary", "temp", "t"));

    companion object {
        fun fromAlias(s: String): SubManagerType? =
            values().firstOrNull { s.lowercase() in it.alias }
    }
}
