# PlayerUtil

`PlayerUtil` 提供玩家相关的常用工具：药水效果、占位符/Kether 解析、消息发送等。药水效果与消息发送经 `PlatformCompat` 提交到主线程，保证 Paper / Spigot 双平台一致。

---

## addPotionEffect

给玩家添加药水效果（完整参数版）。经 `PlatformCompat` 提交到主线程执行，适配旧版本地端缺少的构造器。

`addPotionEffect(player: Player, type: String, duration: Int = 30, amplifier: Int = 0, ambient: Boolean = true, particles: Boolean = true, icon: Boolean = true): Unit`

## addPotionEffect

给玩家添加药水效果（简化版）。经 `submit` 提交到主线程执行。

`addPotionEffect(player: Player, type: String, duration: Int = 30, amplifier: Int = 0): Unit`

## removePotionEffect

移除玩家身上的指定药水效果。经 `submit` 提交到主线程执行。

`removePotionEffect(player: Player, type: String): Unit`

## parsePlaceholders

用 PlaceholderAPI 解析字符串中的占位符（如 `%player_name%`）。需要服务器安装 PlaceholderAPI。

`parsePlaceholders(player: Player, string: String): String`

## parseKether

用 Kether 脚本引擎解析字符串，返回执行结果。`vars` 为脚本环境变量。

`parseKether(player: Player, string: String, vars: Map<String, Any?>): Any?`

## parseKetherList

逐行用 Kether 引擎解析字符串列表，返回执行结果。

`parseKetherList(player: Player, string: List<String>, vars: Map<String, Any?>): Any?`

## sendMessage

向玩家发送一条 Adventure `Component` 消息。统一经 `PlatformCompat`，双平台行为一致（直接调用 `player.sendMessage` 在 Spigot 上可能因重载缺失而报错）。

`sendMessage(player: Player, component: Component): Unit`

## sendActionBar

向玩家发送一条 ActionBar 消息（游戏内快捷提示栏）。

`sendActionBar(player: Player, component: Component): Unit`

## showTitle

向玩家发送标题（Title）与副标题。时长单位为 tick。

`showTitle(player: Player, title: Component, subTitle: Component, durationIn: Int = 30, duration: Int = 30, durationOut: Int = 30): Unit`
