package io.github.zzzyyylllty.sertraline.function.item

import com.alibaba.fastjson2.JSON
import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.data.AttributeInst
import io.github.zzzyyylllty.sertraline.data.DepazItemInst
import io.github.zzzyyylllty.sertraline.data.DepazItems
import io.github.zzzyyylllty.sertraline.function.kether.evalKetherValue
import io.github.zzzyyylllty.sertraline.function.sertralize.AnySerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import org.bukkit.command.CommandSender
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

fun ItemStack?.getData(): LinkedHashMap<String, Any> {
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

    this.itemTagReader {
        //data = jsonUtils.decodeFromString<LinkedHashMap<String, @Serializable(AnySerializer::class) Any>>(getString("SERTRALINE_DATA") ?: "{}")
        data = JSON.parseObject(getString("SERTRALINE_DATA") ?: "{}") as LinkedHashMap<String, Any>
    }
    return data
}

fun ItemStack?.getDepazItemNBTOrFail(): String? {
    return this?.getItemTag()?.getDeep("SERTRALINE_DATA")?.toJsonSimplified()
}

fun ItemStack.getDepazItemInst(): DepazItemInst {
    var attribute : String = "[]"
    var id : String = "null"
    var data = LinkedHashMap<String, Any>()
    val jsonUtils = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
        allowStructuredMapKeys = true
        allowSpecialFloatingPointValues = true
    }
        this.itemTagReader {
            attribute = getString("SERTRALINE_ATTRIBUTE") ?: "[]"
            id = getString("SERTRALINE_ID") ?: "null"
            data = JSON.parse(getString("SERTRALINE_DATA") ?:"{}") as LinkedHashMap<String, Any>
        }
    val atbInst = Json.decodeFromString(attribute) as MutableList<AttributeInst>

    return DepazItemInst(
        id = id,
        item = this,
        attributes = atbInst,
        data = data
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