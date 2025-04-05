import io.izzel.taboolib.gradle.*
import io.izzel.taboolib.gradle.BukkitNMSUtil
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    id("io.izzel.taboolib") version "2.0.22"
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
}

taboolib {
    description {
        name("Sertraline")
        desc("An advanced item plugin.")
        contributors {
            // 作者名称
            name("AkaCandyKAngel")
        }
        dependencies {
            // 依赖插件名称（不要误会成写自己，会触发 self-loop 错误）
            name("MythicLib")
            // 可选依赖
            // name("XXX").optional(true)
        }
    }
    env {
        // 安装模块
        install(Basic, Bukkit, BukkitHook, BukkitNMSUtil,Database, Kether, CommandHelper, BukkitNMSItemTag)
    }
    version {
        taboolib = "6.2.3-20d868d"
    }
    relocate("top.maplex.arim","xxx.xxx.arim")
    relocate("ink.ptms.um","xx.um")
    relocate("kotlinx.serialization", "kotlinx.serialization133")
    relocate("com.google", "io.github.zzzyyylllty.sertraline.library.google")
    relocate("com.alibaba", "io.github.zzzyyylllty.sertraline.library.com.alibaba")
}

repositories {
    mavenCentral()
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
}

dependencies {
    // compileOnly("ink.ptms.core:v12004:12004:mapped")
    // compileOnly("ink.ptms.core:v12004:12004:universal")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
    ///implementation("net.momirealms:craft-engine-core:0.0.41")
    //implementation("net.momirealms:craft-engine-bukkit:0.0.41")
    implementation("me.clip:placeholderapi:2.11.5")
    compileOnly("io.lumine:Mythic-Dist:5.6.1") { isTransitive = false }
    compileOnly("ink.ptms:Zaphkiel:2.0.14") { isTransitive = false }
    implementation("io.th0rgal:oraxen:1.189.0") { isTransitive = false }
    implementation("com.github.LoneDev6:api-itemsadder:3.6.3-beta-14") { isTransitive = false }
    compileOnly("de.tr7zw:item-nbt-api-plugin:2.14.1") { isTransitive = false }
    compileOnly("com.github.FrancoBM12:API-MagicCosmetics:2.2.7") { isTransitive = false }
    compileOnly("io.lumine:MythicLib-dist:1.6.2-SNAPSHOT") { isTransitive = false }
    compileOnly("net.Indyuce:MMOItems-API:6.10-SNAPSHOT")
    compileOnly("pers.neige.neigeitems:NeigeItems:1.17.24") { isTransitive = false }
    // compileOnly("com.willfp:eco:6.71.3") { isTransitive = false }
    // compileOnly("com.willfp:EcoItems:5.49.1") { isTransitive = false }
    implementation("com.github.Saukiya:SX-Item:4.4.0")
    implementation("ink.ptms.chemdah:api:1.1.8") { isTransitive = false }
    compileOnly("net.luckperms:api:5.4")
    implementation("me.clip:placeholderapi:2.11.5")
    compileOnly("io.lumine:Mythic-Dist:5.6.1")
    implementation("net.kyori:adventure-text-serializer-legacy:4.19.0")
    // implementation("net.kyori:adventure-platform-bukkit:4.3.4")
    // implementation("com.beust:klaxon:5.5")
    taboo("com.beust:klaxon:5.6")
    taboo("ink.ptms:um:1.1.3") // universal mythicmobs
    implementation("net.kyori:adventure-api:4.19.0")
    compileOnly("ink.ptms.adyeshach:api:2.0.24")
    compileOnly(fileTree("libs"))
    implementation("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    implementation("net.kyori:adventure-text-minimessage:4.19.0")
    implementation("net.kyori:adventure-nbt:4.19.0")
    testImplementation(kotlin("test"))
    taboo("top.maplex.arim:Arim:1.2.13")
    taboo("com.alibaba.fastjson2:fastjson2-kotlin:2.0.56")
    implementation("org.tabooproject.reflex:analyser:1.1.4")
    implementation("org.tabooproject.reflex:fast-instance-getter:1.1.4")
    implementation("org.tabooproject.reflex:reflex:1.1.4") // 需要 analyser 模块
    // 本体依赖
    implementation("org.ow2.asm:asm:9.2")
    implementation("org.ow2.asm:asm-util:9.2")
    implementation("org.ow2.asm:asm-commons:9.2")
    //taboo("org.jetbrains.kotlin:kotlin-reflect:1.8.22")
    taboo("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.6.3") { isTransitive = false }
    taboo("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.6.3") { isTransitive = false }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = listOf("-Xjvm-default=all","-Xskip-prerelease-check","-Xallow-unstable-dependencies")
        // Skip NeigeItems InCompatibility Kotlin Version
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
tasks.withType<JavaCompile> {
    options.release.set(17)
    options.encoding = "UTF-8"
}