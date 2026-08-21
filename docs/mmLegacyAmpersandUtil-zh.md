# mmLegacyAmpersandUtil

`mmLegacyAmpersandUtil` 是 `net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer` 的实例（`LegacyComponentSerializer.legacyAmpersand()`），用 `&` 在 Adventure `Component` 与旧版格式字符串之间转换。

适合解析玩家输入或配置里使用 `&a` 颜色代码的旧版文本。

---

## serialize

将 Component 序列化为带 `&` 颜色代码的旧版字符串。

`serialize(component: Component): String`

## deserialize

将带 `&` 颜色代码的旧版字符串解析为 Component。

`deserialize(input: String): Component`
