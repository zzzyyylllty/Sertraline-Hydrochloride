package io.github.zzzyyylllty.sertraline.reflect

import io.github.zzzyyylllty.sertraline.logger.fineS
import io.github.zzzyyylllty.sertraline.logger.severeS
import taboolib.library.reflex.Reflex.Companion.getProperty

val className = Class.forName("net.minecraft.resources.ResourceLocation")
fun getBuiltInRegistries(): Any? {
    try {
        val builtInRegistriesClass = Class.forName("net.minecraft.core.registries.BuiltInRegistries")
        val component = builtInRegistriesClass.getProperty<Any?>("DATA_COMPONENT_TYPE", isStatic = true)
        fineS("COMP: $component")
        return component
    } catch (e: Exception) {
        severeS("BuiltInRegistries Reflect failed.")
        e.printStackTrace()
        return null
    }
}

fun getFromNamespaceAndPathMethod(): Any? {
    return try {
        val resourceLocationClass = className
        // fromNamespaceAndPath(String, String)
        val method = resourceLocationClass.getMethod("fromNamespaceAndPath", String::class.java, String::class.java)
        method
    } catch (e: Exception) {
        severeS("ResourceLocation#fromNamespaceAndPath reflect getMethod failed.")
        e.printStackTrace()
        null
    }
}

fun getGetValueMethod(): java.lang.reflect.Method? {
    return try {
        val registryClass = Class.forName("net.minecraft.core.Registry")
        val method = registryClass.getMethod("getValue", Any::class.java, className)
        method
    } catch (e: Exception) {
        severeS("Registry#getValue reflect getMethod failed.")
        e.printStackTrace()
        null
    }
}


fun getItemStackComponent(): java.lang.reflect.Method? {
    return try {
        val registryClass = Class.forName("net.minecraft.core.Registry")
        val method = registryClass.getMethod("getValue", Any::class.java, className)
        method
    } catch (e: Exception) {
        severeS("Registry#getValue reflect getMethod failed.")
        e.printStackTrace()
        null
    }
}

