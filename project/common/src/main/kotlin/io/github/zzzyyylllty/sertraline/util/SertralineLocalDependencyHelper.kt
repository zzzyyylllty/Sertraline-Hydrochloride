package io.github.zzzyyylllty.sertraline.util

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.logger.severeS
import io.github.zzzyyylllty.sertraline.logger.warningS
import org.tabooproject.reflex.ReflexClass
import taboolib.common.ClassAppender
import taboolib.common.PrimitiveIO
import taboolib.common.PrimitiveSettings
import taboolib.common.env.*
import taboolib.common.env.aether.AetherResolver
import taboolib.common.env.legacy.Artifact
import taboolib.common.env.legacy.Dependency
import taboolib.common.env.legacy.DependencyDownloader
import taboolib.common.env.legacy.Repository
import java.io.File
import java.lang.reflect.Method
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.function.Consumer


val runtimeEnvDependency = RuntimeEnvDependency()

val method: Method = RuntimeEnvDependency::class.java.getDeclaredMethod(
    "loadDependencyLegacy",
    String::class.java,
    File::class.java,
    List::class.java,
    String::class.java,
    Boolean::class.javaPrimitiveType,
    Boolean::class.javaPrimitiveType,
    Boolean::class.javaPrimitiveType,
    List::class.java,
    Boolean::class.javaPrimitiveType
)

class SertralineLocalDependencyHelper {
    private val defaultLibrary: String = PrimitiveSettings.FILE_LIBS
    private val defaultRepositoryCentral: String? = PrimitiveSettings.REPO_CENTRAL

    fun getDependency(clazz: ReflexClass): MutableList<ParsedDependency> {
        val dependencyList: MutableList<ParsedDependency> = ArrayList<ParsedDependency>()
        val runtimeDependency = clazz.getAnnotationIfPresent(RuntimeDependency::class.java)
        if (runtimeDependency != null) {
            dependencyList.add(ParsedDependency(runtimeDependency.properties()))
        }
        val runtimeDependencies = clazz.getAnnotationIfPresent(RuntimeDependencies::class.java)
        runtimeDependencies?.mapList("value")
            ?.map { ParsedDependency(it) }
            ?.let { dependencyList.addAll(it) }
        return dependencyList
    }

    @JvmOverloads
    @Throws(Throwable::class)
    fun loadDependency(url: String, baseDir: File = File(defaultLibrary), repository: String? = null) {
        loadDependency(
            url,
            baseDir,
            ArrayList<JarRelocation?>(),
            repository,
            true,
            false,
            true,
            mutableListOf<DependencyScope?>(DependencyScope.RUNTIME, DependencyScope.COMPILE)
        )
    }

    @JvmOverloads
    @Throws(Throwable::class)
    fun loadDependency(
        url: String,
        baseDir: File,
        relocation: MutableList<JarRelocation?>,
        repository: String?,
        ignoreOptional: Boolean,
        ignoreException: Boolean,
        transitive: Boolean,
        scope: MutableList<DependencyScope?>,
        external: Boolean = true,
    ) {
        // 支持用户对源进行替换
        var repository = repository
        if (repository == null || repository.isEmpty()) {
            repository = defaultRepositoryCentral
        } else if (PrimitiveSettings.RUNTIME_PROPERTIES.containsKey("repo-$repository")) {
            repository = PrimitiveSettings.RUNTIME_PROPERTIES.getProperty("repo-$repository")
        }
        // 使用 Aether 处理依赖 - 已修改 - 由于 Aether 问题，添加使用旧版加载方法的保险选项，如果Aether出错则使用旧版加载方法。
        if (isAetherFound) {
            try {
                devLog("Start native Dependency loading for $url...")
                RuntimeEnv.ENV_DEPENDENCY.loadDependency(
                    url,
                    File(defaultLibrary),
                    relocation,
                    repository,
                    ignoreOptional,
                    ignoreException,
                    transitive,
                    scope,
                    external
                )
            } catch (ex: Throwable) {
                warningS("Native dependency parsing failed. Fallback to Legacy Dependency Loader.")
                ex.printStackTrace()

                method.isAccessible = true
                try {
                    method.invoke(
                        runtimeEnvDependency,
                        url,
                        baseDir,
                        relocation,
                        repository,
                        ignoreOptional,
                        ignoreException,
                        transitive,
                        scope,
                        external
                    )
                } catch (e: Exception) {
                    warningS("Legacy dependency parsing failed. Fallback to Default Repo Loader.")

                    try {
                    method.invoke(
                        runtimeEnvDependency,
                        url,
                        baseDir,
                        relocation,
                        "https://repo1.maven.org/maven2",
                        ignoreOptional,
                        ignoreException,
                        transitive,
                        scope,
                        external
                    )
                    } catch (e: Exception) {
                        severeS("Dependency loading failed for $url.")
                    }
                }
            }
        } else {
            devLog("Aether not found. directly use legacy solver.")
            method.isAccessible = true
            method.invoke(
                runtimeEnvDependency,
                url,
                baseDir,
                relocation,
                repository,
                ignoreOptional,
                ignoreException,
                transitive,
                scope,
                external
            )
        }
    }
    /**
     * 从本地文件中加载依赖
     */
    @Suppress("deprecation")
    @Throws(Throwable::class)
    fun loadFromLocalFile(url: URL?) {
        if (url == null) {
            severeS("An error in processing load dependency from local file: File Url is null.")
            return
        }
        url.openStream().use { inputStream ->
            val parsed = JsonParser().parse(PrimitiveIO.readFully(inputStream, StandardCharsets.UTF_8))
            if (!parsed.isJsonArray) {
                severeS("An error in processing load dependency from local file: Local file stream must be JsonArray.")
                return
            }
            val array = parsed.getAsJsonArray()
            for (element in array) {
                val `object` = element.getAsJsonObject()
                // 获取检查条件
                val test: MutableList<String?> = ArrayList<String?>()
                for (testElement in array(`object`, "test")) {
                    test.add(testElement.asString)
                }
                if (!test.isEmpty() && test.stream().allMatch { path: String? -> this.test(replaceTestTexts(path!!)) }) {
                    devLog("Test class $test founded, skipping dependency loading.")
                    continue
                }
                // 获取依赖信息
                val value = `object`.get("value").asString
                val repository = find(`object`, "repository", defaultRepositoryCentral)
                val transitive = find(`object`, "transitive")
                val ignoreOptional = find(`object`, "ignoreOptional")
                val ignoreException = find(`object`, "ignoreException")
                val external = find(`object`, "external")
                // 读取依赖范围
                val scopes: MutableList<DependencyScope?> = ArrayList<DependencyScope?>()
                for (scope in array(`object`, "scopes")) {
                    scopes.add(DependencyScope.valueOf(scope.asString.uppercase(Locale.getDefault())))
                }
                // 读取重定向规则
                val relocation: MutableList<JarRelocation?> = ArrayList<JarRelocation?>()
                val relocate = array(`object`, "relocate")
                var i = 0
                while (i + 1 < relocate.size()) {
                    relocation.add(JarRelocation(relocate.get(i).asString, relocate.get(i + 1).asString))
                    i += 2
                }
                // 加载依赖
                loadDependency(
                    value,
                    File(defaultLibrary),
                    relocation,
                    repository,
                    ignoreOptional,
                    ignoreException,
                    transitive,
                    scopes,
                    external
                )
            }
        }
    }

