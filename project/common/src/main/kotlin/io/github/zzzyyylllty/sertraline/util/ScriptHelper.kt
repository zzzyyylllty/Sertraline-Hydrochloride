package io.github.zzzyyylllty.sertraline.util

import taboolib.common.LifeCycle
import taboolib.common.env.RuntimeEnv
import taboolib.common.platform.Awake
import taboolib.common.platform.function.severe
import taboolib.common.platform.function.warning
import java.io.File
import javax.script.ScriptEngineManager


object ScriptHelper {

    val engineManager by lazy { ScriptEngineManager(this::class.java.classLoader) }

    @Awake(LifeCycle.INIT)
    private fun initDependency() {
        loadDependencies("graaljs")
    }

    internal fun loadDependencies(name: String) {
        RuntimeEnv.ENV_DEPENDENCY.loadFromLocalFile(
            this::class.java.classLoader.getResource("META-INF/dependencies/$name.json")
        )
    }

}