package io.github.zzzyyylllty.sertraline.util.serialize

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.math.BigDecimal

class SmartTypeAdapter : TypeAdapter<Any>() {

    override fun write(out: JsonWriter, value: Any) {
        writeValue(out, value)
    }

    private fun writeValue(out: JsonWriter, value: Any) {
        when (value) {
            is String -> out.value(value)
            is Int -> out.value(value)
            is Long -> out.value(value)
            is Double -> out.value(value)
            is Float -> out.value(value)
            is Boolean -> out.value(value)
            is BigDecimal -> out.value(value)
            is Map<*, *> -> {
                out.beginObject()
                value.forEach { (k, v) ->
                    if (k != null) {
                        out.name(k.toString())
                        writeValue(out, v!!)
                    }
                }
                out.endObject()
            }
            is List<*> -> {
                out.beginArray()
                value.forEach { item ->
                    if (item != null) {
                        writeValue(out, item)
                    } else {
                        out.nullValue()
                    }
                }
                out.endArray()
            }
            is Array<*> -> {
                out.beginArray()
                value.forEach { item ->
                    if (item != null) {
                        writeValue(out, item)
                    } else {
                        out.nullValue()
                    }
                }
                out.endArray()
            }
            else -> out.value(value.toString())
        }
    }

    override fun read(reader: JsonReader): Any? {
        return when (reader.peek()) {
            JsonToken.STRING -> parseString(reader.nextString())
            JsonToken.NUMBER -> parseNumberSmart(reader.nextString())
            JsonToken.BOOLEAN -> reader.nextBoolean()
            JsonToken.NULL -> {
                reader.nextNull()
                null
            }
            JsonToken.BEGIN_ARRAY -> parseSmartArray(reader)
            JsonToken.BEGIN_OBJECT -> parseSmartObject(reader)
            else -> reader.nextString()
        }
    }

    private fun parseString(str: String): Any {
        // Check whether it is a string representation of a boolean
        if (str.equals("true", ignoreCase = true)) return true
        if (str.equals("false", ignoreCase = true)) return false

        // Try to parse it as a number
        return try {
            parseNumberSmart(str)
        } catch (e: NumberFormatException) {
            str
        }
    }

    private fun parseNumberSmart(numberStr: String): Any {
        return try {
            // Try Int first
            if (numberStr.matches(Regex("-?\\d+"))) {
                val longValue = numberStr.toLong()
                if (longValue >= Int.MIN_VALUE && longValue <= Int.MAX_VALUE) {
                    longValue.toInt()
                } else {
                    longValue
                }
            } else {
                // Try Double
                val doubleValue = numberStr.toDouble()
                // If it's a whole number but exceeds the Long range, return BigDecimal
                if (doubleValue % 1 == 0.0 &&
                    (doubleValue < Long.MIN_VALUE || doubleValue > Long.MAX_VALUE)) {
                    BigDecimal(numberStr)
                } else {
                    doubleValue
                }
            }
        } catch (e: NumberFormatException) {
            numberStr
        }
    }

    private fun parseSmartArray(reader: JsonReader): List<Any?> {
        val list = mutableListOf<Any?>()
        reader.beginArray()
        while (reader.hasNext()) {
            list.add(read(reader))
        }
        reader.endArray()
        return list
    }

    private fun parseSmartObject(reader: JsonReader): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        reader.beginObject()
        while (reader.hasNext()) {
            val key = reader.nextName()
            val value = read(reader)
            map[key] = value
        }
        reader.endObject()
        return map
    }
}