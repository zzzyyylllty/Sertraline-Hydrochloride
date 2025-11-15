package io.github.zzzyyylllty.sertraline.function.fluxon.script

import io.github.zzzyyylllty.sertraline.Sertraline.fluxonInst
import io.github.zzzyyylllty.sertraline.util.minimessage.mmJsonUtil
import io.github.zzzyyylllty.sertraline.util.minimessage.mmLegacyAmpersandUtil
import io.github.zzzyyylllty.sertraline.util.minimessage.mmLegacySectionUtil
import io.github.zzzyyylllty.sertraline.util.minimessage.mmStrictUtil
import io.github.zzzyyylllty.sertraline.util.minimessage.mmUtil
import net.kyori.adventure.text.Component
import org.tabooproject.fluxon.runtime.FluxonRuntime
import org.tabooproject.fluxon.runtime.FunctionContext
import org.tabooproject.fluxon.runtime.NativeFunction.NativeCallable
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import org.tabooproject.fluxon.runtime.java.Export
@Awake(LifeCycle.ENABLE)
fun registerFunctionComponent() {
    FunctionComponent.init(fluxonInst)
}


object FunctionComponent {
    fun init(runtime: FluxonRuntime) {
        runtime.registerFunction(
            "component",
            0,
            NativeCallable { context: FunctionContext<Any?>? -> FluxonComponentObject.INSTANCE })
        runtime.exportRegistry.registerClass(FluxonComponentObject::class.java, "sertraline:component")
    }

    class FluxonComponentObject {

        companion object {
            val INSTANCE: FluxonComponentObject = FluxonComponentObject()
        }


        @Export
        fun serialize(arg: Component) {
            mmUtil.serialize(arg)
        }

        @Export
        fun serializeStrict(arg: Component) {
            mmStrictUtil.serialize(arg)
        }

        @Export
        fun serializeJson(arg: Component) {
            mmJsonUtil.serialize(arg)
        }

        @Export
        fun serializeLegacySection(arg: Component) {
            mmLegacySectionUtil.serialize(arg)
        }

        @Export
        fun serializeLegacyAmpersand(arg: Component) {
            mmLegacyAmpersandUtil.serialize(arg)
        }
        @Export
        fun serializeSection(arg: Component) {
            mmLegacySectionUtil.serialize(arg)
        }

        @Export
        fun serializeAmpersand(arg: Component) {
            mmLegacyAmpersandUtil.serialize(arg)
        }
        @Export
        fun deserialize(arg: String) {
            mmUtil.deserialize(arg)
        }

        @Export
        fun deserializeStrict(arg: String) {
            mmStrictUtil.deserialize(arg)
        }

        @Export
        fun deserializeJson(arg: String) {
            mmJsonUtil.deserialize(arg)
        }

        @Export
        fun deserializeLegacySection(arg: String) {
            mmLegacySectionUtil.deserialize(arg)
        }

        @Export
        fun deserializeLegacyAmpersand(arg: String) {
            mmLegacyAmpersandUtil.deserialize(arg)
        }
        @Export
        fun deserializeSection(arg: String) {
            mmLegacySectionUtil.deserialize(arg)
        }

        @Export
        fun deserializeAmpersand(arg: String) {
            mmLegacyAmpersandUtil.deserialize(arg)
        }
        @Export
        fun deserializeList(arg: List<String>) {
            mmUtil.deserialize(arg.joinToString("<br>"))
        }

        @Export
        fun deserializeStrictList(arg: List<String>) {
            mmStrictUtil.deserialize(arg.joinToString("<br>"))
        }

        @Export
        fun deserializeJsonList(arg: List<String>) {
            mmJsonUtil.deserialize(arg.joinToString("<br>"))
        }

        @Export
        fun deserializeLegacySectionList(arg: List<String>) {
            mmLegacySectionUtil.deserialize(arg.joinToString("<br>"))
        }

        @Export
        fun deserializeLegacyAmpersandList(arg: List<String>) {
            mmLegacyAmpersandUtil.deserialize(arg.joinToString("<br>"))
        }
        @Export
        fun deserializeSectionList(arg: List<String>) {
            mmLegacySectionUtil.deserialize(arg.joinToString("<br>"))
        }

        @Export
        fun deserializeAmpersandList(arg: List<String>) {
            mmLegacyAmpersandUtil.deserialize(arg.joinToString("<br>"))
        }


    }
}