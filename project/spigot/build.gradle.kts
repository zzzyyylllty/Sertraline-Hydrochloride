import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

taboolib { subproject = true }

// 平台适配层：
//   main   源集 = 共享公共契约（PaperEventHooks，纯 Bukkit 类型）
//   paper  源集 = Paper 实现（PlatformCompat，直接调用 Paper API）
//   spigot 源集 = Spigot 实现（PlatformCompat，纯 Bukkit API，零反射）
// 两个 PlatformCompat 同名同签名，打包时严格二选一，绝不共存于同一 jar。
sourceSets {
    create("paper") { kotlin.srcDir("src/paper/kotlin") }
    create("spigot") { kotlin.srcDir("src/spigot/kotlin") }
}

dependencies {
    // main 源集（PaperEventHooks）仅使用 Bukkit 类型
    compileOnly("ink.ptms.core:v12104:12104:universal")

    // paper 源集：paper-api + main 公共类
    "paperCompileOnly"(rootProject.libs.paperapi)
    "paperCompileOnly"(sourceSets["main"].output)

    // spigot 源集：Spigot 等价 API（ink.ptms.core）+ main 公共类 + adventure（该 API 不带 adventure，需显式声明）
    "spigotCompileOnly"("ink.ptms.core:v12104:12104:universal")
    "spigotCompileOnly"("ink.ptms.core:v12104:12104:mapped")
    // action bar 需要 bungee 聊天 API（spigot-api 的传递依赖，运行时由服务端提供）
    "spigotCompileOnly"("net.md-5:bungeecord-chat:1.20-R0.2")
    "spigotCompileOnly"(sourceSets["main"].output)
    "spigotCompileOnly"(rootProject.libs.bundles.adventure)
    // data component 由 EmbianComponent 负责（NMS 反射路径，Spigot 无 Paper API 可用）
    "spigotCompileOnly"(files("$rootDir/libs-public/EmbianComponent-1.2.2.jar"))
}

configurations {
    // 根 allprojects 注入的 paper-api 只对 paper 源集可见，其余源集排除
    getByName("compileOnly") {
        exclude(group = "io.papermc.paper", module = "paper-api")
    }
    getByName("spigotCompileOnly") {
        exclude(group = "io.papermc.paper", module = "paper-api")
    }
    create("paperOutput")
    create("spigotOutput")
}

// 平台类目录：paper/spigot 两个源集同名类 PlatformCompat 严格二选一同步进目录。
// 产物为 Provider<Directory>，消费方 from() 时按目录展开（与其它子模块 main 源集一致），
// 且解析发生在执行期，规避配置期解析导致的 afterEvaluate 冲突。
val paperPlatformClasses by tasks.registering(Sync::class) {
    from(sourceSets["main"].output)
    from(sourceSets["paper"].output)
    into(layout.buildDirectory.dir("platform/paper"))
}

val spigotPlatformClasses by tasks.registering(Sync::class) {
    from(sourceSets["main"].output)
    from(sourceSets["spigot"].output)
    into(layout.buildDirectory.dir("platform/spigot"))
}

artifacts {
    add("paperOutput", paperPlatformClasses.map { it.destinationDir }) {
        builtBy(paperPlatformClasses)
    }
    add("spigotOutput", spigotPlatformClasses.map { it.destinationDir }) {
        builtBy(spigotPlatformClasses)
    }
}
