# ThreadUtil

`ThreadUtil` provides thread-related helper methods.

---

## sleep

Sleep the current thread for the given number of milliseconds. Note: if the script runs on the Minecraft main thread, sleeping will freeze the server — use only on async threads.

`sleep(time: Long): Unit`
