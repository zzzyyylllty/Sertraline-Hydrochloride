package io.github.zzzyyylllty.sertraline.function.sertralize

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.util.TimeZone
import java.util.regex.Pattern

class TimeZoneTypeAdapter : JsonSerializer<TimeZone>, JsonDeserializer<TimeZone> {
    override fun serialize(
        src: TimeZone?,
        typeOfSrc: java.lang.reflect.Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src?.id)
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: java.lang.reflect.Type?,
        context: JsonDeserializationContext?
    ): TimeZone? {
        return json?.asString?.let { TimeZone.getTimeZone(it) }
    }
}

class PatternTypeAdapter : JsonSerializer<Pattern>, JsonDeserializer<Pattern> {
    override fun serialize(src: Pattern?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src?.pattern())  // 保存正则表达式字符串
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Pattern {
        return Pattern.compile(json?.asString ?: "")  // 从字符串中编译出Pattern
    }
}