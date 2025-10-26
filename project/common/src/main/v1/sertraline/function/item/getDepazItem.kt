package io.github.zzzyyylllty.sertraline.function.item

import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.data.AttributeInst
import io.github.zzzyyylllty.sertraline.data.DepazItemInst
import io.github.zzzyyylllty.sertraline.data.DepazItems
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.function.sertralize.AnySerializer

import kotlinx.serialization.json.Json
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.getItemTag
import taboolib.module.nms.itemTagReader

val jsonMain = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
    coerceInputValues = true
    encodeDefaults = true
    allowStructuredMapKeys = true
    allowSpecialFloatingPointValues = true
}

fun ItemStack?.getDepazItem(): DepazItems? {
    var id : String? = null
    this.itemTagReader {
        id = getString("SERTRALINE_ID")
    }
    return itemMap[id]
}

fun DepazItemInst?.getDepazItem(): DepazItems? {
    var id : String? = this?.id
    return itemMap[id]
}

fun ItemStack?.getDepazData(): LinkedHashMap<String, Any> {
    var data = linkedMapOf<String, Any>()

    val jsonUtils = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
        allowStructuredMapKeys = true
        allowSpecialFloatingPointValues = true
    }

    val tag = this?.getItemTag()
        //data = jsonUtils.decodeFromString<LinkedHashMap<String, Any>>(getString("SERTRALINE_DATA") ?: "{}")
    data = (jsonMain.decodeFromString<LinkedHashMap<String, String>>(tag?.getDeep("SERTRALINE_DATA")?.toJsonSimplified() ?: "{}") as LinkedHashMap<String, Any>)

    return data
}

fun ItemStack?.getDepazItemNBTOrFail(): String? {
    return this?.getItemTag()?.getDeep("SERTRALINE_DATA")?.toJsonSimplified()
}

fun ItemStack.getDepazItemInst(): DepazItemInst? {
    var attribute : String = "[]"
    var id : String? = null
    var data = LinkedHashMap<String, Any>()
    this.itemTagReader {
        id = getString("SERTRALINE_ID")
    }
    if (itemMap[id] == null || id == null) return null

    data = this.getDepazData()
    val atbInst = this.getAttribute()

    return DepazItemInst(
        id = id!!,
        item = this,
        attributes = atbInst,
        data = data
    )
}

fun ItemStack.getAttribute(): MutableList<AttributeInst> {
//    1.0.0
//    this.itemTagReader {
//        attribute = getString("SERTRALINE_ATTRIBUTE") ?: "{}"
//    }

    var atbInst: MutableList<AttributeInst> = mutableListOf()
    // 1.0.1 Start
    val itemTag = this.getItemTag()
    itemTag.getDeep("SERTRALINE_ATTRIBUTE")?.asList()?.forEach {
        devLog("getted attribute $it")
        //val map: String = it.asCompound().toJson()
        //devLog("getted Jsonmap $map")  // 输出简洁的 JSON

        val json = jsonMain.decodeFromString<AttributeInst>(it.toString())
        //val json = JSON.parseObject<AttributeInst>(map, object : TypeReference<AttributeInst>() {})
        atbInst.add(json)
        devLog("getted Instance $json")
    }

    // 1.0.1 End

    // val atbInst = Json.decodeFromString(attribute) as MutableList<AttributeInst>

    return atbInst
}

fun ItemStack?.isDepazItem(): Boolean {
    var idExists = false
    this.itemTagReader {
        idExists = (getString("SERTRALINE_ID")?.isEmpty() == true)
    }
    return idExists
}

fun ItemStack?.getDepazId(): String? {
    var id : String? = null
    this.itemTagReader {
        id = getString("SERTRALINE_ID")
    }
    return id
}

fun ItemStack?.isDepazItemInList(): Boolean {
    var id : String? = null
    this.itemTagReader {
        id = getString("SERTRALINE_ID")
    }
    return itemMap[id] != null
}