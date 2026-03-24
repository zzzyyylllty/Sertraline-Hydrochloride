rootProject.name = "Sertraline"
include(":project:common")
include(":project:common-files")
include(":project:premium")
include(":plugin")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("gradle/libs.version.toml"))
        }
    }
}

pluginManagement {
    repositories {
        maven("https://repo.tabooproject.org/repository/releases")
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}