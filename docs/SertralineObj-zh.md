# SertralineObj

脚本中的 `SertralineObj` 即插件主类 `Sertraline` 对象，是访问插件全局状态（公共物品、品质、类型、配置）与 `api()` 入口的静态门面。

---

## api

获取 `SertralineAPI` 接口实例（等同于脚本中的 `Sertraline.api()`），用于构建/查询/扣除物品等全部 API 功能（见 `SertralineAPI` 文档）。

`api(): SertralineAPI`

## reloadCustomConfig

重载插件全部配置（物品、映射、品质、类型、等级、lore 格式、合成台、模板、脚本等）。`async` 控制是否异步重载，`sender` 用于把重载统计摘要发送给指令执行者。

`reloadCustomConfig(async: Boolean = true, sender: CommandSender? = null): Unit`

## itemMap

所有已注册的公共物品，`Map<物品ID, ModernSItem>`。

`itemMap: LinkedHashMap<String, ModernSItem>`

## mappings

已加载的映射数据，`Map<映射名, List<String>?>`。

`mappings: LinkedHashMap<String, List<String>?>`

## loreFormats

已加载的 Lore 格式，`Map<格式名, LoreFormat>`。

`loreFormats: LinkedHashMap<String, LoreFormat>`

## craftingStations

已加载的合成台配置，`Map<合成台名, CraftingStation>`。

`craftingStations: LinkedHashMap<String, CraftingStation>`

## tiers

已加载的品质，`Map<品质ID, Tier>`。

`tiers: LinkedHashMap<String, Tier>`

## types

已加载的类型，`Map<类型ID, Type>`。

`types: LinkedHashMap<String, Type>`

## levels

已加载的等级，`Map<等级ID, Level>`。

`levels: LinkedHashMap<String, Level>`

## manager

物品管理器（`ItemManager`），负责公共/私有、临时/持久物品的创建、查询与删除。

`manager: ItemManager`

## config

主配置文件 `config.yml` 的 `Configuration` 对象。

`config: Configuration`

## experimentalConfig

实验性配置 `experimental.yml` 的 `Configuration` 对象。

`experimentalConfig: Configuration`

## devMode

是否开启调试模式（对应 `config.yml` 的 `debug`）。

`devMode: Boolean`

## allowAsyncLog

是否允许异步日志输出（对应 `config.yml` 的 `async-logging`）。

`allowAsyncLog: Boolean`

## isEnabled

插件是否已启用。

`isEnabled: Boolean`

## dataFolder

插件数据文件夹（`File`）。

`dataFolder: File`

## consoleSender

控制台 `CommandSender`，可向控制台发送消息。

`consoleSender: CommandSender`

## dateTimeFormatter

日期时间格式化器（`yyyy-MM-dd HH:mm:ss`），可用 `dateTimeFormatter.format(Date)` 格式化时间。

`dateTimeFormatter: DateTimeFormatter`
