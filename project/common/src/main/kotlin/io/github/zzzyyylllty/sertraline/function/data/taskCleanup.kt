package io.github.zzzyyylllty.sertraline.function.data

import taboolib.common.LifeCycle
import taboolib.common.function.DebounceFunction
import taboolib.common.function.ThrottleFunction
import taboolib.common.platform.Awake


@Awake(LifeCycle.DISABLE)
fun taskCleanup() {
    // Clear all throttle records
    ThrottleFunction.allThrottleFunctions.forEach { it.clearAll() }

    // Cancel all debounce tasks
    DebounceFunction.allDebounceFunctions.forEach { it.clearAll() }
}