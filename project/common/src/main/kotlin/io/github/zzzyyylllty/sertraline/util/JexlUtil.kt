package io.github.zzzyyylllty.sertraline.util

import com.google.gson.Gson
import org.bukkit.Bukkit
import taboolib.common.util.unsafeLazy
import taboolib.expansion.JexlCompiledScript
import taboolib.expansion.JexlCompiler
import kotlin.getValue


object JexlUtil {
    // Production: safe mode + silent mode
    val prodJexlCompiler = JexlCompiler.new()
        .safe(true)
        .silent(true)
        .strict(false)      // set non-strict mode
        .cache(512)         // enable caching, size 512
        .cacheThreshold(256) // set cache threshold
        .collectMode(0)
        .antish(false)
//    .namespace(mapOf(
//        "Math" to java.lang.Math::class.java,
//        "System" to java.lang.System::class.java,
//        "Bukkit" to Bukkit::class.java,
//        "Gson" to Gson::class.java
//    ))

}