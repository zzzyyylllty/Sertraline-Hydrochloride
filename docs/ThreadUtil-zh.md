# ThreadUtil

`ThreadUtil` 提供线程相关的辅助方法。

---

## sleep

让当前线程休眠指定毫秒数。注意：若脚本运行在 Minecraft 主线程，休眠会卡住服务器，请仅在异步线程中使用。

`sleep(time: Long): Unit`
