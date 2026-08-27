package io.github.zzzyyylllty.sertraline.util.serialize.typeadapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken // import TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException

// This is the internal handling logic for Any?, now wrapped in a generic ValueAdapter.
// This ValueAdapter is not registered directly to Any.class; instead it is invoked by the Map/List TypeAdapters.
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
                // Re-obtain the List<Any?> adapter via TypeToken
                // Note that we need the List adapter, not the plain Any one, because Any cannot be registered directly.
                // To avoid infinite recursion, be careful: we want to delegate to Gson's internal List handling,
                // but with the List elements also processed by our AnyValueTypeAdapter.
                // Actually, it's simpler to just handle it recursively right here.
                for (item in value) {
                    this.write(out, item) // recursively call ourselves to process list elements
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
                    this.write(out, v) // recursively call ourselves to process Map values
                }
                out.endObject()
            }
            else -> {
                // For ModernSItem's internal data and config fields,
                // if the Any? value is not a primitive, List, or Map,
                // we still need a fallback mechanism.
                // The handling here determines what happens when an unknown type appears:
                // 1. Throw an exception (strictest):
                throw IllegalArgumentException("Unsupported type for AnyValueTypeAdapter: ${value::class.java.name}. Expected primitive, List, or Map.")
                // 2. Delegate to Gson's default serializer (may introduce reflection):
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
                    list.add(this.read(reader)) // recursively call ourselves to process list elements
                }
                reader.endArray()
                list
            }
            JsonToken.BEGIN_OBJECT -> {
                val map = LinkedHashMap<String, Any?>()
                reader.beginObject()
                while (reader.hasNext()) {
                    val key = reader.nextName()
                    map[key] = this.read(reader) // recursively call ourselves to process Map values
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

// TypeAdapter for LinkedHashMap<String, Any?>
// This adapter uses AnyValueTypeAdapter to process its internal Any? values
class LinkedHashMapAnyTypeAdapter(private val anyValueAdapter: AnyValueTypeAdapter) : TypeAdapter<LinkedHashMap<String, Any?>>() {
    override fun write(out: JsonWriter, map: LinkedHashMap<String, Any?>?) {
        if (map == null) {
            out.nullValue()
            return
        }
        out.beginObject()
        for ((key, value) in map) {
            out.name(key)
            anyValueAdapter.write(out, value) // process the value with AnyValueTypeAdapter
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
            map[key] = anyValueAdapter.read(reader) // process AnyValue with AnyValueTypeAdapter
        }
        reader.endObject()
        return map
    }
}

// TypeAdapter for ArrayList<Any?> (or other List<Any?>)
// This adapter uses AnyValueTypeAdapter to process its internal Any? elements
class ArrayListAnyTypeAdapter(private val anyValueAdapter: AnyValueTypeAdapter) : TypeAdapter<ArrayList<Any?>>() {
    override fun write(out: JsonWriter, list: ArrayList<Any?>?) {
        if (list == null) {
            out.nullValue()
            return
        }
        out.beginArray()
        for (item in list) {
            anyValueAdapter.write(out, item) // process AnyValue with AnyValueTypeAdapter
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
            list.add(anyValueAdapter.read(reader)) // process Any element with AnyValueTypeAdapter
        }
        reader.endArray()
        return list
    }
}