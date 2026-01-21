import io.izzel.taboolib.gradle.*
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.taboolib)
    kotlin("plugin.serialization") version "2.0.0"
    // paperweight id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
    id("maven-publish")
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "io.izzel.taboolib")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    // paperweight apply(plugin = "io.papermc.paperweight.userdev")
    apply(plugin = "maven-publish")

    group = "io.github.zzzyyylllty.sertraline"
    version = rootProject.version

    taboolib {
        env {
            debug = true
            forceDownloadInDev = false
            repoCentral = "https://maven.aliyun.com/repository/central"
            repoTabooLib = "https://repo.tabooproject.org/repository/releases"
            fileLibs = "libraries"
            fileAssets = "assets"
            enableIsolatedClassloader = false
            install(
                Basic, Bukkit, BukkitHook, BukkitNMSUtil, Database, Kether,
                CommandHelper, BukkitNMSItemTag, JavaScript, BukkitUI,
                BukkitUtil, Jexl, Metrics, DatabasePlayer, BukkitNMS
            )
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

        // 阿里云镜像（优先级高）
        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.aliyun.com/repository/google")

        // Paper 相关
        maven("https://repo.papermc.io/repository/maven-public/")

        // TabooLib
        maven("https://repo.tabooproject.org/repository/releases/")

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
    }

    dependencies {
        // TabooLib 依赖
        taboo("com.github.cryptomorin:XSeries:v13.6.0")
        taboo("io.github.zzzyyylllty:EmbianComponent:1.0.2")
        taboo("cn.gtemc:itembridge:1.0.17")
        taboo("ink.ptms:um:1.2.1")
        taboo(platform(rootProject.libs.kotlincrypto.bom))
        taboo(rootProject.libs.kotlincrypto.sha2)
        taboo(rootProject.libs.kotlin.stdlib)

        // Paper 开发包
        // paperweight paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")
        compileOnly(rootProject.libs.paperapi)

        // 编译时依赖
        compileOnly("ink.ptms.chemdah:api:1.1.17")
        compileOnly("ink.ptms:nms-all:1.0.0")
        compileOnly("com.willfp:eco:6.77.2")
        compileOnly(rootProject.libs.mythiclibdist)
        compileOnly(rootProject.libs.placeholderapi)
        compileOnly(rootProject.libs.packeteventsspigot)
        compileOnly(rootProject.libs.datafixerupper)
        compileOnly(rootProject.libs.fluxoncore)
        compileOnly(rootProject.libs.netty.all)
        compileOnly(rootProject.libs.caffeine)
        compileOnly(rootProject.libs.gson)
        compileOnly(rootProject.libs.bundles.graalvm)
        compileOnly(rootProject.libs.bundles.jackson)

        // 本地依赖
        compileOnly(fileTree("libs"))

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
                )
            )
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    tasks.withType<JavaCompile> {
        options.release.set(21)
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

    publishing {
        publications {
            create<MavenPublication>("Sertraline") {
                from(components["java"])

                artifact(sourcesJar.get()) {
                    classifier = "sources"
                }

                artifact(javadocJar.get()) {
                    classifier = "javadoc"
                }
            }
        }
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
                "caffeineVersion" to rootProject.libs.versions.caffeine.get(),
                "fluxonVersion" to rootProject.libs.versions.fluxon.get(),
                "datafixerupperVersion" to rootProject.libs.versions.datafixerupper.get(),
                "uniItemVersion" to rootProject.libs.versions.uniItem.get(),
                "adventureVersion" to rootProject.libs.versions.adventure.get(),
                "jacksonVersion" to rootProject.libs.versions.jackson.get(),
                "arimVersion" to rootProject.libs.versions.arim.get()
            )
        }
    }
}