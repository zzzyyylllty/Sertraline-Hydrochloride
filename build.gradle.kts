import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import io.izzel.taboolib.gradle.*
import io.izzel.taboolib.gradle.BukkitNMSUtil
import org.gradle.internal.impldep.org.apache.http.client.methods.RequestBuilder.options
import org.gradle.kotlin.dsl.implementation
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    id("io.izzel.taboolib") version "2.0.22"
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
    id("com.gradleup.shadow") version "9.0.0-beta12"
    id("maven-publish")
}

taboolib {
    description {
        name("Sertraline")
        desc("An advanced item plugin. ChoTen item management plugin.")
        contributors {
            // 作者名称
            name("AkaCandyKAngel")
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
            // 可选依赖
            // name("XXX").optional(true)
        }
    }
    env {
        // 调试模式
        debug = true
        // 是否在开发模式下强制下载依赖
        forceDownloadInDev = true
        // 中央仓库地址
        repoCentral = "https://maven.aliyun.com/repository/central"
        // TabooLib 仓库地址
        repoTabooLib = "https://repo.tabooproject.org/repository/releases"
        // 依赖下载目录
        fileLibs = "libraries"
        // 资源下载目录
        fileAssets = "assets"
        // 是否启用隔离加载器（即完全隔离模式）
        enableIsolatedClassloader = false
        install(Basic, Bukkit, BukkitHook, BukkitNMSUtil, Database, Kether, CommandHelper, BukkitNMSItemTag, JavaScript)
    }
    version {
        taboolib = "6.2.3-20d868d" // 6.2.3-20d868d
        coroutines = "1.7.3"
        // 跳过 Kotlin 加载
        skipKotlin = false
        // 跳过 Kotlin 重定向
        skipKotlinRelocate = false
        // 跳过 TabooLib 重定向
        skipTabooLibRelocate = false

    }
    relocate("top.maplex.arim","xxx.xxx.arim")
    relocate("ink.ptms.um","xx.um")
    relocate("com.google", "io.github.zzzyyylllty.sertraline.library.google")
    relocate("com.alibaba", "io.github.zzzyyylllty.sertraline.library.com.alibaba")
    relocate("kotlinx.serialization", "kotlinx.serialization170")
}

repositories {
    mavenCentral()
    jcenter()

    maven("https://dl.bintray.com/kotlin/kotlinx/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.papermc.io/repository/maven-snapshots/")
    maven("https://repo.tabooproject.org/repository/releases/")
    maven {
        name = "mythicmobs"
        url = uri("https://mvn.lumine.io/repository/maven-public/")
    }
    maven {
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.codemc.org/repository/maven-public")
    maven("https://repo.rosewooddev.io/repository/public/")
    maven("https://repo.opencollab.dev/main/")
    maven("https://repo.oraxen.com/releases")
    maven("https://nexus.phoenixdevt.fr/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://r.irepo.space/maven/")
    maven("https://repo.auxilor.io/repository/maven-public/")
    maven("https://repo.hibiscusmc.com/releases/")
    maven("https://repo.tabooproject.org/repository/releases/")
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/") {
        name = "sonatype-oss-snapshots"
    }
    maven("https://repo.momirealms.net/releases/")
    maven("https://maven.aliyun.com/repository/google")
    maven("https://maven.aliyun.com/repository/public")
    maven("https://maven.aliyun.com/repository/jcenter")
}

