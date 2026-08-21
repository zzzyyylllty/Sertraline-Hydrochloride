# EventUtil

`EventUtil` provides event helper methods: canceling events and calling events. Internally goes through `PlatformCompat` for consistent Paper/Spigot behavior; errors are logged rather than thrown to the script.

---

## cancel

Set the event as canceled (or restore it). Pass `cancel = true` (the default) to cancel the event.

`cancel(event: Cancellable, cancel: Boolean = true): Unit`

Example: after getting a `Cancellable` event in a script, use `EventUtil.cancel(event)` to prevent its default behavior.

**Note:** an event can only be canceled when the script runs on the main thread. Make sure the script runs on the main thread (`async: true`); canceling from an async thread will have no effect.

## call

Call a constructed Bukkit event so other listeners can receive and handle it.

`call(event: Event): Unit`
