# UUIDUtil

`UUIDUtil` 提供 UUID 的生成、解析、校验与格式转换工具。

---

## random

生成一个随机 UUID。

`random(): UUID`

## fromString

将字符串解析为 UUID。格式非法时抛出 `IllegalArgumentException`。

`fromString(str: String): UUID`

## fromStringOrNull

将字符串解析为 UUID，格式非法时返回 `null` 而不是抛出异常。

`fromStringOrNull(str: String): UUID?`

## nameUUIDFromBytes

根据字节数组生成确定性的 UUID（MD5 哈希）。

`nameUUIDFromBytes(name: ByteArray): UUID`

## nameUUIDFromString

根据字符串生成确定性的 UUID（字符串的 UTF-8 字节做 MD5 哈希）。相同输入总是得到相同 UUID。

`nameUUIDFromString(name: String): UUID`

## isValid

判断字符串是否是合法 UUID。

`isValid(str: String): Boolean`

## toDashless

将 UUID 转换为无连字符的 32 位字符串。

`toDashless(uuid: UUID): String`

## toDashless

将 UUID 字符串转换为无连字符的 32 位字符串。

`toDashless(str: String): String`

## equals

判断两个 UUID 是否相等。

`equals(a: UUID, b: UUID): Boolean`
