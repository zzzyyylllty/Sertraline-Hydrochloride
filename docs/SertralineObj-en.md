# SertralineObj

`SertralineObj` in scripts is the plugin's main class `Sertraline` object — the static facade for accessing plugin global state (public items, tiers, types, config) and the `api()` entry point.

---

## api

Get the `SertralineAPI` interface instance (equivalent to `Sertraline.api()`), used for all API features such as building/querying/taking items (see the `SertralineAPI` doc).

`api(): SertralineAPI`

## reloadCustomConfig

Reload all plugin configurations (items, mappings, tiers, types, levels, lore formats, crafting stations, templates, scripts, etc.). `async` controls whether the reload runs asynchronously; `sender` is used to deliver the reload summary to the command executor.

`reloadCustomConfig(async: Boolean = true, sender: CommandSender? = null): Unit`

## itemMap

All registered public items, `Map<item ID, ModernSItem>`.

`itemMap: LinkedHashMap<String, ModernSItem>`

## mappings

Loaded mapping data, `Map<mapping name, List<String>?>`.

`mappings: LinkedHashMap<String, List<String>?>`

## loreFormats

Loaded lore formats, `Map<format name, LoreFormat>`.

`loreFormats: LinkedHashMap<String, LoreFormat>`

## craftingStations

Loaded crafting station configs, `Map<station name, CraftingStation>`.

`craftingStations: LinkedHashMap<String, CraftingStation>`

## tiers

Loaded tiers, `Map<tier ID, Tier>`.

`tiers: LinkedHashMap<String, Tier>`

## types

Loaded types, `Map<type ID, Type>`.

`types: LinkedHashMap<String, Type>`

## levels

Loaded levels, `Map<level ID, Level>`.

`levels: LinkedHashMap<String, Level>`

## manager

The item manager (`ItemManager`), responsible for creating, querying, and deleting public/private and temporary/persistent items.

`manager: ItemManager`

## config

The `Configuration` object for the main config file `config.yml`.

`config: Configuration`

## experimentalConfig

The `Configuration` object for the experimental config `experimental.yml`.

`experimentalConfig: Configuration`

## devMode

Whether debug mode is enabled (corresponds to `debug` in `config.yml`).

`devMode: Boolean`

## allowAsyncLog

Whether async logging is allowed (corresponds to `async-logging` in `config.yml`).

`allowAsyncLog: Boolean`

## isEnabled

Whether the plugin is enabled.

`isEnabled: Boolean`

## dataFolder

The plugin data folder (`File`).

`dataFolder: File`

## consoleSender

The console `CommandSender`, used to send messages to the console.

`consoleSender: CommandSender`

## dateTimeFormatter

The date-time formatter (`yyyy-MM-dd HH:mm:ss`). Format a time with `dateTimeFormatter.format(Date)`.

`dateTimeFormatter: DateTimeFormatter`
