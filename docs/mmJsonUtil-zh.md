# mmJsonUtil

`mmJsonUtil` 是 `net.kyori.adventure.text.serializer.gson.GsonComponentSerializer` 的实例（`GsonComponentSerializer.gson()`），用于在 Adventure `Component` 与 JSON 字符串之间转换。

适合把 Component 存进数据库/配置文件，或从 JSON 数据恢复 Component。

---

## serialize

将 Component 序列化为 JSON 字符串。

`serialize(component: Component): String`

## deserialize

将 JSON 字符串解析为 Component。输入非法时抛出异常。

`deserialize(input: String): Component`

## serializeTree

将 Component 序列化为 `JsonElement`（Gson 树节点）。

`serializeTree(component: Component): JsonElement`

## consumeTree

将 `JsonElement`（Gson 树节点）解析为 Component。

`consumeTree(json: JsonElement): Component`

---

## 相关辅助函数

- `String.toComponentJson(): String` — 用 MiniMessage 解析字符串后，再通过 `mmJsonUtil` 序列化为 JSON（见 `mmUtil` 文档）。
- `Component.cleanRemovedDataComponents(): Component` — 递归剔除 ShowItem hover 中无法序列化的 Removed 数据组件值。Paper 的 `getDisplayName()` 附加的 ShowItem hover 可能含 YAML 中 `minecraft:xxx: null` 产生的移除补丁，直接 serialize 会抛异常；序列化前先调用它可安全兜底。
