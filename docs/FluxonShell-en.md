# FluxonShell

`FluxonShell` is a wrapper around the Fluxon script engine providing script parsing, execution, and caching. Parse results are cached in `scriptCache` (max 500 entries, expiring after 1 hour without access).

---

## scriptCache

`scriptCache: Cache<String, ParsedScript>`

Cache of Fluxon script parse results. Normally no manual manipulation is needed.

## parse

Parse a script without executing it, returning a `ParseScript` wrapper. `env` configures the script environment before parsing (e.g. injecting variables).

`parse(script: String, env: Environment.() -> Unit = {}): ParseScript`

## invoke

Execute a script text and return its result. `useCache = true` (default) uses the parse cache; pass `false` when the script content changes frequently. `env` configures the script environment.

`invoke(script: String, useCache: Boolean = true, env: Environment.() -> Unit = {}): Any?`

## invoke

Execute an already-parsed script (`ParsedScript`) and return its result. A `return` value inside the script is returned as the result.

`invoke(parsed: ParsedScript, environment: Environment): Any?`

## parse

Parse a script text into a `ParsedScript`; returns `null` on parse failure.

`parse(script: String, environment: Environment): ParsedScript?`
