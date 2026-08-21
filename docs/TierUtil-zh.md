# TierUtil

`TierUtil` 提供品质（Tier）相关的查询工具：获取品质对象与读取品质元数据（meta）。

---

## getTier

获取品质对象，不存在返回 `null`。

`getTier(tierId: String): Tier?`

## getMeta

获取品质元数据，品质或键不存在返回 `null`。

`getMeta(tierId: String, key: String): Any?`

## getMetaAsBoolean

获取元数据并宽容解析为 `Boolean`（支持 `true`/`yes`/`1` 等写法），不存在时返回 `false`。

`getMetaAsBoolean(tierId: String, key: String): Boolean`

## getMetaAsString

获取元数据并转为 `String`，不存在时返回 `null`。

`getMetaAsString(tierId: String, key: String): String?`

## getMetaAsInt

获取元数据并转为 `Int`，不存在或非数字时返回 `null`。

`getMetaAsInt(tierId: String, key: String): Int?`

## getMetaAsDouble

获取元数据并转为 `Double`，不存在或非数字时返回 `null`。

`getMetaAsDouble(tierId: String, key: String): Double?`

## getMetaAsList

获取元数据并转为 `List<Any?>`，不存在或非列表时返回 `null`。

`getMetaAsList(tierId: String, key: String): List<Any?>?`
