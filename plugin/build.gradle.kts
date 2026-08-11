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
//    relocate("com.mojang.datafixers", "io.github.zzzyyylllty.sertraline.dep.datafixers")
    relocate("io.netty.handler.codec.http", "io.github.zzzyyylllty.sertraline.dep.http")
    relocate("io.netty.handler.codec.rtsp", "io.github.zzzyyylllty.sertraline.dep.rtsp")
    relocate("io.netty.handler.codec.spdy", "io.github.zzzyyylllty.sertraline.dep.spdy")
    relocate("io.netty.handler.codec.http2", "io.github.zzzyyylllty.sertraline.dep.http2")
    relocate("org.tabooproject.fluxon", "io.github.zzzyyylllty.sertraline.dep.fluxon")
    relocate("com.github.benmanes.caffeine", "io.github.zzzyyylllty.sertraline.dep.caffeine")
    relocate("org.kotlincrypto", "io.github.zzzyyylllty.sertraline.dep.kotlincrypto")
//    relocate("org.objectweb.asm", "io.github.zzzyyylllty.sertraline.dep.asm")
}


val platform: String = (findProperty("platform") ?: "paper") as String

val platformOutput by configurations.creating

// Spigot 专用：捆绑 adventure（Paper 由服务端全局提供；Spigot 无 adventure，
// 运行期下载的副本会被 relocate 到 dep.kyori，而插件字节码引用的是未 relocate 的
// net.kyori.*，因此必须把原命名空间类直接内嵌进 Spigot jar）
val bundleAdventure by configurations.creating

dependencies {
    // 项目依赖：保证 :project:spigot 先于本项目完成配置，
    // 并按平台严格二选一取其 jar 产物（paper 源集 / spigot 源集）
    "platformOutput"(project(":project:spigot", configuration = if (platform == "spigot") "spigotOutput" else "paperOutput"))
    if (platform == "spigot") {
        bundleAdventure(rootProject.libs.bundles.adventure)
    }
}

tasks {
    val taboolibMainTask = named("taboolibMainTask")

    // 平台类目录 Sync 任务（:project:spigot 内注册）：先构建再展开进主 jar
    val platformClassesTask = if (platform == "spigot") ":project:spigot:spigotPlatformClasses" else ":project:spigot:paperPlatformClasses"

    jar {
        archiveFileName.set(
            if (platform == "spigot") "${rootProject.name}-${rootProject.version}-Premium-Spigot.jar"
            else "${rootProject.name}-${rootProject.version}-Premium-Standard.jar"
        )
        dependsOn(platformClassesTask)
        from(platformOutput)
        rootProject.subprojects.forEach { proj ->
            if (proj.name != "spigot") {
                from(proj.sourceSets["main"].output)
            }
        }
        if (platform == "spigot") {
            from({ bundleAdventure.map { zipTree(it) } }) {
                exclude("META-INF/**", "module-info.class")
                duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            }
        }
    }

    val freeJar by registering(Jar::class) {
        group = "build"
        description = "Generate FREE version jar by filtering premium classes"

        dependsOn(taboolibMainTask)
        dependsOn(jar)

        archiveFileName.set(
            if (platform == "spigot") "${rootProject.name}-${version}-Free-Spigot.jar"
            else "${rootProject.name}-${version}-Free-Standard.jar"
        )

        // 修复：使用 archiveFile 替代 archivePath
        from(zipTree(jar.get().archiveFile)) {
            exclude("io/github/zzzyyylllty/sertraline/premium/**")
        }
    }

    named("build") {
        // paperweight dependsOn(reobfJar)
        dependsOn(freeJar)
    }

    // 便捷任务：一次命令构建 Spigot 平台 jar（等价于 ./gradlew build -Pplatform=spigot -x test）
    val buildSpigot by registering(GradleBuild::class) {
        group = "build"
        description = "Build Spigot platform jar (Sertraline-*-Spigot.jar)"
        dir = rootProject.projectDir
        tasks = listOf("build")
        // org.gradle.StartParameter.excludedTaskNames 的 getter 返回 Set 而 setter 收 Iterable，
        // 类型不一致 Kotlin 不认作 var，只能直接调用 setter；projectProperties 则可属性赋值
        startParameter.setExcludedTaskNames(listOf("test"))
        startParameter.projectProperties = startParameter.projectProperties + mapOf("platform" to "spigot")
    }

    // 一次命令同时产出 Paper 与 Spigot 两套 jar
    val buildAll by registering {
        group = "build"
        description = "Build both Paper and Spigot platform jars"
        dependsOn(named("build"), buildSpigot)
    }
}