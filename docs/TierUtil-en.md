# TierUtil

`TierUtil` provides tier-related query utilities: getting tier objects and reading tier metadata (meta).

---

## getTier

Get a tier object; returns `null` if it does not exist.

`getTier(tierId: String): Tier?`

## getMeta

Get a tier's metadata value; returns `null` if the tier or key does not exist.

`getMeta(tierId: String, key: String): Any?`

## getMetaAsBoolean

Get a metadata value and leniently parse it as `Boolean` (supports `true`/`yes`/`1`, etc.); returns `false` if it does not exist.

`getMetaAsBoolean(tierId: String, key: String): Boolean`

## getMetaAsString

Get a metadata value as `String`; returns `null` if it does not exist.

`getMetaAsString(tierId: String, key: String): String?`

## getMetaAsInt

Get a metadata value as `Int`; returns `null` if it does not exist or is not a number.

`getMetaAsInt(tierId: String, key: String): Int?`

## getMetaAsDouble

Get a metadata value as `Double`; returns `null` if it does not exist or is not a number.

`getMetaAsDouble(tierId: String, key: String): Double?`

## getMetaAsList

Get a metadata value as `List<Any?>`; returns `null` if it does not exist or is not a list.

`getMetaAsList(tierId: String, key: String): List<Any?>?`
