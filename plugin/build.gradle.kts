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
// legacy12（MC 1.12.2）与 spigot 都使用 :project:spigot 的 spigot 源集（纯 Bukkit API + 降级路径）
val useSpigotOutput: Boolean = platform != "paper"

val platformOutput by configurations.creating

// Spigot 专用：捆绑 adventure（Paper 由服务端全局提供；Spigot 无 adventure，
// 运行期下载的副本会被 relocate 到 dep.kyori，而插件字节码引用的是未 relocate 的
// net.kyori.*，因此必须把原命名空间类直接内嵌进 Spigot jar）
val bundleAdventure by configurations.creating

dependencies {
    // 项目依赖：保证 :project:spigot 先于本项目完成配置，
    // 并按平台严格二选一取其 jar 产物（paper 源集 / spigot 源集）
    "platformOutput"(project(":project:spigot", configuration = if (useSpigotOutput) "spigotOutput" else "paperOutput"))
    if (useSpigotOutput) {
        bundleAdventure(rootProject.libs.bundles.adventure)
    }
}

tasks {
    val taboolibMainTask = named("taboolibMainTask")

    // 平台类目录 Sync 任务（:project:spigot 内注册）：先构建再展开进主 jar
    val platformClassesTask = if (useSpigotOutput) ":project:spigot:spigotPlatformClasses" else ":project:spigot:paperPlatformClasses"
    // 产物后缀：paper → Standard / spigot → Spigot / legacy12 → Legacy
    val jarSuffix = when (platform) {
        "spigot" -> "Spigot"
        "legacy12" -> "Legacy"
        else -> "Standard"
    }

    jar {
        archiveFileName.set("${rootProject.name}-${rootProject.version}-Premium-$jarSuffix.jar")
        dependsOn(platformClassesTask)
        from(platformOutput)
        rootProject.subprojects.forEach { proj ->
            if (proj.name != "spigot") {
                from(proj.sourceSets["main"].output)
            }
        }
        if (useSpigotOutput) {
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

        archiveFileName.set("${rootProject.name}-${version}-Free-$jarSuffix.jar")

        // 修复：使用 archiveFile 替代 archivePath
        from(zipTree(jar.get().archiveFile)) {
            exclude("io/github/zzzyyylllty/sertraline/premium/**")
        }
    }

    named("build") {
        // paperweight dependsOn(reobfJar)
        dependsOn(freeJar)
    }

    // 一次命令同时产出 Paper、Spigot、Legacy 三套 jar
    val buildAll by registering {
        group = "build"
        description = "Build all platform jars (Paper + Spigot + Legacy)"
        dependsOn(named("build"), named("buildSpigot"), named("buildLegacy"))
    }
}

// 便捷任务：一次命令构建 Spigot / Legacy 平台 jar（等价于 ./gradlew build -Pplatform=xxx -x test）。
// 不能用 GradleBuild：同一 Gradle 会话内两个指向相同 dir 的 GradleBuild 会在 IncludedBuildRegistry
// 撞构建路径（:Sertraline）报 "Included build ... same as included build"（gradle/gradle#13522）。
// 改为逐个 exec 独立 gradlew 进程（--no-daemon 保证不排队在忙碌的当前 daemon 上），行为等价。
val gradlewScript: String = rootProject.file(
    if (System.getProperty("os.name").lowercase().contains("win")) "gradlew.bat" else "gradlew"
).absolutePath
// 继承当前 Gradle 守护进程所在的 JDK，保证嵌套构建同样跑在 JDK 21
val gradleJdkHome: String = System.getProperty("java.home")

fun registerNestedPlatformBuild(name: String, platform: String, description: String): TaskProvider<Exec> {
    return tasks.register(name, Exec::class) {
        group = "build"
        this.description = description
        workingDir = rootProject.projectDir
        commandLine(
            gradlewScript, "-p", rootProject.projectDir,
            "-Dorg.gradle.java.home=$gradleJdkHome",
            "-Pplatform=$platform", "build", "-x", "test",
            "--no-daemon", "--console=plain"
        )
    }
}

val buildSpigot = registerNestedPlatformBuild(
    "buildSpigot", "spigot", "Build Spigot platform jar (Sertraline-*-Spigot.jar)"
)
val buildLegacy = registerNestedPlatformBuild(
    "buildLegacy", "legacy12", "Build Legacy platform jar (Sertraline-*-Legacy.jar, MC 1.12.2 / Java 8)"
)