dependencies {
    // compileOnly("ink.ptms.core:v12004:12004:mapped")
    // compileOnly("ink.ptms.core:v12004:12004:universal")
    implementation("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
    //implementation("org.jetbrains.kotlin:kotlin-libs:2.0.0")
//    compileOnly("net.momirealms:craft-engine-core:0.0.41")
//    compileOnly("net.momirealms:craft-engine-bukkit:0.0.41")
//    // implementation("me.clip:placeholderapi:2.11.5")
//    compileOnly("io.lumine:Mythic-Dist:5.6.1") { isTransitive = false }
//    compileOnly("ink.ptms:Zaphkiel:2.0.14") { isTransitive = false }
//    compileOnly("io.th0rgal:oraxen:1.189.0") { isTransitive = false }
//    compileOnly("com.github.LoneDev6:api-itemsadder:3.6.3-beta-14") { isTransitive = false }
//    // compileOnly("io.rokuko.azureflow.api.AzureFlowAPI:1.1.0.1")
//
//    // compileOnly("de.tr7zw:item-nbt-api-plugin:2.14.1") { isTransitive = false }
//
//    compileOnly("com.github.FrancoBM12:API-MagicCosmetics:2.2.7") { isTransitive = false }
//    compileOnly("io.lumine:MythicLib-dist:1.6.2-SNAPSHOT") { isTransitive = false }
//    compileOnly("net.Indyuce:MMOItems-API:6.10-SNAPSHOT")
//    compileOnly("pers.neige.neigeitems:NeigeItems:1.17.24") { isTransitive = false }
//    compileOnly("com.willfp:eco:6.71.3") { isTransitive = false }
//    compileOnly("com.willfp:EcoItems:5.49.1") { isTransitive = false }
//    compileOnly("com.github.Saukiya:SX-Item:4.4.0")
//
//    compileOnly("ink.ptms.chemdah:api:1.1.8") { isTransitive = false }
//
//    compileOnly("net.luckperms:api:5.4")
//    compileOnly("io.lumine:Mythic-Dist:5.6.1")

    implementation("org.yaml:snakeyaml:1.30")
    implementation("net.kyori:adventure-text-serializer-legacy:4.19.0")
    implementation("net.kyori:adventure-api:4.19.0")
    implementation("net.kyori:adventure-text-minimessage:4.19.0")
    implementation("net.kyori:adventure-nbt:4.19.0")
    // implementation("net.kyori:adventure-platform-bukkit:4.3.4")
    // implementation("com.beust:klaxon:5.5")

    taboo("ink.ptms:um:1.1.3") // universal mythicmobs
    //compileOnly("io.lumine:Mythic-Dist:5.6.1")
    compileOnly("ink.ptms.adyeshach:api:2.0.24")
    // testImplementation(kotlin("test"))
    taboo("top.maplex.arim:Arim:1.2.13")
    taboo("com.google.code.gson:gson:2.10.1")
    //taboo("org.jetbrains.kotlin:kotlin-reflect:1.8.22")
    taboo("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.0")
    taboo("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.7.0") { isTransitive = false }
    taboo("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.7.0") { isTransitive = false }


    // 本体
    implementation("org.tabooproject.reflex:analyser:1.1.4")
    implementation("org.tabooproject.reflex:fast-instance-getter:1.1.4")
    implementation("org.tabooproject.reflex:reflex:1.1.4") // 需要 analyser 模块

    // 本体依赖
    implementation("org.ow2.asm:asm:9.2")
    implementation("org.ow2.asm:asm-util:9.2")
    implementation("org.ow2.asm:asm-commons:9.2")
    implementation(kotlin("stdlib"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "21"
        freeCompilerArgs = listOf("-Xjvm-default=all","-Xskip-prerelease-check","-Xallow-unstable-dependencies")
        // Skip NeigeItems InCompatibility Kotlin Version
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
tasks.withType<JavaCompile> {
    options.release.set(21)
    options.encoding = "UTF-8"
}

// 定义源码包任务
val sourcesJar by tasks.registering(Jar::class) {
    from(sourceSets.main.get().allJava)
    archiveClassifier.set("sources")
}

// 定义文档包任务（可选）
val javadocJar by tasks.registering(Jar::class) {
    from(tasks.javadoc)
    archiveClassifier.set("javadoc")
}

publishing {
    publications {
        create<MavenPublication>("Sertraline") {
            // 主构件（自动从 java 组件生成）
            from(components["java"])

            val rootVersion = "1.1.2"
            val rootGroup = "io.github.zzzyyylllty.sertraline"
            // 添加源码包（必须指定分类器）
            artifact(sourcesJar.get()) {
                classifier = "sources"
            }

            // 添加文档包（可选）
            artifact(javadocJar.get()) {
                classifier = "javadoc"
            }

            // 其他构件配置...
        }
    }
}