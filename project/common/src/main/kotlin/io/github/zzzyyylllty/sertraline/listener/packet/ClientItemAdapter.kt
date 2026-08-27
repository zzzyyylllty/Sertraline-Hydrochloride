package io.github.zzzyyylllty.sertraline.listener.packet

import io.github.zzzyyylllty.sertraline.Sertraline.config
import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.compat.PlatformCompat
import io.github.zzzyyylllty.sertraline.config.asListEnhanced
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.debugMode.devLog
import io.github.zzzyyylllty.sertraline.impl.removeComponentNMS
import io.github.zzzyyylllty.sertraline.item.itemSerializer
import io.github.zzzyyylllty.sertraline.impl.setComponent
import io.github.zzzyyylllty.sertraline.impl.setComponentNMS
import io.github.zzzyyylllty.sertraline.util.VersionHelper
import io.github.zzzyyylllty.sertraline.util.parseNamespacedKey
import io.github.zzzyyylllty.sertraline.util.loreformat.handleLoreFormat
import io.github.zzzyyylllty.sertraline.util.minimessage.toComponent
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import com.cryptomorin.xseries.XMaterial
import taboolib.module.lang.asLangText
import taboolib.module.nms.NMSItemTag.Companion.asBukkitCopy
import taboolib.module.nms.NMSItemTag.Companion.asNMSCopy
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag
import taboolib.platform.util.deserializeToItemStack
import taboolib.platform.util.serializeToByteArray
import io.github.zzzyyylllty.sertraline.util.toUpperCase
import org.bukkit.NamespacedKey
import kotlin.math.roundToInt

private val carriedItemFieldInContainerClick by lazy { if (VersionHelper().isUniversal) "carriedItem" else "item" }
private val packetLore by lazy { config.getBoolean("packet.packet-lore", true) }
private val packetComponent by lazy { config.getBoolean("packet.packet-component", true) }
val suffix by lazy { console.asLangText("Editor_Item_Suffix").asListEnhanced()?.toComponent() ?: emptyList() }

/**
 * Gets the SERTRALINE_OITEM Tag from the incoming virtual item's Tag to restore the original item
 * */
fun ItemStack.c2s(): ItemStack {
    val tag = this.getItemTag()
    val deserialized = (tag["sertraline_oitem"]?.asByteArray())?.deserializeToItemStack()
        ?: run {
            return this
        }
    return deserialized
}
/**
 * Converts a server-side item into a virtual item
 * */
fun ItemStack.s2c(player: Player?): ItemStack {
    val oItem = this.clone()
    if (type == Material.AIR) return oItem
    val tag = this.getItemTag(true)
    val id = tag["sertraline_id"]?.asString() ?: return oItem
    val sItem = itemSerializer(id, player) ?: return oItem
    if (packetLore) handleLoreFormat(sItem, player, PlatformCompat.getLore(this), true, this)?.let {
        PlatformCompat.setLore(this, it)
    }
    val nmsItem = asNMSCopy(this)
    val modifiedItem = visualComponentSetterNMS(nmsItem, sItem, oItem.serializeToByteArray())
    if (tag["sertraline_browse_item"] != null && PlatformCompat.getLore(oItem) != PlatformCompat.getLore(modifiedItem)) { // if it is a browse item and its lore was modified
        val lore = PlatformCompat.getLore(modifiedItem)?.toMutableList() ?: mutableListOf()
        lore.addAll(suffix)
        PlatformCompat.setLore(modifiedItem, lore)
    }
    return modifiedItem
}
fun visualComponentSetterNMS(item: Any, sItem: ModernSItem,serialized: ByteArray): ItemStack {

    // visual overrides are written via DataComponent, older versions (1.12.2 etc.) return the original item directly
    if (!VersionHelper().isOrAbove12005()) return asBukkitCopy(item)
    var resultItem = item
    var visualMaterial: Material? = null
    val autoComponents = LinkedHashMap<String, Any>()

    if (packetComponent) {
        val filtered = (sItem.data["visual"] as? Map<*,*>)?.toMutableMap() ?: return asBukkitCopy(item)
        if (!filtered.isEmpty()) {

            filtered["material"]?.let {
                visualMaterial = XMaterial.valueOf(it.toString().toUpperCase()).get() ?: Material.STONE
                filtered.remove("material")
            }

            filtered.remove("auto_lore")?.let { autoComponents["autoLore"] = it }
            filtered.remove("auto_name")?.let { autoComponents["autoName"] = it }
            filtered.remove("auto_custom_model_data")?.let { autoComponents["autoCMD"] = it }
            filtered.remove("auto_model")?.let { autoComponents["autoModel"] = it }

            filtered.forEach { (key, value) ->
                if (value != null) resultItem.setComponentNMS("minecraft:$key", value)?.let { resultItem = it }
                else resultItem = resultItem.removeComponentNMS("minecraft:$key")
            }


        }
    }

    var resultBItem = asBukkitCopy(resultItem)
    devLog("autoComponents: $autoComponents")
    if (autoComponents.isNotEmpty()) {
        resultBItem.itemMeta?.let { meta ->
            autoComponents.forEach { (key, value) ->
                when (key) {
                    "autoName" -> PlatformCompat.setDisplayName(meta, value.toString().toComponent())
                    "autoLore" -> PlatformCompat.setLore(meta, (value.asListEnhanced())?.toComponent() ?: listOf(value.toString().toComponent()))
                    "autoCMD" -> PlatformCompat.setCustomModelData(meta, value.toString().toDouble().roundToInt())
                    "autoModel" -> PlatformCompat.setItemModel(meta, value.toString().parseNamespacedKey())
                }
                resultBItem.setItemMeta(meta)
            }
        }
    }
    val tag = resultBItem.getItemTag()
    tag["sertraline_oitem"] = serialized
    resultBItem = resultBItem.setItemTag(tag)
    visualMaterial?.let { resultBItem.type = it }
    return resultBItem
}

@Deprecated("Performance Issue")
fun visualComponentSetter(item: ItemStack, sItem: ModernSItem): ItemStack {
    val filtered = (sItem.data["visual"] as? Map<*,*>)?.toMutableMap() ?: return item
    if (filtered.isEmpty()) return item

    var resultItem = item
    filtered["material"]?.let {
        resultItem.type = XMaterial.valueOf(it.toString()).get() ?: Material.STONE
        filtered.remove("material")
    }

    if (!filtered.isEmpty()) {
        var nmsItem = asNMSCopy(resultItem)
        filtered.forEach { (key, value) ->
            nmsItem.setComponentNMS("minecraft:$key", value!!)?.let { nmsItem = it }
        }
        resultItem = asBukkitCopy(nmsItem)
    }

    return resultItem
}