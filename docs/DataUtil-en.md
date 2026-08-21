# DataUtil

`DataUtil` provides database-backed player key-value data read/write and cooldown management. Player data and cooldowns are stored separately per player UUID.

---

## Player Data

### savePlayerData

Save the player's data to the database.

`savePlayerData(player: Player): Unit`

### resetAllData

Delete all of the player's key-value data.

`resetAllData(player: Player): Unit`

### getDataRaw

Read the raw string value; returns `null` if the data does not exist.

`getDataRaw(player: Player, dataID: String): String?`

### getDataSmart

Read and smart-convert the type (infers number / boolean / string from the content); returns `null` if the data does not exist.

`getDataSmart(player: Player, dataID: String): Any?`

### getDataAsInt

Read as `Int`; returns `null` if the data does not exist or cannot be parsed.

`getDataAsInt(player: Player, dataID: String): Int?`

### getDataAsBoolean

Read as `Boolean` (lenient parsing of common boolean spellings); returns `null` if the data does not exist or cannot be parsed.

`getDataAsBoolean(player: Player, dataID: String): Boolean?`

### getDataAsDouble

Read as `Double`; returns `null` if the data does not exist or cannot be parsed.

`getDataAsDouble(player: Player, dataID: String): Double?`

### getDataAsLong

Read as `Long`; returns `null` if the data does not exist or cannot be parsed.

`getDataAsLong(player: Player, dataID: String): Long?`

### setData

Write (or overwrite) a key-value entry. Values are stored as strings.

`setData(player: Player, dataID: String, dataValue: Any): Unit`

### setDataIfNotExist

Write only if the key does not already exist.

`setDataIfNotExist(player: Player, dataID: String, dataValue: Any): Unit`

### removeData

Delete a key-value entry.

`removeData(player: Player, dataID: String): Unit`

### getAllDataRaw

Read all of the player's key-value data as a `Map<String, String>`.

`getAllDataRaw(player: Player): Map<String, String>`

---

## Cooldown Management

Cooldown expiry times are stored as epoch milliseconds.

### resetAllCooldown

Clear all of the player's cooldowns.

`resetAllCooldown(player: Player): Unit`

### getAllCooldownRaw

Read all cooldowns as `Map<cooldown ID, expiry timestamp string>`.

`getAllCooldownRaw(player: Player): Map<String, String>`

### getAllCooldownLong

Read all cooldowns as `Map<cooldown ID, expiry timestamp Long>`.

`getAllCooldownLong(player: Player): Map<String, Long>`

### getAllCooldownDate

Read all cooldowns as `Map<cooldown ID, Date>`.

`getAllCooldownDate(player: Player): Map<String, Date>`

### getCooldownRaw

Read a single cooldown's expiry timestamp string; returns `null` if none exists.

`getCooldownRaw(player: Player, cooldownID: String): String?`

### getCooldownLong

Read a single cooldown's expiry timestamp `Long`; returns `null` if none exists.

`getCooldownLong(player: Player, cooldownID: String): Long?`

### getCooldownDate

Read a single cooldown's expiry time as `Date`; returns `null` if none exists.

`getCooldownDate(player: Player, cooldownID: String): Date?`

### getCooldownLeftLong

Read the remaining cooldown in milliseconds; returns `null` if none exists.

`getCooldownLeftLong(player: Player, cooldownID: String): Long?`

### getCooldownLeftDate

Read the remaining cooldown as a `Date` duration (measured from now); returns `null` if none exists.

`getCooldownLeftDate(player: Player, cooldownID: String): Date?`

### setCooldown

Set a cooldown; `second` is the duration in seconds.

`setCooldown(player: Player, cooldownID: String, second: Double): Unit`

### setCooldownMill

Set a cooldown; `tick` is the duration in milliseconds.

`setCooldownMill(player: Player, cooldownID: String, tick: Int): Unit`

### extendCooldown

Extend a cooldown; `second` is the additional seconds (starts from now if none exists).

`extendCooldown(player: Player, cooldownID: String, second: Double): Unit`

### extendCooldownMill

Extend a cooldown; `tick` is the additional milliseconds (starts from now if none exists).

`extendCooldownMill(player: Player, cooldownID: String, tick: Int): Unit`

### reduceCooldown

Shorten a cooldown; `second` is the seconds to subtract (computed backwards from now if none exists).

`reduceCooldown(player: Player, cooldownID: String, second: Double): Unit`

### reduceCooldownTick

Shorten a cooldown; `tick` is the milliseconds to subtract (computed backwards from now if none exists).

`reduceCooldownTick(player: Player, cooldownID: String, tick: Int): Unit`

### resetCooldown

Clear a single cooldown.

`resetCooldown(player: Player, cooldownID: String): Unit`

### isInCooldown

Check whether the cooldown is still active (returns `true` when the expiry time is >= now).

`isInCooldown(player: Player, cooldownID: String): Boolean`

### cleanupCooldown

Delete all of the player's expired cooldown records.

`cleanupCooldown(player: Player): Unit`
