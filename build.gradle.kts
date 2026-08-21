import io.izzel.taboolib.gradle.*
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// 平台属性：paper（默认，高版本 API + Java 21）/ spigot / legacy12（MC 1.12.2 API 面 + Java 8）。
// platform 是配置期属性，一次构建进程只能取一个值；Spigot 与 Legacy 侧由 :plugin 的嵌套构建完成。
val platform: String = (findProperty("platform") ?: "paper") as String

plugins {
    java
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.taboolib)
    kotlin("plugin.serialization") version "2.0.0"
    // paperweight id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
    id("com.vanniktech.maven.publish") version "0.34.0"
    id("maven-publish")
    signing
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "io.izzel.taboolib")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    // paperweight apply(plugin = "io.papermc.paperweight.userdev")
    apply(plugin = "maven-publish")
    apply(plugin = "com.vanniktech.maven.publish")

    group = "io.github.zzzyyylllty.sertraline"
    version = rootProject.version


    configurations {
        all {
            exclude(group = "com.mojang.datafixers")
        }
    }

    taboolib {
        env {
            debug = false
            forceDownloadInDev = false
            // repoCentral = "https://maven.aliyun.com/repository/central" — removed, use default
            repoTabooLib = "https://repo.tabooproject.org/repository/releases"
            fileLibs = "libraries"
            fileAssets = "assets"
            enableIsolatedClassloader = false
            install(
                Basic, Bukkit, BukkitHook, BukkitNMSUtil, Database, Kether,
                CommandHelper, BukkitNMSItemTag, JavaScript, BukkitUI,
                BukkitUtil, Jexl, Metrics, DatabasePlayer, BukkitNMS,
                PtcObject
            )
            disableOnUnsupportedVersion = false
            disableOnSkippedVersion = false
        }
        version {
            taboolib = rootProject.libs.versions.taboolib.get()
            coroutines = "1.7.3"
            skipKotlin = false
            skipKotlinRelocate = false
            skipTabooLibRelocate = false

        }
    }

    repositories {
        mavenLocal()
        mavenCentral()


        maven("https://repo-momi.gtemc.cn/")
        // TabooLib
        maven {
            url = uri("https://repo.tabooproject.org/repository/releases/")
        }
        maven("https://repo1.maven.org/maven2")
        maven("https://jitpack.io")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

        // Paper 相关
        maven("https://repo.papermc.io/repository/maven-public/")

        // 插件依赖仓库
        maven("https://repo.auxilor.io/repository/maven-public/")
        maven("https://nexus.phoenixdevt.fr/repository/maven-public/")
        maven("https://mvn.lumine.io/repository/maven-public/") // MythicMobs
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://repo.codemc.io/repository/maven-public/")
        maven("https://repo.dmulloy2.net/repository/public/")

        // 其他仓库
        maven("https://jitpack.io")
        maven("https://repo.gtemc.net/releases/")
        maven {
            url = uri("https://nexus.maplex.top/repository/maven-public/")
            isAllowInsecureProtocol = true
        }
        maven("https://repo.skriptlang.org/releases")
        maven("https://repo.destroystokyo.com/repository/maven-public/")
    }

    dependencies {
        // TabooLib 依赖
        // taboo("com.github.cryptomorin:XSeries:13.6.0+26.1")   .
        taboo("io.github.almighty-satan:XSeries:13.6.0+26.1")

        taboo("cn.gtemc:itembridge:1.0.31")

        taboo(platform(rootProject.libs.kotlincrypto.bom))
        taboo(rootProject.libs.kotlincrypto.sha2)
        taboo(rootProject.libs.kotlin.stdlib)

        // 平台 API：ink.ptms.core 的 universal = Spigot 完整 API（org.bukkit+org.spigotmc），
        // mapped = Mojang 映射的 NMS 类。与 org.spigotmc:spigot-api 完全等价。
        // Paper 专属 API（io.papermc.paper.*）只在 :project:spigot 的 paper 源集内声明。
        // legacy12：MC 1.12.2 核心 v11200（1.17.1 起才有 universal/mapped 子类型，v11200 只有默认类型）
        if (platform == "legacy12") {
            compileOnly("ink.ptms.core:v11200:11200")
        } else {
            compileOnly("ink.ptms.core:v12104:12104:universal")
            compileOnly("ink.ptms.core:v12104:12104:mapped")
        }

        // 原 API jar 的传递依赖（paper-api/spigot-api 均依赖 guava），现显式声明
        compileOnly("com.google.guava:guava:33.3.1-jre")

        // 编译时依赖
        compileOnly("ink.ptms.chemdah:api:1.1.17")
        compileOnly("ink.ptms:nms-all:1.0.0")
//        compileOnly("com.willfp:eco:6.77.2")
        compileOnly("com.github.SkriptLang:Skript:2.14.3")

        taboo("ink.ptms:um:1.2.1")
        // MythicLib 不再支持 1.12.2（1.7.1 为 Java 11 字节码），legacy12 丢弃
        if (platform != "legacy12") {
            compileOnly(rootProject.libs.mythiclibdist)
            compileOnly("io.lumine:Mythic-Dist:5.6.1")
        }
        compileOnly(rootProject.libs.placeholderapi)
        compileOnly(rootProject.libs.packeteventsspigot)
//        compileOnly(rootProject.libs.datafixerupper)
        compileOnly(rootProject.libs.fluxoncore)
        compileOnly(rootProject.libs.netty.all)
        // caffeine 3.x 需 Java 11；legacy12 用 2.9.3（Java 8）
        compileOnly(if (platform == "legacy12") rootProject.libs.caffeineLegacy else rootProject.libs.caffeine)
        compileOnly(rootProject.libs.gson)
        compileOnly(rootProject.libs.bundles.graalvm)
        compileOnly(rootProject.libs.bundles.jackson)
        taboo("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

        // private dependency
        // not affect use
        compileOnly(files("$rootDir/libs/ChoTenTech-1.0.0-api.jar"))


        // EmbianComponent：paper/spigot 用 1.2.2；legacy12 用 Java 8 字节码版本（数据组件运行时版本守卫，仅打包不调用）
        taboo(files("$rootDir/libs-public/EmbianComponent-${if (platform == "legacy12") "1.2.2-java8" else "1.2.2"}.jar"))
//        taboo("com.github.zzzyyylllty:EmbianComponent:1.2.0")

        // sparrow-minimessage：Java 21 字节码（major 65），legacy12（Java 8）不捆绑仅编译用，运行时由 FastMiniMessage 强制关闭
        if (platform == "legacy12") {
            compileOnly(files("$rootDir/libs-public/sparrow-minimessage-0.5.jar"))
        } else {
            taboo(files("$rootDir/libs-public/sparrow-minimessage-0.5.jar"))
        }


        // 运行时依赖
        implementation(rootProject.libs.bundles.reflex)
        implementation(rootProject.libs.bundles.asm)
        implementation(rootProject.libs.bundles.adventure)
    }

    tasks.withType<KotlinCompile> {
        compilerOptions {
            freeCompilerArgs.addAll(
                listOf(
                    "-Xjvm-default=all",
                    "-Xskip-prerelease-check",
                    "-Xallow-unstable-dependencies"
                ) + if (platform == "legacy12") listOf("-Xjdk-release=8") else emptyList()
            )
            // legacy12 目标 Java 8（1.12.2 服务端只能跑 Java 8）；其余平台 Java 21
            jvmTarget.set(if (platform == "legacy12") JvmTarget.JVM_1_8 else JvmTarget.JVM_21)
        }
    }

    tasks.withType<JavaCompile> {
        options.release.set(if (platform == "legacy12") 8 else 21)
        options.encoding = "UTF-8"
    }

    // 源码包
    val sourcesJar by tasks.registering(Jar::class) {
        from(sourceSets.main.get().allJava)
        archiveClassifier.set("sources")
    }

    // 文档包
    val javadocJar by tasks.registering(Jar::class) {
        from(tasks.javadoc)
        archiveClassifier.set("javadoc")
    }

}

