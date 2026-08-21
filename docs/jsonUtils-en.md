# jsonUtils

`jsonUtils` is Sertraline's built-in `com.google.gson.Gson` instance for JSON serialization/deserialization in JS scripts. It is pre-configured for Minecraft plugin use: HTML escaping disabled, nulls serialized, lenient parsing, and custom type adapters registered so `LinkedHashMap` / `ArrayList` round-trip with structure preserved and numbers narrowed to suitable Kotlin types.

---

## jsonUtils

`jsonUtils: Gson`

A standard Gson instance — use `jsonUtils.toJson(obj)` / `jsonUtils.fromJson(json, type)` directly. JSON it produces has no HTML escaping (e.g. `<`), and null fields are kept.

## unwrapJson

Convert a Gson `JsonElement` into a script-friendly plain value.

`unwrapJson(value: Any?): Any?`

- `JsonObject` → `Map<String, Any?>`
- `JsonArray` → `List<Any?>`
- `JsonPrimitive` → `Boolean` / number / `String`
- Numbers are narrowed by magnitude to `Byte` / `Short` / `Int` / `Long` / `Float` / `Double` (integers narrowed down to the smallest fitting integer type)

## linkedHashMapStringType

`linkedHashMapStringType: Type`

A `TypeToken` that preserves `LinkedHashMap<String, Any?>` structure when used with `fromJson`; together with the custom adapters it restores JSON objects into ordered Maps.

## arrayListAnyType

`arrayListAnyType: Type`

A `TypeToken` that restores JSON arrays into `ArrayList<Any?>` when used with `fromJson`; together with the custom adapters it restores JSON arrays into Lists.
