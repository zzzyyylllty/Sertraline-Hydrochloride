import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

taboolib { subproject = true }

val platform: String = (findProperty("platform") ?: "paper") as String

dependencies {
    if (platform == "spigot") {
        compileOnly(project(":project:spigot", configuration = "spigotOutput"))
    } else {
        compileOnly(project(":project:spigot", configuration = "paperOutput"))
    }
}

configurations {
    // 根 allprojects 注入的 paper-api 只用于 Paper 构建
    if (platform == "spigot") {
        getByName("compileOnly") {
            exclude(group = "io.papermc.paper", module = "paper-api")
        }
    }
}