// common-files 项目特殊配置
project(":project:common-files") {
    tasks.withType<ProcessResources>().configureEach {
        filesMatching("**/*.json") {
            expand(
                "nashornVersion" to rootProject.libs.versions.nashorn.get(),
                "graaljsVersion" to rootProject.libs.versions.graalvm.get(),
                "jexlVersion" to rootProject.libs.versions.jexl.get(),
                "gsonVersion" to rootProject.libs.versions.gson.get(),
                "kotlincryptoVersion" to rootProject.libs.versions.kotlinCrypto.get(),
                // legacy12 运行时下载 caffeine 2.9.3（3.x 需 Java 11）
                "caffeineVersion" to (if (platform == "legacy12") rootProject.libs.versions.caffeineLegacy.get() else rootProject.libs.versions.caffeine.get()),
                "fluxonVersion" to rootProject.libs.versions.fluxon.get(),
                "datafixerupperVersion" to rootProject.libs.versions.datafixerupper.get(),
                "asmVersion" to rootProject.libs.versions.asm.get(),
//                "uniItemVersion" to rootProject.libs.versions.uniItem.get(),
                "adventureVersion" to rootProject.libs.versions.adventure.get(),
                "jacksonVersion" to rootProject.libs.versions.jackson.get(),
                "arimVersion" to rootProject.libs.versions.arim.get()
            )
        }
        // legacy12（Java 8）：DFU 8（Java 17+ 字节码）与 GraalJS（Java 11+）运行时下载即炸，
        // 直接不打包 json，运行时找不到依赖描述就不会下载
        if (platform == "legacy12") {
            exclude("**/dependencies/datafixerupper.json", "**/dependencies/graaljs.json")
        }
    }
}

