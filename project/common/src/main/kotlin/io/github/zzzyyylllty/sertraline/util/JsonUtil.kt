package io.github.zzzyyylllty.sertraline.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.reflect.TypeToken
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.util.serialize.typeadapter.AnyValueTypeAdapter
import io.github.zzzyyylllty.sertraline.util.serialize.typeadapter.ArrayListAnyTypeAdapter
import io.github.zzzyyylllty.sertraline.util.serialize.typeadapter.LinkedHashMapAnyTypeAdapter
import kotlin.math.round

// 定义 TypeToken
val linkedHashMapStringType = object : TypeToken<LinkedHashMap<String, Any?>>() {}.type
val arrayListAnyType = object : TypeToken<ArrayList<Any?>>() {}.type

// 构建 Gson 实例的步骤
val jsonUtils: Gson by lazy {
    val builder = GsonBuilder()
        .setVersion(1.0)
        .disableHtmlEscaping()
        .disableInnerClassSerialization()
        // .setPrettyPrinting() // 强烈建议禁用，影响性能最大
        .excludeFieldsWithModifiers()
        .setLenient() // 根据你的需求决定是否保留宽松模式

    // 先创建一个基础的 Gson 实例，用于 AnyValueTypeAdapter 内部可能需要调用的地方
    // 但在这个新设计中，AnyValueTypeAdapter 的递归是直接通过自身方法调用的，
    // 所以这里直接传入一个“辅助”gson实例即可
    val tempGson = builder.create()

    // 实例化我们的 AnyValueTypeAdapter
    val anyValueAdapter = AnyValueTypeAdapter(tempGson)

    // 注册针对特定泛型类型的 TypeAdapter
    builder.registerTypeAdapter(linkedHashMapStringType, LinkedHashMapAnyTypeAdapter(anyValueAdapter))
    builder.registerTypeAdapter(arrayListAnyType, ArrayListAnyTypeAdapter(anyValueAdapter))
    // 对于 List<Any?> 的情况，ModernSItem 中并没有直接声明 List<Any?>，
    // 而是作为 Any? 的值出现。所以我们主要需要保证 Map<String, Any?> 的 Any? 能被处理。
    // 如果你在 ModernSItem 外部直接有一个 List<Any?> 字段，就需要注册它的 TypeAdapter。
    // 然而，对于 Map/List 内部的值是 Any? 的情况，AnyValueTypeAdapter 的递归处理已经足够了。
    // 因为 LinkedHashMapAnyTypeAdapter 和 ArrayListAnyTypeAdapter 内部在处理值时，
    // 都会调用 anyValueAdapter.write(out, value) 或 anyValueAdapter.read(reader)。

    builder.create()
}

fun unwrapJson(value: Any?): Any? {
    when (value) {
        is JsonObject -> {
            val map = mutableMapOf<String, Any?>()
            value.entrySet().forEach { (key, element) ->
                map[key] = unwrapJson(element)
            }
            return map
        }
        is JsonArray -> {
            val list = mutableListOf<Any?>()
            value.forEach { element ->
                list.add(unwrapJson(element))
            }
            return list
        }
        is JsonPrimitive -> {
            when {
                value.isBoolean -> return value.asBoolean
                value.isNumber -> {
                    val numberString = value.asString
                    try {
                        val asDouble = numberString.toDouble()

                        // Special handling for Byte (only 0 and 1)
                        if (asDouble == 0.0 || asDouble == 1.0) {
                            return asDouble.toInt().toByte()
                        }
                        // Try to convert to Long
                        try {
                            if (asDouble == round(asDouble)) { // Ensure Double value is equal to Long value (no decimal part)
                                val asLong = asDouble.toLong()

                                // Try to convert to Int
                                if (asLong in Int.MIN_VALUE..Int.MAX_VALUE) {
                                    val asInt = asLong.toInt()

                                    // Try to convert to Short
                                    if (asInt in Short.MIN_VALUE..Short.MAX_VALUE) {
                                        val asShort = asInt.toShort()
                                        return asShort
                                    }

                                    return asInt
                                } else {
                                    return asLong
                                }
                            }
                        } catch (e: Exception) {
                            // Ignore, it's not a Long
                        }


                        // Try to convert to Float (check for precision loss)
                        try {
                            val asFloat = value.asFloat
                            devLog("float: $asFloat, double: $asDouble")
                            if (asFloat.toString() == asDouble.toString()) {
                                return asFloat
                            }
                        } catch (_: Exception) {
                        }

                        return asDouble // If none of the above conversions are satisfied, return Double
                    } catch (e: NumberFormatException) {
                        return numberString // If it cannot be parsed into Double, return String
                    }
                }
                value.isString -> return value.asString
                else -> return value.toString() // Handle other primitive types if needed
            }
        }
        is JsonElement -> return value.toString() // Fallback for other JsonElement types
        null -> return null
        else -> return value // Return the original value if it's not a Json type
    }
}