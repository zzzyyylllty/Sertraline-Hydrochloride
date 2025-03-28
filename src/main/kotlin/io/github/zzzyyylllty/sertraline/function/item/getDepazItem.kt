package io.github.zzzyyylllty.sertraline.function.item

import com.alibaba.fastjson2.to
import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.data.DepazItemInst
import io.github.zzzyyylllty.sertraline.data.DepazItems
import io.github.zzzyyylllty.sertraline.function.error.throwNPEWithMessage
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
fun ItemStack?.getDepazItemOrFail(): DepazItems {
    var id : String? = null
    this.itemTagReader {
        id = getString("SERTRALINE_ID")
    }
    val item = itemMap[id]
    if (item == null) throwNPEWithMessage("ITEM_NOT_FOUND", id.toString())
    return item!!
}
fun ItemStack?.getDepazItemNBTOrFail(): String? {
    return this?.getItemTag()?.getDeep("SERTRALINE_DATA")?.toJsonSimplified()
}
fun ItemStack?.getDepazItemInst(): DepazItemInst {
    var data : String? = "{}"
        this?.itemTagReader {
            data = getString("SERTRALINE_DATA")
        }
    val json = data
    return json.to<DepazItemInst>() ?: throw NullPointerException()
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