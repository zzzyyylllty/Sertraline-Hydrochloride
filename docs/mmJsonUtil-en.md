# mmJsonUtil

`mmJsonUtil` is an instance of `net.kyori.adventure.text.serializer.gson.GsonComponentSerializer` (`GsonComponentSerializer.gson()`), used to convert between Adventure `Component` and JSON strings.

Useful for storing components in a database or config file, or for restoring components from JSON data.

---

## serialize

Serialize a Component into a JSON string.

`serialize(component: Component): String`

## deserialize

Parse a JSON string into a Component. Throws on invalid input.

`deserialize(input: String): Component`

## serializeTree

Serialize a Component into a `JsonElement` (Gson tree node).

`serializeTree(component: Component): JsonElement`

## consumeTree

Parse a `JsonElement` (Gson tree node) into a Component.

`consumeTree(json: JsonElement): Component`

---

## Related helper functions

- `String.toComponentJson(): String` — parse the string with MiniMessage, then serialize the component to JSON via `mmJsonUtil` (see the `mmUtil` doc).
- `Component.cleanRemovedDataComponents(): Component` — recursively strips `Removed` data-component values from a ShowItem hover that cannot be serialized. The ShowItem hover attached by Paper's `getDisplayName()` may contain removal patches produced by `minecraft:xxx: null` in YAML; serializing directly would throw. Call it before serializing to be safe.
