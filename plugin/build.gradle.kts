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
            name("packetevents").optional(true)
            // 可选依赖
            // name("XXX").optional(true)
        }
    }
    relocate("top.maplex.arim","xxx.xxx.arim")
    relocate("ink.ptms.um","xx.um")
    relocate("com.google", "io.github.zzzyyylllty.sertraline.library.google")
    relocate("com.alibaba", "io.github.zzzyyylllty.sertraline.library.com.alibaba")
    relocate("kotlinx.serialization", "kotlinx.serialization170")
    relocate("io.github.projectunified.uniitem","io.github.zzzyyylllty.sertraline.library.com.uniitem")
    relocate("com.fasterxml.jackson","io.github.zzzyyylllty.sertraline.library.com.fasterxml.jackson")

}

tasks {
    jar {
        archiveFileName.set("${rootProject.name}-${archiveFileName.get().substringAfter('-')}")
        rootProject.subprojects.forEach { from(it.sourceSets["main"].output) }
    }
}

dependencies {
    // compileOnly("ink.ptms.core:v12004:12004:mapped")
    // compileOnly("ink.ptms.core:v12004:12004:universal")
    implementation("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
}