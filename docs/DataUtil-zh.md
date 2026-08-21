# DataUtil

`DataUtil` 提供基于数据库的玩家键值数据读写与冷却管理工具。玩家数据与冷却均按玩家 UUID 隔离存储。

---

## 玩家数据

### savePlayerData

保存玩家数据到数据库。

`savePlayerData(player: Player): Unit`

### resetAllData

删除该玩家的全部键值数据。

`resetAllData(player: Player): Unit`

### getDataRaw

读取原始字符串值，数据不存在返回 `null`。

`getDataRaw(player: Player, dataID: String): String?`

### getDataSmart

读取并智能转换类型（按内容推测为数字 / 布尔 / 字符串等），数据不存在返回 `null`。

`getDataSmart(player: Player, dataID: String): Any?`

### getDataAsInt

读取为 `Int`，数据不存在或无法解析返回 `null`。

`getDataAsInt(player: Player, dataID: String): Int?`

### getDataAsBoolean

读取为 `Boolean`（宽容解析常见布尔写法），数据不存在或无法解析返回 `null`。

`getDataAsBoolean(player: Player, dataID: String): Boolean?`

### getDataAsDouble

读取为 `Double`，数据不存在或无法解析返回 `null`。

`getDataAsDouble(player: Player, dataID: String): Double?`

### getDataAsLong

读取为 `Long`，数据不存在或无法解析返回 `null`。

`getDataAsLong(player: Player, dataID: String): Long?`

### setData

写入（或覆盖）一条键值数据。值以字符串形式存储。

`setData(player: Player, dataID: String, dataValue: Any): Unit`

### setDataIfNotExist

仅在该键不存在时写入。

`setDataIfNotExist(player: Player, dataID: String, dataValue: Any): Unit`

### removeData

删除一条键值数据。

`removeData(player: Player, dataID: String): Unit`

### getAllDataRaw

读取该玩家的全部键值数据，返回 `Map<String, String>`。

`getAllDataRaw(player: Player): Map<String, String>`

---

## 冷却管理

冷却到期时间以「时间戳毫秒」存储。

### resetAllCooldown

清除该玩家的全部冷却。

`resetAllCooldown(player: Player): Unit`

### getAllCooldownRaw

读取全部冷却，返回 `Map<冷却ID, 到期时间戳字符串>`。

`getAllCooldownRaw(player: Player): Map<String, String>`

### getAllCooldownLong

读取全部冷却，返回 `Map<冷却ID, 到期时间戳 Long>`。

`getAllCooldownLong(player: Player): Map<String, Long>`

### getAllCooldownDate

读取全部冷却，返回 `Map<冷却ID, Date>`。

`getAllCooldownDate(player: Player): Map<String, Date>`

### getCooldownRaw

读取单个冷却的到期时间戳字符串，无冷却返回 `null`。

`getCooldownRaw(player: Player, cooldownID: String): String?`

### getCooldownLong

读取单个冷却的到期时间戳 `Long`，无冷却返回 `null`。

`getCooldownLong(player: Player, cooldownID: String): Long?`

### getCooldownDate

读取单个冷却的到期时间 `Date`，无冷却返回 `null`。

`getCooldownDate(player: Player, cooldownID: String): Date?`

### getCooldownLeftLong

读取冷却剩余毫秒数，无冷却返回 `null`。

`getCooldownLeftLong(player: Player, cooldownID: String): Long?`

### getCooldownLeftDate

读取冷却剩余时长 `Date`（从当前时间起算），无冷却返回 `null`。

`getCooldownLeftDate(player: Player, cooldownID: String): Date?`

### setCooldown

设置冷却，`second` 为持续秒数。

`setCooldown(player: Player, cooldownID: String, second: Double): Unit`

### setCooldownMill

设置冷却，`tick` 为持续毫秒数。

`setCooldownMill(player: Player, cooldownID: String, tick: Int): Unit`

### extendCooldown

延长冷却，`second` 为额外秒数（无冷却则从当前时间起算）。

`extendCooldown(player: Player, cooldownID: String, second: Double): Unit`

### extendCooldownMill

延长冷却，`tick` 为额外毫秒数（无冷却则从当前时间起算）。

`extendCooldownMill(player: Player, cooldownID: String, tick: Int): Unit`

### reduceCooldown

缩短冷却，`second` 为减少的秒数（无冷却则从当前时间反向计算）。

`reduceCooldown(player: Player, cooldownID: String, second: Double): Unit`

### reduceCooldownTick

缩短冷却，`tick` 为减少的毫秒数（无冷却则从当前时间反向计算）。

`reduceCooldownTick(player: Player, cooldownID: String, tick: Int): Unit`

### resetCooldown

清除单个冷却。

`resetCooldown(player: Player, cooldownID: String): Unit`

### isInCooldown

判断冷却是否仍在生效（到期时间大于等于当前时间返回 `true`）。

`isInCooldown(player: Player, cooldownID: String): Boolean`

### cleanupCooldown

清除该玩家所有已过期的冷却记录。

`cleanupCooldown(player: Player): Unit`
