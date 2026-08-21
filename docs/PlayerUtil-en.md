# PlayerUtil

`PlayerUtil` provides common player utilities: potion effects, placeholder/Kether parsing, and message sending. Potion effects and messages go through `PlatformCompat` on the main thread for consistent Paper/Spigot behavior.

---

## addPotionEffect

Add a potion effect to a player (full-parameter version). Dispatched to the main thread via `PlatformCompat`, adapting for legacy servers missing constructors.

`addPotionEffect(player: Player, type: String, duration: Int = 30, amplifier: Int = 0, ambient: Boolean = true, particles: Boolean = true, icon: Boolean = true): Unit`

## addPotionEffect

Add a potion effect to a player (simplified version). Dispatched to the main thread via `submit`.

`addPotionEffect(player: Player, type: String, duration: Int = 30, amplifier: Int = 0): Unit`

## removePotionEffect

Remove a potion effect from a player. Dispatched to the main thread via `submit`.

`removePotionEffect(player: Player, type: String): Unit`

## parsePlaceholders

Parse placeholders in a string (e.g. `%player_name%`) using PlaceholderAPI. Requires PlaceholderAPI installed on the server.

`parsePlaceholders(player: Player, string: String): String`

## parseKether

Parse a string with the Kether script engine and return the execution result. `vars` are script environment variables.

`parseKether(player: Player, string: String, vars: Map<String, Any?>): Any?`

## parseKetherList

Parse a list of strings line by line with the Kether engine and return the execution result.

`parseKetherList(player: Player, string: List<String>, vars: Map<String, Any?>): Any?`

## sendMessage

Send an Adventure `Component` message to a player. Unified through `PlatformCompat` for consistent behavior on both platforms (calling `player.sendMessage` directly can fail on Spigot due to a missing overload).

`sendMessage(player: Player, component: Component): Unit`

## sendActionBar

Send an ActionBar message to a player (the in-game quick hint bar).

`sendActionBar(player: Player, component: Component): Unit`

## showTitle

Send a title and subtitle to a player. Durations are in ticks.

`showTitle(player: Player, title: Component, subTitle: Component, durationIn: Int = 30, duration: Int = 30, durationOut: Int = 30): Unit`
