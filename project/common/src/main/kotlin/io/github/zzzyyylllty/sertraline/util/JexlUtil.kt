package io.github.zzzyyylllty.sertraline.util

import taboolib.expansion.JexlCompiler


// 生产环境：安全模式 + 静默模式
val prodJexlCompiler = JexlCompiler.new()
    .safe(true)
    .silent(true)
    .strict(false)      // 设置非严格模式
    .cache(512)         // 启用缓存，大小为 512
    .cacheThreshold(256) // 设置缓存阈值
    .collectMode(0)
    .antish(false)