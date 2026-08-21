# mmUtil

`mmUtil` 是 `net.kyori.adventure.text.minimessage.MiniMessage` 的实例，用于将 MiniMessage 格式文本解析为 Adventure `Component`，或将 Component 序列化回字符串。

用它把带 MiniMessage 标签（`<red>`、`<gradient:...>`、`<click:...>` 等）的字符串转换成 Component，再发送给玩家、设置为物品名称/lore，或传给其他 Sertraline API。

---

## deserialize

将 MiniMessage 字符串解析为 Component。输入非法时抛出异常。

`deserialize(input: String): Component`

## deserializeOrNull

将 MiniMessage 字符串解析为 Component，输入非法时返回 `null` 而不是抛出异常。

`deserializeOrNull(input: String): Component?`

## serialize

将 Component 序列化为 MiniMessage 字符串。

`serialize(component: Component): String`

## stripTags

移除字符串中的所有 MiniMessage 标签，保留纯文本。

`stripTags(input: String, respectEscape: Boolean): String`

## escapeTags

转义 `<` 和 `>`，让文本原样显示而不是被当作标签解析。

`escapeTags(input: String): String`

## parseTags

只解析字符串中的标签为 Component，其余内容保持纯文本。

`parseTags(input: String): Component`

---

## 同包相关辅助函数

以下顶层函数同样可用于脚本，它们基于 MiniMessage 构建：

- `String.toComponent(): Component` — 等价于 `FastMiniMessage.deserialize(this)`（见 `FastMiniMessage`）。
- `String.toComponentJson(): String` — 解析后通过 `mmJsonUtil` 序列化为 JSON。
- `List<String>.toComponent(): List<Component>` — 逐行解析；若配置开启 `performance.adventure.use-split-replace-list-serialize`，则用 `<br>` 连接后解析再拆分。
- `Any?.serializeComponent(): Any?` — 递归将 Map/List 中的每个 String 转换为 Component。
- `Component.toMiniMessageString(strict: Boolean = false): String` — `strict = true` 时用严格解析器序列化。

### FastMiniMessage

`FastMiniMessage` 是 fast-minimessage 门面（`experimental.yml` 开启，默认关闭）。开启时使用 sparrow-minimessage 并缓存结果；关闭时回退到 `mmUtil.deserialize`，行为与原版完全一致。

- `FastMiniMessage.deserialize(str: String): Component`
- `FastMiniMessage.applyConfig(enable: Boolean, cacheSize: Int)`
