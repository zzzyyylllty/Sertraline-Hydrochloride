# mmLegacySectionUtil

`mmLegacySectionUtil` 是 `net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer` 的实例（`LegacyComponentSerializer.legacySection()`），用 `§` 分段符号在 Adventure `Component` 与旧版格式字符串之间转换。

适合与使用 `§a` 这类旧版颜色代码的物品/消息互操作。

---

## serialize

将 Component 序列化为带 `§` 颜色代码的旧版字符串。

`serialize(component: Component): String`

## deserialize

将带 `§` 颜色代码的旧版字符串解析为 Component。

`deserialize(input: String): Component`

---

## 相关辅助函数

- `Component.toMiniMessageString(strict: Boolean = false): String` — 默认序列化为 MiniMessage 格式；如需输出到依赖 `§` 的环境，请自行用 `mmLegacySectionUtil.serialize(component)`。
