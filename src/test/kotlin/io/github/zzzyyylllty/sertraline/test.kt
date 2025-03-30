package io.github.zzzyyylllty.sertraline

import com.beust.klaxon.Klaxon
import io.github.zzzyyylllty.sertraline.data.AttributeSources
import kotlin.test.Test

class test{
    data class SimpleAttributeInst(
        val type: SimpleAttributeSources = SimpleAttributeSources.MYTHIC_LIB,
        val attr: LinkedHashMap<String, String>,
        val definer: String,
        val uuid: String?,
        val source: String,
        val mythicLibEquipSlot: String,
        val requireSlot: ArrayList<String>
    )


    enum class SimpleAttributeSources {
        MYTHIC_LIB,
        //ATTRIBUTE_PLUS,
        //MYTHIC_MOBS,
        ///SX_ATTRIBUTE_2,
        //SX_ATTRIBUTE_3
    }
    @Test
    fun test() {
        val json = """{"attr":{"MAX_HEALTH":"5"},"definer":"sertraline","mythicLibEquipSlot":"MAIN_HAND","requireSlot":["36","37","38","39","ANY_HAND"],"source":"MELEE_WEAPON","type":"MYTHIC_LIB"}"""
        //val result = Klaxon().parse<SimpleAttributeInst>(json) // 测试基础解析
        //print(result)
    }
}