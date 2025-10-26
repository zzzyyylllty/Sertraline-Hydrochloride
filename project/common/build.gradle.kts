import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

taboolib { subproject = true }

dependencies {
    implementation("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
}

tasks.withType(KotlinCompile::class.java) {
    kotlinOptions {
        freeCompilerArgs = listOf("-module-name", "zap_common")
    }
}