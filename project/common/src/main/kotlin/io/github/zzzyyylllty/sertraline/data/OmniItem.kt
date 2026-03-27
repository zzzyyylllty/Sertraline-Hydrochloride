package io.github.zzzyyylllty.sertraline.data

import io.github.zzzyyylllty.embiancomponent.EmbianComponent
import io.github.zzzyyylllty.sertraline.config.asListEnhanced
import io.github.zzzyyylllty.sertraline.logger.severeL
import io.github.zzzyyylllty.sertraline.logger.warningL
import io.github.zzzyyylllty.sertraline.util.ExternalItemHelper
import io.github.zzzyyylllty.sertraline.util.VersionHelper
import io.github.zzzyyylllty.sertraline.util.minimessage.toComponent
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XItemStack
import taboolib.module.nms.NMSItemTag.Companion.asNMSCopy
import kotlin.math.roundToInt

private val specialItemNamespace = listOf("minecraft", "mc", "vanilla")
val componentHelper by lazy { if (VersionHelper().isOrAbove12005()) EmbianComponent.SafetyComponentSetter else null }

data class OmniItem(
    val source: String,
    val item: String,
    val parameters: LinkedHashMap<String, Any?>? = null,
    val components: LinkedHashMap<String, Any?>? = null,
    val amount: String? = "1",
) {
    fun build(player: Player?, overrideAmount: Int? = null): ItemStack {

        val amount = overrideAmount ?: (amount ?: "1").toDoubleOrNull()?.roundToInt()

//        val split = (if (namespaceID.contains("{")) namespaceID.parseKether(player, defaultData).split(":") else listOf("mc", "grass_block")).toMutableList()
//        val source = if (split.size >= 2) split.first().lowercase() else "mc"
//        split.removeFirst()
//        val item = split.joinToString(":")
        var itemStack: ItemStack?

        try {
            val providedItem = if (specialItemNamespace.contains(source)) {

                when (source) {
                    "mc", "minecraft", "vanilla" -> {
                        val parameters = (parameters ?: mapOf<String, Any?>()).toMutableMap()
                        parameters["material"] = item
                        XItemStack.deserialize(parameters)
                    }

                    else -> null
                }

            } else {
                if (player != null) {
                    ExternalItemHelper.itemBridge?.build(source, player, item)?.get()
                } else {
                    ExternalItemHelper.itemBridge?.build(source, item)?.get()
                }
            }
            if (providedItem == null) {
                severeL("ErrorItemGenerationFailedNull", source, item)
                return ItemStack(Material.GRASS_BLOCK)
            }
            itemStack = providedItem

        } catch (e: Exception) {
            severeL("ErrorItemGenerationFailed", source, item)
            e.printStackTrace()
            return ItemStack(Material.GRASS_BLOCK)
        }

        if (parameters?.isNotEmpty() ?: false) {

            val meta = itemStack.itemMeta
            parameters["name"]?.toString()?.toComponent()?.let { meta.displayName(it) }
            parameters["display-name"]?.toString()?.toComponent()?.let { meta.displayName(it) }
            parameters["custom-name"]?.toString()?.toComponent()?.let { meta.customName(it) }
            parameters["item-name"]?.toString()?.toComponent()?.let { meta.itemName(it) }
            (parameters["item-model"] ?: parameters["model"])?.toString()?.let { meta.itemModel = NamespacedKey.fromString(it) }
            itemStack.setItemMeta(meta)
            parameters["lore"].asListEnhanced()?.toComponent()?.let { itemStack.lore(it) }

        }
        if (components != null && components.isNotEmpty()) {
            if (VersionHelper().isOrAbove12005()) {
                var nmsStack = asNMSCopy(itemStack)
                components.forEach {
                    val value = it.value
                    if (value != null) EmbianComponent.SafetyComponentSetter.setComponentNMS(nmsStack, it.key, value)?.let { nmsStack = it }
                    else EmbianComponent.SafetyComponentSetter.removeComponentNMS(nmsStack, it.key).let { nmsStack = it }
                }
            } else {
                warningL("WarningNotSupportDataComponent")
            }
        }
        itemStack.amount = amount ?: 1
        return itemStack
    }
}