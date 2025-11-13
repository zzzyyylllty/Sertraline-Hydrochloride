taboolib {
    description {
        name("Sertraline")
        desc("An advanced item plugin. ChoTen item management plugin.")
        contributors {
            // 作者名称
            name("AkaCandyKAngel")
            name("jhqwqmc")
        }
        dependencies {
            // 依赖插件名称（不要误会成写自己，会触发 self-loop 错误）
            name("DylsemHokma").optional(true)
            name("MythicLib").optional(true)
            name("TrMenu").optional(true)
            name("Zaphkiel").optional(true)
            name("MMOItems").optional(true)
            name("SX-Item").optional(true)
            name("MythicMobs").optional(true)
            name("NeigeItems").optional(true)
            name("Chemdah").optional(true)
            name("CraftEngine").optional(true)
            name("ItemsAdder").optional(true)
            name("Oxaren").optional(true)
            name("MagicCosmetics").optional(true)
            name("packetevents").optional(true)
            // name("ProtocolLib").optional(true)
            // 可选依赖.
            // name("XXX").optional(true)
        }
    }
    relocate("top.maplex.arim","xxx.xxx.arim")
    relocate("ink.ptms.um","xx.um")
    // relocate("com.google", "io.github.zzzyyylllty.sertraline.library.com.google")
    relocate("com.alibaba", "io.github.zzzyyylllty.sertraline.library.com.alibaba")
    relocate("kotlinx.serialization", "kotlinx.serialization170")
    // relocate("de.tr7zw.changeme.nbtapi","io.github.zzzyyylllty.sertraline.library.de.tr7zw.changeme.nbtapi")
    relocate("io.github.projectunified.uniitem","io.github.zzzyyylllty.sertraline.library.com.uniitem")
    relocate("com.fasterxml.jackson","io.github.zzzyyylllty.sertraline.library.com.fasterxml.jackson")
    relocate("com.mojang.datafixerupper","io.github.zzzyyylllty.sertraline.library.com.mojang.datafixerupper")
    relocate("io.netty.handler.codec.http", "io.github.zzzyyylllty.sertraline.library.netty.handler.codec.http")
    relocate("io.netty.handler.codec.rtsp", "io.github.zzzyyylllty.sertraline.library.netty.handler.codec.rtsp")
    relocate("io.netty.handler.codec.spdy", "io.github.zzzyyylllty.sertraline.library.netty.handler.codec.spdy")
    relocate("io.netty.handler.codec.http2", "io.github.zzzyyylllty.sertraline.library.netty.handler.codec.http2")
    relocate("org.tabooproject.fluxon","io.github.zzzyyylllty.sertraline.library.org.tabooproject.fluxon")
    relocate("com.github.ben-manes.caffeine","io.github.zzzyyylllty.sertraline.library.com.github.ben-manes.caffeine")
}

tasks {
    jar {
        archiveFileName.set("${rootProject.name}-${archiveFileName.get().substringAfter('-')}")
//        destinationDirectory.set(file("F:\\20250207\\Ets\\plugins"))
        rootProject.subprojects.forEach { from(it.sourceSets["main"].output) }
    }
}