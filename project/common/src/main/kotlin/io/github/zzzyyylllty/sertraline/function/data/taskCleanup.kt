package io.github.zzzyyylllty.sertraline.function.data

import taboolib.common.LifeCycle
import taboolib.common.function.DebounceFunction
import taboolib.common.function.ThrottleFunction
import taboolib.common.platform.Awake


@Awake(LifeCycle.DISABLE)
fun taskCleanup() {
    // 清除所有节流记录
    ThrottleFunction.allThrottleFunctions.forEach { it.clearAll() }

    // 取消所有防抖任务
    DebounceFunction.allDebounceFunctions.forEach { it.clearAll() }
}