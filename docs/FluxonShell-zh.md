# FluxonShell

`FluxonShell` 是 Fluxon 脚本引擎的封装，提供脚本解析、执行与缓存。解析结果会缓存在 `scriptCache`（上限 500 条、1 小时无访问过期）。

---

## scriptCache

`scriptCache: Cache<String, ParsedScript>`

Fluxon 脚本解析结果的缓存。一般无需手动操作。

## parse

解释脚本但不执行，返回 `ParseScript` 包装对象。`env` 用于在解析前配置脚本环境（如注入变量）。

`parse(script: String, env: Environment.() -> Unit = {}): ParseScript`

## invoke

执行脚本文本，返回执行结果。`useCache = true`（默认）时使用解析缓存；若脚本内容频繁变化，建议传 `false` 关闭缓存。`env` 用于配置脚本环境。

`invoke(script: String, useCache: Boolean = true, env: Environment.() -> Unit = {}): Any?`

## invoke

执行已解析的脚本（`ParsedScript`），返回执行结果。脚本内 `return` 的值会作为返回值。

`invoke(parsed: ParsedScript, environment: Environment): Any?`

## parse

将脚本文本解析为 `ParsedScript`，解析失败返回 `null`。

`parse(script: String, environment: Environment): ParsedScript?`
