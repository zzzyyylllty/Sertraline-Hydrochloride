package io.github.zzzyyylllty.sertraline.function.sertralize

import com.alibaba.fastjson2.toJSONString
import com.beust.klaxon.Converter
import com.beust.klaxon.JsonValue
import com.beust.klaxon.Klaxon
import io.github.zzzyyylllty.sertraline.data.AttributeInst
import io.github.zzzyyylllty.sertraline.data.AttributeSources

val atbInstConverter = object: Converter {
    override fun canConvert(cls: Class<*>)
        = cls == AttributeInst::class.java

    override fun toJson(value: Any): String
        = value.toJSONString()

    override fun fromJson(jv: JsonValue)
        = AttributeInst(
        type = AttributeSources.valueOf(jv.objString("type")),
        attr = Klaxon().parse<LinkedHashMap<String, String>>(jv.objString("attr")) ?: LinkedHashMap<String, String>(),
        definer = jv.objString("definer"),
        uuid = jv.objString("uuid"),
        source = jv.objString("source"),
        mythicLibEquipSlot = jv.objString("mythicLibEquipSlot"),
        requireSlot = Klaxon().parse<ArrayList<String>>(jv.objString("requireSlot")) ?: arrayListOf()
    )

}