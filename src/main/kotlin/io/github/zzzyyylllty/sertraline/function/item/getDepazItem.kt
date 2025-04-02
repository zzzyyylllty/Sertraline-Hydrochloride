package io.github.zzzyyylllty.sertraline.function.item

import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.data.AttributeInst
import io.github.zzzyyylllty.sertraline.data.DepazItemInst
import io.github.zzzyyylllty.sertraline.data.DepazItems
import kotlinx.serialization.json.Json
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.getItemTag
import taboolib.module.nms.itemTagReader

fun ItemStack?.getDepazItem(): DepazItems? {
    var id : String? = null
    this.itemTagReader {
        id = getString("SERTRALINE_ID")
    }
    return itemMap[id]
}

fun ItemStack?.getDepazItemNBTOrFail(): String? {
    return this?.getItemTag()?.getDeep("SERTRALINE_DATA")?.toJsonSimplified()
}
fun ItemStack.getDepazItemInst(): DepazItemInst {
    var attribute : List<String> = emptyList()
    var id : String = "null"
        this.itemTagReader {
            attribute = getStringList("SERTRALINE_ATTRIBUTE")
            id = getString("SERTRALINE_ID") ?: "null"
        }
    //val array = JSON.parseArray(attribute) // <- AttributeInst::class.java
    //val atbInst = array.toList<AttributeInst>()
    //val atbInst = Klaxon().parse<List<AttributeInst>>(attribute)
    val atbInst = mutableListOf<AttributeInst>()
    val jsonUtils = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
        allowStructuredMapKeys = true
        allowSpecialFloatingPointValues = true
    }
    for (single in attribute) {
        //Klaxon()/*.converter(atbInstConverter)*/.parse<AttributeInst>(single)
        atbInst.add(jsonUtils.decodeFromString(AttributeInst.serializer(), single))
        //atbInst.add(Json.decodeFromString<AttributeInst>(single))
    }
    return DepazItemInst(
        id = id,
        item = this,
        attributes = atbInst
    )
}

fun ItemStack?.isDepazItem(): Boolean {
    var idExists = false
    this.itemTagReader {
        idExists = (getString("SERTRALINE_ID")?.isEmpty() == true)
    }
    return idExists
}

fun ItemStack?.isDepazItemInList(): Boolean {
    var id : String? = null
    this.itemTagReader {
        id = getString("SERTRALINE_ID")
    }
    return itemMap[id] != null
}