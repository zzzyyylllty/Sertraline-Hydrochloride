# UUIDUtil

`UUIDUtil` provides UUID generation, parsing, validation, and format conversion.

---

## random

Generate a random UUID.

`random(): UUID`

## fromString

Parse a string into a UUID. Throws `IllegalArgumentException` on invalid format.

`fromString(str: String): UUID`

## fromStringOrNull

Parse a string into a UUID, returning `null` instead of throwing on invalid format.

`fromStringOrNull(str: String): UUID?`

## nameUUIDFromBytes

Generate a deterministic UUID from a byte array (MD5 hash).

`nameUUIDFromBytes(name: ByteArray): UUID`

## nameUUIDFromString

Generate a deterministic UUID from a string (MD5 hash of the string's UTF-8 bytes). The same input always yields the same UUID.

`nameUUIDFromString(name: String): UUID`

## isValid

Check whether a string is a valid UUID.

`isValid(str: String): Boolean`

## toDashless

Convert a UUID into a 32-character dashless string.

`toDashless(uuid: UUID): String`

## toDashless

Convert a UUID string into a 32-character dashless string.

`toDashless(str: String): String`

## equals

Check whether two UUIDs are equal.

`equals(a: UUID, b: UUID): Boolean`
