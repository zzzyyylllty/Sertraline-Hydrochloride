package io.github.zzzyyylllty.sertraline.util.serialize.typeadapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken // 引入 TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException

// 针对 Any? 的内部处理逻辑，现在我们将它封装在一个通用的 ValueAdapter 中
// 这个 ValueAdapter 不直接注册给 Any.class，而是由 Map/List 的 TypeAdapter 调用
class AnyValueTypeAdapter(private val gson: Gson) : TypeAdapter<Any?>() {

    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: Any?) {
        if (value == null) {
            out.nullValue()
            return
        }

        when (value) {
            is String -> out.value(value)
            is Number -> out.value(value)
            is Boolean -> out.value(value)
            is List<*> -> {
                out.beginArray()
                // 使用 TypeToken 重新获取 List<Any?> 的适配器
                // 注意这里需要获取 List 的适配器，而不是直接 Any 的，因为 Any 无法直接注册
                // 为了避免无限循环，这里需要特别小心，我们希望委托给 Gson 内部的 List 处理，
                // 只是List的元素也使用我们提供的AnyValueTypeAdapter
                // 实际上，更简单的做法是直接在这里进行递归处理
                for (item in value) {
                    this.write(out, item) // 递归调用自身处理列表元素
                }
                out.endArray()
            }
            is Map<*, *> -> {
                out.beginObject()
                for ((k, v) in value) {
                    if (k !is String) {
                        throw IllegalArgumentException("Map key must be a string for JSON serialization: $k")
                    }
                    out.name(k)
                    this.write(out, v) // 递归调用自身处理 Map 的值
                }
                out.endObject()
            }
            else -> {
                // 对于 ModernSItem 内部的 data 和 config 字段，
                // 如果 Any? 值不是基本类型、List 或 Map，
                // 我们仍然需要一个 fallback 机制。
                // 这里的处理方式决定了如果出现未知类型时的行为：
                // 1. 抛出异常 (最严格):
                throw IllegalArgumentException("Unsupported type for AnyValueTypeAdapter: ${value::class.java.name}. Expected primitive, List, or Map.")
                // 2. 委托给 Gson 的默认序列化器 (可能引入反射):
                // gson.toJson(value, value.javaClass, out)
            }
        }
    }

    @Throws(IOException::class)
    override fun read(reader: JsonReader): Any? {
        return when (reader.peek()) {
            JsonToken.BEGIN_ARRAY -> {
                val list = ArrayList<Any?>()
                reader.beginArray()
                while (reader.hasNext()) {
                    list.add(this.read(reader)) // 递归调用自身处理列表元素
                }
                reader.endArray()
                list
            }
            JsonToken.BEGIN_OBJECT -> {
                val map = LinkedHashMap<String, Any?>()
                reader.beginObject()
                while (reader.hasNext()) {
                    val key = reader.nextName()
                    map[key] = this.read(reader) // 递归调用自身处理 Map 的值
                }
                reader.endObject()
                map
            }
            JsonToken.STRING -> reader.nextString()
            JsonToken.NUMBER -> {
                val numberString = reader.nextString()
                try {
                    if (numberString.contains(".") || numberString.contains("e", ignoreCase = true)) {
                        numberString.toDouble()
                    } else {
                        numberString.toLong()
                    }
                } catch (e: NumberFormatException) {
                    throw JsonSyntaxException("Cannot parse number: $numberString", e)
                }
            }
            JsonToken.BOOLEAN -> reader.nextBoolean()
            JsonToken.NULL -> {
                reader.nextNull()
                null
            }
            JsonToken.END_DOCUMENT -> null
            else -> throw IllegalStateException("Unexpected token: ${reader.peek()}")
        }
    }
}

// 针对 LinkedHashMap<String, Any?> 的 TypeAdapter
// 这个适配器将使用 AnyValueTypeAdapter 来处理其内部的 Any? 值
class LinkedHashMapAnyTypeAdapter(private val anyValueAdapter: AnyValueTypeAdapter) : TypeAdapter<LinkedHashMap<String, Any?>>() {
    override fun write(out: JsonWriter, map: LinkedHashMap<String, Any?>?) {
        if (map == null) {
            out.nullValue()
            return
        }
        out.beginObject()
        for ((key, value) in map) {
            out.name(key)
            anyValueAdapter.write(out, value) // 使用 AnyValueTypeAdapter 处理值
        }
        out.endObject()
    }

    override fun read(reader: JsonReader): LinkedHashMap<String, Any?>? {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            return null
        }
        val map = LinkedHashMap<String, Any?>()
        reader.beginObject()
        while (reader.hasNext()) {
            val key = reader.nextName()
            map[key] = anyValueAdapter.read(reader) // 使用 AnyValueTypeAdapter 处理值
        }
        reader.endObject()
        return map
    }
}

// 针对 ArrayList<Any?> (或其他 List<Any?>) 的 TypeAdapter
// 这个适配器将使用 AnyValueTypeAdapter 来处理其内部的 Any? 元素
class ArrayListAnyTypeAdapter(private val anyValueAdapter: AnyValueTypeAdapter) : TypeAdapter<ArrayList<Any?>>() {
    override fun write(out: JsonWriter, list: ArrayList<Any?>?) {
        if (list == null) {
            out.nullValue()
            return
        }
        out.beginArray()
        for (item in list) {
            anyValueAdapter.write(out, item) // 使用 AnyValueTypeAdapter 处理元素
        }
        out.endArray()
    }

    override fun read(reader: JsonReader): ArrayList<Any?>? {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            return null
        }
        val list = ArrayList<Any?>()
        reader.beginArray()
        while (reader.hasNext()) {
            list.add(anyValueAdapter.read(reader)) // 使用 AnyValueTypeAdapter 处理元素
        }
        reader.endArray()
        return list
    }
}