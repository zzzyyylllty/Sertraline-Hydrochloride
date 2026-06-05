package io.github.zzzyyylllty.sertraline.util

import java.util.UUID

object UUIDUtil {

    @JvmStatic
    fun random(): UUID = UUID.randomUUID()

    @JvmStatic
    fun fromString(str: String): UUID = UUID.fromString(str)

    @JvmStatic
    fun fromStringOrNull(str: String): UUID? = try {
        UUID.fromString(str)
    } catch (_: IllegalArgumentException) {
        null
    }

    @JvmStatic
    fun nameUUIDFromBytes(name: ByteArray): UUID = UUID.nameUUIDFromBytes(name)

    @JvmStatic
    fun nameUUIDFromString(name: String): UUID = UUID.nameUUIDFromBytes(name.toByteArray())

    @JvmStatic
    fun isValid(str: String): Boolean = try {
        UUID.fromString(str)
        true
    } catch (_: IllegalArgumentException) {
        false
    }

    @JvmStatic
    fun toDashless(uuid: UUID): String = uuid.toString().replace("-", "")

    @JvmStatic
    fun toDashless(str: String): String = fromString(str).toString().replace("-", "")

    @JvmStatic
    fun equals(a: UUID, b: UUID): Boolean = a == b
}
