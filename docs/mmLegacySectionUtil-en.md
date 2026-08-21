# mmLegacySectionUtil

`mmLegacySectionUtil` is an instance of `net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer` (`LegacyComponentSerializer.legacySection()`), which converts between Adventure `Component` and legacy format strings using the `§` section symbol.

Useful for interop with items/messages that use old-style color codes like `§a`.

---

## serialize

Serialize a Component into a legacy string with `§` color codes.

`serialize(component: Component): String`

## deserialize

Parse a legacy string with `§` color codes into a Component.

`deserialize(input: String): Component`

---

## Related helper functions

- `Component.toMiniMessageString(strict: Boolean = false): String` — serializes to MiniMessage format by default; to emit `§`-style output, use `mmLegacySectionUtil.serialize(component)` directly.