    fun test(path: String): Boolean {
        val test = if (path.startsWith("!")) path.substring(1) else path
        return !test.isEmpty() && isClassExistsSafety(test)
    }

    fun find(`object`: JsonObject, key: String?, def: String?): String? {
        return if (`object`.has(key)) `object`.get(key).getAsString() else def
    }

    fun find(`object`: JsonObject, key: String?): Boolean {
        return `object`.has(key) && `object`.get(key).getAsBoolean()
    }

    fun array(`object`: JsonObject, key: String?): JsonArray {
        return if (`object`.has(key)) `object`.getAsJsonArray(key) else JsonArray()
    }

    companion object {
        private var isAetherFound = false

        init {
            // 当服务端版本在 1.17+ 时，可借助服务端自带的 Aether 库完成依赖下载，兼容性更高。
            // 同时停止对 Legacy 的支持。
            try {
                Class.forName("org.eclipse.aether.graph.Dependency")
                isAetherFound = true
            } catch (e: ClassNotFoundException) {
                isAetherFound = false
            }
            // Mohist 直接不用 Aether
            try {
                Class.forName("com.mohistmc.MohistMC")
                isAetherFound = false
            } catch (ignored: ClassNotFoundException) {
            }
        }
    }
}

fun replaceTestTexts(str: String): String {
    val str = str.toString()
    val replaced = str
        .replaceUnrelocated("!top.maplex.arim","!io.github.zzzyyylllty.sertraline.dep.arim")
        .replaceUnrelocated("!ink.ptms.um","!io.github.zzzyyylllty.sertraline.dep.um")
        .replaceUnrelocated("!com.alibaba", "!io.github.zzzyyylllty.sertraline.dep.alibaba")
        .replaceUnrelocated("!kotlinx.serialization", "!kotlinx.serialization170")
        .replaceUnrelocated("!io.github.projectunified.uniitem","!io.github.zzzyyylllty.sertraline.dep.uniitem")
        .replaceUnrelocated("!com.fasterxml.jackson","!io.github.zzzyyylllty.sertraline.dep.jackson")
        .replaceUnrelocated("!com.mojang.datafixers","!io.github.zzzyyylllty.sertraline.dep.datafixers")
        .replaceUnrelocated("!io.netty.handler.codec.http", "!io.github.zzzyyylllty.sertraline.dep.http")
        .replaceUnrelocated("!io.netty.handler.codec.rtsp", "!io.github.zzzyyylllty.sertraline.dep.rtsp")
        .replaceUnrelocated("!io.netty.handler.codec.spdy", "!io.github.zzzyyylllty.sertraline.dep.spdy")
        .replaceUnrelocated("!io.netty.handler.codec.http2", "!io.github.zzzyyylllty.sertraline.dep.http2")
        .replaceUnrelocated("!org.tabooproject.fluxon","!io.github.zzzyyylllty.sertraline.dep.fluxon")
        .replaceUnrelocated("!com.github.benmanes.caffeine","!io.github.zzzyyylllty.sertraline.dep.caffeine")
        .replaceUnrelocated("!org.kotlincrypto","!io.github.zzzyyylllty.sertraline.dep.kotlincrypto")
//        .replaceUnrelocated("!com.oracle.truffle","!io.github.zzzyyylllty.sertraline.dep.truffle")
//        .replaceUnrelocated("!org.graalvm.polyglot","!io.github.zzzyyylllty.sertraline.dep.polyglot")
    return replaced
}

fun String.replaceUnrelocated(before:String, after:String): String {
    return this.replace(before.removePrefix("!"), after.removePrefix("!"))
}