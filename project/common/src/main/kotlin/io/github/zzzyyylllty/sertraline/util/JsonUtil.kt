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

val linkedHashMapStringType = object : TypeToken<LinkedHashMap<String, Any?>>() {}.type
val arrayListAnyType = object : TypeToken<ArrayList<Any?>>() {}.type

val jsonUtils: Gson by lazy {
    val builder = GsonBuilder()
        .setVersion(1.0)
        .disableHtmlEscaping()
        .disableInnerClassSerialization()
        // .setPrettyPrinting()
        .excludeFieldsWithModifiers()
        .setLenient()

    val tempGson = builder.create()

    // 实例化
    val anyValueAdapter = AnyValueTypeAdapter(tempGson)

    // 注册
    builder.registerTypeAdapter(linkedHashMapStringType, LinkedHashMapAnyTypeAdapter(anyValueAdapter))
    builder.registerTypeAdapter(arrayListAnyType, ArrayListAnyTypeAdapter(anyValueAdapter))

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

                        if (asDouble == 0.0 || asDouble == 1.0) {
                            return asDouble.toInt().toByte()
                        }
                        // Long
                        try {
                            if (asDouble == round(asDouble)) {
                                val asLong = asDouble.toLong()

                                // Int
                                if (asLong in Int.MIN_VALUE..Int.MAX_VALUE) {
                                    val asInt = asLong.toInt()

                                    // Short
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

                        return asDouble
                    } catch (e: NumberFormatException) {
                        return numberString
                    }
                }
                value.isString -> return value.asString
                else -> return value.toString()
            }
        }
        is JsonElement -> return value.toString()
        null -> return null
        else -> return value
    }
}