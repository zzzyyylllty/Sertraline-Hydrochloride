import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import io.izzel.taboolib.gradle.*
import io.izzel.taboolib.gradle.Basic
import io.izzel.taboolib.gradle.Bukkit
import io.izzel.taboolib.gradle.BukkitFakeOp
import io.izzel.taboolib.gradle.BukkitHook
import io.izzel.taboolib.gradle.BukkitUI
import io.izzel.taboolib.gradle.BukkitUtil
import io.izzel.taboolib.gradle.BukkitNMS
import io.izzel.taboolib.gradle.BukkitNMSUtil
import io.izzel.taboolib.gradle.BukkitNMSItemTag
import io.izzel.taboolib.gradle.BukkitNMSEntityAI
import io.izzel.taboolib.gradle.BukkitNMSDataSerializer
import io.izzel.taboolib.gradle.I18n
import io.izzel.taboolib.gradle.Metrics
import io.izzel.taboolib.gradle.MinecraftChat
import io.izzel.taboolib.gradle.MinecraftEffect
import io.izzel.taboolib.gradle.CommandHelper
import io.izzel.taboolib.gradle.Database
import io.izzel.taboolib.gradle.DatabasePlayer
import io.izzel.taboolib.gradle.Ptc
import io.izzel.taboolib.gradle.PtcObject
import io.izzel.taboolib.gradle.Kether


plugins {
    java
    id("io.izzel.taboolib") version "2.0.22"
    id("org.jetbrains.kotlin.jvm") version "1.8.22"
}

taboolib {
    env {
        install(Basic)
        install(Bukkit)
        install(BukkitFakeOp)
        install(BukkitHook)
        install(BukkitUI)
        install(BukkitUtil)
        install(BukkitNMS)
        install(BukkitNMSUtil)
        install(BukkitNMSItemTag)
        install(BukkitNMSEntityAI)
        install(BukkitNMSDataSerializer)
        install(I18n)
        install(Metrics)
        install(MinecraftChat)
        install(MinecraftEffect)
        install(CommandHelper)
        install(Database)
        install(DatabasePlayer)
        install(Ptc)
        install(PtcObject)
        install(Kether)
    }
    description {
        name = "Sertraline"
        desc("An Advanced Item Plugin")
        contributors {
            name("AkaCandyKAngel")
        }
    }
    version { taboolib = "6.2.2" }
}
configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlin:kotlin-stdlib:1.8.22")
    }
}
repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.papermc.io/repository/maven-snapshots/")
    maven {
        name = "adyeshach"
        url = uri("https://repo.tabooproject.org/repository/releases/")
    }
    maven {
        name = "mythicmobs"
        url = uri("https://mvn.lumine.io/repository/maven-public/")
    }
    maven {
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
    maven("https://repo.tabooproject.org/repository/releases")
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
}

dependencies {
    compileOnly("ink.ptms.core:v12101:12101:mapped")
    compileOnly("ink.ptms.core:v12101:12101:universal")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
    implementation("me.clip:placeholderapi:2.11.5")
    implementation("io.lumine:Mythic-Dist:5.6.1") { isTransitive = false }
    implementation("ink.ptms:Zaphkiel:2.0.14") { isTransitive = false }
    implementation("io.th0rgal:oraxen:1.165.0") { isTransitive = false }
    implementation("com.github.LoneDev6:api-itemsadder:3.6.3-beta-14") { isTransitive = false }
    implementation("de.tr7zw:item-nbt-api-plugin:2.12.2") { isTransitive = false }
    implementation("com.github.FrancoBM12:API-MagicCosmetics:2.2.7") { isTransitive = false }
    implementation("io.lumine:MythicLib-dist:1.6.2-SNAPSHOT") { isTransitive = false }
    implementation("net.Indyuce:MMOItems-API:6.10-SNAPSHOT")
    implementation("pers.neige.neigeitems:NeigeItems:1.17.24") { isTransitive = false }
    implementation("com.willfp:eco:6.71.3") { isTransitive = false }
    implementation("com.willfp:EcoItems:5.49.1") { isTransitive = false }

}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
