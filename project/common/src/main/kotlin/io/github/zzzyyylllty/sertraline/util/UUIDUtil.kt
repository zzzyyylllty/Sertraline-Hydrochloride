package io.github.zzzyyylllty.sertraline.util

import java.util.UUID

object UUIDUtil {

    fun random(): UUID = UUID.randomUUID()

    fun fromString(str: String): UUID = UUID.fromString(str)

    fun fromStringOrNull(str: String): UUID? = try {
        UUID.fromString(str)
    } catch (_: IllegalArgumentException) {
        null
    }

    fun nameUUIDFromBytes(name: ByteArray): UUID = UUID.nameUUIDFromBytes(name)

    fun nameUUIDFromString(name: String): UUID = UUID.nameUUIDFromBytes(name.toByteArray())

    fun isValid(str: String): Boolean = try {
        UUID.fromString(str)
        true
    } catch (_: IllegalArgumentException) {
        false
    }

    fun toDashless(uuid: UUID): String = uuid.toString().replace("-", "")

    fun toDashless(str: String): String = fromString(str).toString().replace("-", "")

    fun equals(a: UUID, b: UUID): Boolean = a == b
}
