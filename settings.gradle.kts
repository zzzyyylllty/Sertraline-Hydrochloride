rootProject.name = "SertralineHydrochloride"


pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://r.irepo.space/maven/") {
            content { includeGroupByRegex("^org\\.inksnow(\\..+|)\$") }
        }
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}