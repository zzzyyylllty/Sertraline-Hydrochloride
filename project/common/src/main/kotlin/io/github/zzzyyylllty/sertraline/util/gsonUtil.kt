package io.github.zzzyyylllty.sertraline.util

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import de.tr7zw.changeme.nbtapi.NBT
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT
import org.bukkit.block.banner.Pattern
import java.lang.reflect.Modifier
import java.util.Random
import java.util.TimeZone
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive

val jsonUtils = GsonBuilder()
    .setVersion(1.0)
    .disableJdkUnsafe()
    .disableHtmlEscaping()
    .disableInnerClassSerialization()
    .setPrettyPrinting()
    .excludeFieldsWithModifiers()
    .setLenient()
    .create()

val mcClassLoader: ClassLoader by lazy {
    Class.forName("net.minecraft.server.MinecraftServer").classLoader
}

fun parseStringToMinecraftJsonElement(jsonString: String): JsonElement {
    val mcClassLoader = mcClassLoader
    val jsonParserClass = Class.forName("com.google.gson.JsonParser", true, mcClassLoader)
    val parseStringMethod = jsonParserClass.getMethod("parseString", String::class.java)
    return parseStringMethod.invoke(null, jsonString) as JsonElement
}

fun parseJsonStringWithMCGson(json: String): JsonElement {
    //val mcClassLoader = mcClassLoader
    //val jsonParserClass = Class.forName("com.google.gson.JsonParser", true, mcClassLoader)
    //val parseStringMethod = jsonParserClass.getMethod("parseString", String::class.java)
    return (jsonUtils.toJsonTree(json) as com.google.gson.JsonElement)

}