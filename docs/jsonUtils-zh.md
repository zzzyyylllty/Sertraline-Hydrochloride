# jsonUtils

`jsonUtils` 是 Sertraline 内置的 `com.google.gson.Gson` 实例，用于 JS 脚本中处理 JSON 序列化/反序列化。它已配置好适合 Minecraft 插件的选项：禁用 HTML 转义、序列化 null、宽松解析，并注册了自定义类型适配器，使 `LinkedHashMap` / `ArrayList` 往返时保持结构、数字会被收窄为合适的 Kotlin 类型。

---

## jsonUtils

`jsonUtils: Gson`

标准 Gson 实例，可直接使用 `jsonUtils.toJson(obj)` / `jsonUtils.fromJson(json, type)`。用它序列化出的 JSON 不会有 `<` 这类 HTML 转义，null 字段也会保留。

## unwrapJson

将 Gson 的 `JsonElement` 转换为脚本友好的普通值。

`unwrapJson(value: Any?): Any?`

- `JsonObject` → `Map<String, Any?>`
- `JsonArray` → `List<Any?>`
- `JsonPrimitive` → `Boolean` / 数字 / `String`
- 数字按大小收窄为 `Byte` / `Short` / `Int` / `Long` / `Float` / `Double`（整数优先收窄到更小的整型）

## linkedHashMapStringType

`linkedHashMapStringType: Type`

用于 `fromJson` 时保持 `LinkedHashMap<String, Any?>` 结构的 `TypeToken`，配合自定义适配器可把 JSON 对象还原成有序 Map。

## arrayListAnyType

`arrayListAnyType: Type`

用于 `fromJson` 时还原为 `ArrayList<Any?>` 的 `TypeToken`，配合自定义适配器可把 JSON 数组还原成 List。
