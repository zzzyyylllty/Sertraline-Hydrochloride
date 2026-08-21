# TypeUtil

`TypeUtil` provides type-related query utilities: type inheritance checks, metadata reading, and extracting a type from an item / item data. Type IDs match case-insensitively.

---

## getType

Get a type object (case-insensitive); returns `null` if it does not exist.

`getType(typeId: String): Type?`

## getMeta

Get a type's metadata value; returns `null` if the type or key does not exist.

`getMeta(typeId: String, key: String): Any?`

## getMetaAsBoolean

Get a metadata value and leniently parse it as `Boolean` (supports `true`/`yes`/`1`, etc.); returns `false` if it does not exist.

`getMetaAsBoolean(typeId: String, key: String): Boolean`

## getMetaAsString

Get a metadata value as `String`; returns `null` if it does not exist.

`getMetaAsString(typeId: String, key: String): String?`

## getMetaAsInt

Get a metadata value as `Int`; returns `null` if it does not exist or is not a number.

`getMetaAsInt(typeId: String, key: String): Int?`

## getMetaAsDouble

Get a metadata value as `Double`; returns `null` if it does not exist or is not a number.

`getMetaAsDouble(typeId: String, key: String): Double?`

## getMetaAsList

Get a metadata value as `List<Any?>`; returns `null` if it does not exist or is not a list.

`getMetaAsList(typeId: String, key: String): List<Any?>?`

## getAncestors

Get all ancestors of a type (including itself), in order from self to root (the first element is itself).

`getAncestors(typeId: String): List<String>`

## isAssignableFrom

Check whether `typeId` inherits from (or equals) `potentialAncestorId`.

`isAssignableFrom(typeId: String, potentialAncestorId: String): Boolean`

## getDescendants

Get all descendants of a type (including itself). Note: this iterates all types and is slow — use only when the type count is small.

`getDescendants(typeId: String): List<String>`

## getTypeIdFromItemData

Extract the type ID from an item data Map (the `sertraline:type` key); returns `null` if it does not exist.

`getTypeIdFromItemData(itemData: Map<String, Any?>): String?`

## getTypeFromItemData

Extract the type object from an item data Map; returns `null` if it does not exist.

`getTypeFromItemData(itemData: Map<String, Any?>): Type?`

## getItemTypeId

Get the type ID from an item (`ItemStack` / `ModernSItem` / item data Map). For `ItemStack`, reads the NBT `sertraline_type` first, otherwise falls back to its template's type; returns `null` if unrecognized.

`getItemTypeId(item: Any): String?`

## isItemIsType

Check whether the item's type **exactly equals** the given type (no inheritance, case-insensitive).

`isItemIsType(item: Any, typeId: String): Boolean`

## isItemIsExtendType

Check whether the item's type inherits from the given type (includes inheritance, e.g. `sword` inheriting `weapon` returns `true` for `weapon`, case-insensitive).

`isItemIsExtendType(item: Any, typeId: String): Boolean`
