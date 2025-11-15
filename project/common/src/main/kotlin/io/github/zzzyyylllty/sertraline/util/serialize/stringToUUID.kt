package io.github.zzzyyylllty.sertraline.util.serialize

import org.kotlincrypto.hash.sha2.SHA256
import java.util.UUID

fun String.toUUID(): UUID {
    return UUID.fromString(this)
}
fun String.generateUUID(): UUID {
    return UUID.nameUUIDFromBytes(this.toByteArray())
}
fun String.generateHash(): String {
    val sha256 = SHA256()
    sha256.update(this.toByteArray())
    return sha256.digest().joinToString("") { "%02x".format(it) }
}

fun List<String>.generateUUID(): UUID {
    return UUID.nameUUIDFromBytes(this.joinToString("\n").toByteArray())
}
