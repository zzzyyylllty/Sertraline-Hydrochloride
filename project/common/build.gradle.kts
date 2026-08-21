import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

taboolib { subproject = true }

val platform: String = (findProperty("platform") ?: "paper") as String

dependencies {
    // legacy12 与 spigot 都使用 spigot 源集（纯 Bukkit API + 降级路径）；仅 paper 用 paper 源集
    if (platform != "paper") {
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

// legacy12（v11200 = MC 1.12.2 API）编译面不存在的类 → 排除对应文件：
// SmithItemEvent/SmithingInventory（1.16+）、TradeSelectEvent（1.14+），1.12.2 本身无锻造台；
// MythicLib 面（io.lumine.*，MythicLib 不支持 1.12.2）；现代合成系统（RecipeChoice/Tag/RecipeManager 均为 1.13+）
if (platform == "legacy12") {
    sourceSets["main"].kotlin.exclude(
        "**/listener/sertraline/itemSmithingRestriction.kt",
        "**/listener/sertraline/itemTradeRestriction.kt",
        "**/listener/action/itemActionRiptide.kt",
        "**/attribute/MythicLibAttributeProvider.kt",
        "**/util/dependencies/MMOUtil.kt",
        "**/recipe/ItemResolver.kt",
        "**/recipe/NMSRecipeFactory.kt",
        "**/util/SimpleRecipeHelper.kt",
        "**/config/loadRecipeFiles.kt",
        "**/listener/recipeBookListener.kt",
        "**/listener/sertraline/itemRecipe.kt"
    )
}
