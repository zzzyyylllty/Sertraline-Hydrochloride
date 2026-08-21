# mmUtil

`mmUtil` is a `net.kyori.adventure.text.minimessage.MiniMessage` instance, used to parse MiniMessage-format text into Adventure `Component` and serialize them back.

Use it to convert strings with MiniMessage tags (`<red>`, `<gradient:...>`, `<click:...>`, etc.) into components you can send to a player, set as item name/lore, or pass to other Sertraline APIs.

---

## deserialize

Parse a MiniMessage string into a Component. Throws on invalid input.

`deserialize(input: String): Component`

## deserializeOrNull

Parse a MiniMessage string into a Component, returning `null` instead of throwing if the input is invalid.

`deserializeOrNull(input: String): Component?`

## serialize

Serialize a Component back into a MiniMessage string.

`serialize(component: Component): String`

## stripTags

Remove all MiniMessage tags from a string, keeping the plain text.

`stripTags(input: String, respectEscape: Boolean): String`

## escapeTags

Escape `<` and `>` so the text is displayed literally instead of being parsed as tags.

`escapeTags(input: String): String`

## parseTags

Parse only the tags in a string into a Component while keeping everything else as plain text.

`parseTags(input: String): Component`

---

## Related helper functions (same package)

These top-level functions are also available to scripts and build on MiniMessage:

- `String.toComponent(): Component` — equivalent to `FastMiniMessage.deserialize(this)` (see `FastMiniMessage`).
- `String.toComponentJson(): String` — parse then serialize the component to JSON via `mmJsonUtil`.
- `List<String>.toComponent(): List<Component>` — parse each line; if `performance.adventure.use-split-replace-list-serialize` is enabled in config, joins with `<br>` and splits back.
- `Any?.serializeComponent(): Any?` — recursively converts every String in a Map/List into a Component.
- `Component.toMiniMessageString(strict: Boolean = false): String` — serialize with the strict serializer when `strict = true`.

### FastMiniMessage

`FastMiniMessage` is the fast-minimessage facade (enabled in `experimental.yml`, off by default). When enabled it uses sparrow-minimessage and caches results; when disabled it falls back to `mmUtil.deserialize` with behavior identical to the default.

- `FastMiniMessage.deserialize(str: String): Component`
- `FastMiniMessage.applyConfig(enable: Boolean, cacheSize: Int)`
