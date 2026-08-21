# mmLegacyAmpersandUtil

`mmLegacyAmpersandUtil` is an instance of `net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer` (`LegacyComponentSerializer.legacyAmpersand()`), which converts between Adventure `Component` and legacy format strings using `&`.

Useful for parsing player input or legacy text using `&a` color codes.

---

## serialize

Serialize a Component into a legacy string with `&` color codes.

`serialize(component: Component): String`

## deserialize

Parse a legacy string with `&` color codes into a Component.

`deserialize(input: String): Component`
