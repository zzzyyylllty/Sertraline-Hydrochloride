package io.github.zzzyyylllty.sertraline.item

import io.github.projectunified.uniitem.all.AllItemProvider
import io.github.projectunified.uniitem.api.ItemKey
import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.Sertraline.itemManager
import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.config.asListEnhanded
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.impl.getComponentsFilteredNMS
import io.github.zzzyyylllty.sertraline.item.adapter.transferBooleanToByte
import io.github.zzzyyylllty.sertraline.logger.severeS
import io.github.zzzyyylllty.sertraline.impl.getComponentsNMSFiltered
import io.github.zzzyyylllty.sertraline.impl.setComponentNMS
import io.github.zzzyyylllty.sertraline.util.ItemTagUtil.parseMapNBT
import io.github.zzzyyylllty.sertraline.util.ItemTagUtil.parseNBT
import io.github.zzzyyylllty.sertraline.util.VersionHelper
import io.github.zzzyyylllty.sertraline.util.toUpperCase
import io.papermc.paper.datacomponent.DataComponentTypes
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.module.lang.asLangText
import taboolib.module.nms.NMSItemTag.Companion.asBukkitCopy
import taboolib.module.nms.NMSItemTag.Companion.asNMSCopy
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag
import taboolib.platform.util.buildItem


fun itemSource(input: Any?,player: Player?): ItemStack {
    val str = input.toString()
    val split = str.split(":").toMutableList()
    val key = split.first()
    split.removeFirst()
    devLog("str: $str | split: $split | key: $key")
    val item = try {
        if (!str.contains(":") || str.startsWith("minecraft:")) {
            devLog("Using vanilla item")
            buildItem(XMaterial.valueOf(
                (if (split.isNotEmpty()) split[0] else if (str != "null") str else "GRASS_BLOCK").toUpperCase()
            ))
        } else {
            val provider = AllItemProvider()
            if (player != null) provider.item(ItemKey(key, split.joinToString(":")), player) else provider.item(ItemKey(key, split.joinToString(":")))
        }
    } catch (e: Exception) {
        severeS(console.asLangText("Error_External_ItemStack_Generation_Failed",str, e))
        devLog("ItemStack generation failed")
        e.printStackTrace()
        null
    }
    return item ?: run {
        devLog("Material is null, returning grass block")
        ItemStack(Material.GRASS_BLOCK)
    }
}



fun sertralineItemBuilder(template: String,player: Player?,source: ItemStack? = null,amount: Int = 1,overrideData: Map<String, Any?>? = null): ItemStack {
    val pTemplate = itemMap[template] ?: return ItemStack(Material.GRASS_BLOCK)
    val data = pTemplate.data.toMutableMap()
    overrideData?.let {
        it.forEach { it -> data[it.key] = it.value }
    }
    val template = itemSerializer(pTemplate.copy(data = data), player) ?: return ItemStack(Material.GRASS_BLOCK)
    val itemSource = source ?: itemSource(template.data["xbuilder:material"] ?: template.data["minecraft:material"], player)
    val item = itemManager.processItem(template, itemSource, player)
    item.amount = amount

    val tag = item.getItemTag()
    tag["sertraline_id"] = template.key
    return item.setItemTag(tag)
}


fun ItemStack.rebuild(player: Player?): ItemStack {

    val tag = this.clone().getItemTag(true)
    val sID = tag["sertraline_id"]?.asString() ?: return this
    val overrideData = mutableMapOf<String, Any?>()
    overrideData["sertraline:vars"] = tag["sertraline_data"]?.parseMapNBT()
    val regen = sertralineItemBuilder(sID, player,overrideData = overrideData, amount = this.amount)
    val keep = config["rebuild.keep-data"].asListEnhanded() ?: listOf("sertraline_data","sertraline_revision")
    val newTag = regen.getItemTag()
    keep.forEach {
        newTag[it] = transferBooleanToByte(tag[it]?.parseNBT())
    }
    var rewrited = regen.setItemTag(newTag)
    var rewritedNMS = asNMSCopy(rewrited)

    val keepComp = config["rebuild.keep-component"].asListEnhanded() ?: listOf()
    if (!keepComp.isEmpty()) {
        val orgComponent = asNMSCopy(this).getComponentsFilteredNMS()
        keepComp.forEach {
            orgComponent[it]?.let { value -> rewritedNMS.setComponentNMS(it, value)?.let { rewritedNMS = it } }
        }
        rewrited = asBukkitCopy(rewrited)
    }
    return rewrited
}

fun ItemStack.rebuildLore(player: Player?) {

    val tag = this.clone().getItemTag(true)
    val sID = tag["sertraline_id"]?.asString() ?: return
    val overrideData = mutableMapOf<String, Any?>()
    overrideData["sertraline:vars"] = tag["sertraline_data"]?.parseMapNBT()
    val regen = sertralineItemBuilder(sID, player,overrideData = overrideData)
    this.lore(regen.lore())
}


/**
 * This will lost about 2%(3 in 76) Component data.
 * */
fun ItemStack.rebuildUnsafe(player: Player?) {

    val tag = this.clone().getItemTag(true)
    val sID = tag["sertraline_id"]?.asString() ?: return
    val overrideData = mutableMapOf<String, Any?>()
    overrideData["sertraline:vars"] = tag["sertraline_data"]?.parseMapNBT()
    val regen = sertralineItemBuilder(sID, player,overrideData = overrideData)
    val keep = config["rebuild.keep-data"].asListEnhanded() ?: listOf("sertraline_data","sertraline_revision")
    val newTag = regen.getItemTag()
    keep.forEach {
        newTag[it] = transferBooleanToByte(tag[it]?.parseNBT())
    }
    var rewrited = regen.setItemTag(newTag)
    this.setItemMeta(rewrited.itemMeta)
}

fun ItemStack.rebuildName(player: Player?) {
    val tag = this.clone().getItemTag(true)
    val sID = tag["sertraline_id"]?.asString() ?: return
    val overrideData = mutableMapOf<String, Any?>()
    overrideData["sertraline:vars"] = tag["sertraline_data"]?.parseMapNBT()
    val regen = sertralineItemBuilder(sID, player,overrideData = overrideData)
    if (VersionHelper().isOrAbove12005()) {
        this.setData(DataComponentTypes.CUSTOM_NAME, regen.displayName())
    } else {
        val meta = this.itemMeta
        meta.displayName(regen.displayName())
        this.setItemMeta(meta)
    }
}

fun ItemStack.rebuildDisplay(player: Player?) {

    val tag = this.clone().getItemTag(true)
    val sID = tag["sertraline_id"]?.asString() ?: return
    val overrideData = mutableMapOf<String, Any?>()
    overrideData["sertraline:vars"] = tag["sertraline_data"]?.parseMapNBT()
    val regen = sertralineItemBuilder(sID, player,overrideData = overrideData)
    this.lore(regen.lore())

}