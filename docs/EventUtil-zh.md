# EventUtil

`EventUtil` 提供事件相关的辅助方法：取消事件与触发事件。内部经 `PlatformCompat`，保证 Paper / Spigot 双平台行为一致，出错时记录日志而非抛出到脚本。

---

## cancel

将事件设置为取消（或恢复）。默认传入 `cancel = true` 取消事件。

`cancel(event: Cancellable, cancel: Boolean = true): Unit`

示例：在脚本中拿到一个 `Cancellable` 事件后，用 `EventUtil.cancel(event)` 阻止其默认行为。

## call

触发一个已构造好的 Bukkit 事件，让其他监听器可以收到并处理它。

`call(event: Event): Unit`