//publishing {
//    publications {
//        create<MavenPublication>("mavenJava") {
//            from(components["java"])
//
//            // 配置 POM 文件信息，这是 Maven 仓库必须的
//            pom {
//                name.set("Sertraline")
//                description.set("A concise description.")
//                url.set("https://github.com/zzzyyylllty/Sertraline-Hydrochloride")
//                licenses {
//                    license {
//                        name.set("MIT License")
//                        url.set("https://mit-license.org/")
//                    }
//                }
//                developers {
//                    developer {
//                        id.set("zzzyyylllty")
//                        name.set("AkaCandyKAngel")
//                        email.set("3631901756@qq.com")
//                    }
//                }
//                scm {
//                    url.set("https://github.com/zzzyyylllty/Sertraline-Hydrochloride")
//                }
//            }
//        }
//    }
//
//    repositories {
//        maven {
//            name = "OSSRH"
//            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
//            credentials {
//                username = System.getenv("CENTRAL_TOKEN_USERNAME")
//                password = System.getenv("CENTRAL_TOKEN_PASSWORD")
//            }
//        }
//    }
//
//}

//
//signing {
//    sign(publishing.publications["maven"])
//}

// 默认 `./gradlew build` 同时产出 Paper、Spigot、Legacy 三套 jar。
// platform 是配置期属性，一次构建进程只能取一个值，Spigot/Legacy 侧由 :plugin 嵌套构建完成；
// 嵌套构建自身 platform=spigot/legacy12，此依赖不成立，天然无递归。
tasks.named("build") {
    if (platform == "paper") {
        dependsOn(":plugin:buildSpigot", ":plugin:buildLegacy")
    }
}

// 仅构建当前平台的 jar（paper → Standard），不触发 Spigot/Legacy 嵌套构建
tasks.register("buildStandard") {
    group = "build"
    description = "Build only the Standard (Paper) platform jar, skip Spigot/Legacy nested builds"
    dependsOn(":plugin:build")
}