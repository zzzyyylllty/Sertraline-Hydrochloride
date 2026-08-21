package io.github.zzzyyylllty.sertraline.function.fluxon.script

import io.github.zzzyyylllty.sertraline.Sertraline.fluxonInst
import io.github.zzzyyylllty.sertraline.util.minimessage.mmJsonUtil
import io.github.zzzyyylllty.sertraline.util.minimessage.mmLegacyAmpersandUtil
import io.github.zzzyyylllty.sertraline.util.minimessage.mmLegacySectionUtil
import io.github.zzzyyylllty.sertraline.util.minimessage.toMiniMessageString
import net.kyori.adventure.text.Component
import org.tabooproject.fluxon.runtime.FluxonRuntime
import org.tabooproject.fluxon.runtime.FunctionContext
import org.tabooproject.fluxon.runtime.NativeFunction.NativeCallable
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import java.util.*

@Awake(LifeCycle.ENABLE)
fun registerExtensionComponent() {
    fluxonInst?.let { ExtensionComponent.init(it) }
}


object ExtensionComponent {
    fun init(runtime: FluxonRuntime) {
        runtime.registerExtension(Component::class.java) // 获取指定索引的元素
            .function("serializeComponent", 0, NativeCallable { context: FunctionContext<Component?>? ->
                val c: Component = Objects.requireNonNull<Component>(context!!.getTarget())
                c.toMiniMessageString()
            })
            .function("serializeStrict", 0, NativeCallable { context: FunctionContext<Component?>? ->
                val c: Component = Objects.requireNonNull<Component>(context!!.getTarget())
                c.toMiniMessageString(strict = true)
            })
            .function("serializeLegacyAmpersand", 0, NativeCallable { context: FunctionContext<Component?>? ->
                val c: Component = Objects.requireNonNull<Component>(context!!.getTarget())
                mmLegacyAmpersandUtil.serialize(c)
            })
            .function("serializeLegacySection", 0, NativeCallable { context: FunctionContext<Component?>? ->
                val c: Component = Objects.requireNonNull<Component>(context!!.getTarget())
                mmLegacySectionUtil.serialize(c)
            })
            .function("serializeJson", 0, NativeCallable { context: FunctionContext<Component?>? ->
                val c: Component = Objects.requireNonNull<Component>(context!!.getTarget())
                mmJsonUtil.serialize(c)
            })
    }
}