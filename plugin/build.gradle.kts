taboolib {
    description {
        name("Sertraline")
        desc("An advanced item plugin. ChoTen item management plugin.")
        contributors {
            name("AkaCandyKAngel")
            name("jhqwqmc")
        }
        dependencies {
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
            name("eco").optional(true)
            name("PlaceholderAPI").optional(true)
        }
    }

    relocate("top.maplex.arim", "io.github.zzzyyylllty.sertraline.dep.arim")
    relocate("ink.ptms.um", "io.github.zzzyyylllty.sertraline.dep.um")
    relocate("com.alibaba", "io.github.zzzyyylllty.sertraline.dep.alibaba")
    relocate("kotlinx.serialization", "kotlinx.serialization181")
    relocate("io.github.projectunified.uniitem", "io.github.zzzyyylllty.sertraline.dep.uniitem")
    relocate("com.fasterxml.jackson", "io.github.zzzyyylllty.sertraline.dep.jackson")
    relocate("com.mojang.datafixers", "io.github.zzzyyylllty.sertraline.dep.datafixers")
    relocate("io.netty.handler.codec.http", "io.github.zzzyyylllty.sertraline.dep.http")
    relocate("io.netty.handler.codec.rtsp", "io.github.zzzyyylllty.sertraline.dep.rtsp")
    relocate("io.netty.handler.codec.spdy", "io.github.zzzyyylllty.sertraline.dep.spdy")
    relocate("io.netty.handler.codec.http2", "io.github.zzzyyylllty.sertraline.dep.http2")
    relocate("org.tabooproject.fluxon", "io.github.zzzyyylllty.sertraline.dep.fluxon")
    relocate("com.github.benmanes.caffeine", "io.github.zzzyyylllty.sertraline.dep.caffeine")
    relocate("org.kotlincrypto", "io.github.zzzyyylllty.sertraline.dep.kotlincrypto")
}

tasks {
    val taboolibMainTask = named("taboolibMainTask")

    jar {
        archiveFileName.set("${rootProject.name}-${rootProject.version}-Premium.jar")
        rootProject.subprojects.forEach { from(it.sourceSets["main"].output) }
    }

    val freeJar by registering(Jar::class) {
        group = "build"
        description = "Generate FREE version jar by filtering premium classes"

        dependsOn(taboolibMainTask)
        dependsOn(jar)

        archiveFileName.set("${rootProject.name}-${version}-Free.jar")

        // 修复：使用 archiveFile 替代 archivePath
        from(zipTree(jar.get().archiveFile)) {
            exclude("io/github/zzzyyylllty/sertraline/premium/**")
        }
    }

    named("build") {
        // paperweight dependsOn(reobfJar)
        dependsOn(freeJar)
    }
}