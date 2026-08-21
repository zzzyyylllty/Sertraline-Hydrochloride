package io.github.zzzyyylllty.sertraline.data

import io.github.zzzyyylllty.embiancomponent.EmbianComponent
import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.compat.PlatformCompat
import io.github.zzzyyylllty.sertraline.config.asListEnhanced
import io.github.zzzyyylllty.sertraline.item.sertralineItemBuilder
import io.github.zzzyyylllty.sertraline.util.parseNamespacedKey
import io.github.zzzyyylllty.sertraline.logger.severeL
import io.github.zzzyyylllty.sertraline.logger.warningL
import io.github.zzzyyylllty.sertraline.util.ExternalItemHelper
import io.github.zzzyyylllty.sertraline.util.VersionHelper
import io.github.zzzyyylllty.sertraline.util.minimessage.toComponent
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import com.cryptomorin.xseries.XMaterial
import taboolib.library.xseries.XItemStack
import taboolib.module.nms.NMSItemTag.Companion.asNMSCopy
import kotlin.math.roundToInt

private val specialItemNamespace = listOf("minecraft", "mc", "vanilla")
private val sertralineNamespace = listOf("sertraline", "depazitems", "depaz")
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

        var itemStack: ItemStack?

        try {
            val providedItem = when {
                // 原版物品（mc / minecraft / vanilla）
                specialItemNamespace.contains(source) -> {
                    val params = (parameters ?: mapOf<String, Any?>()).toMutableMap()
                    params["material"] = item
                    XItemStack.deserialize(params)
                }
                // Sertraline 自有物品
                sertralineNamespace.contains(source) -> {
                    if (player != null) sertralineItemBuilder(item, player)
                    else sertralineItemBuilder(item, null)
                }
                // 外部插件物品（ItemsAdder, Oraxen, Nexo, CraftEngine 等）
                else -> {
                    if (player != null) {
                        ExternalItemHelper.build(player, source, item)
                            ?: ExternalItemHelper.itemBridgeAll?.build(source, item, player)?.get()
                    } else {
                        ExternalItemHelper.buildNoPlayer(source, item)
                            ?: ExternalItemHelper.itemBridgeAll?.build(source, item)?.get()
                    }
                }
            }

            if (providedItem == null) {
                severeL("ErrorItemGenerationFailedNull", source, item)
                // 1.13+ 枚举名，1.12.2 无此常量；XMaterial 按服务端版本解析（1.12.2 → GRASS）
return ItemStack(XMaterial.GRASS_BLOCK.parseMaterial() ?: Material.STONE)
            }
            itemStack = providedItem

        } catch (e: Exception) {
            severeL("ErrorItemGenerationFailed", source, item)
            e.printStackTrace()
            // 1.13+ 枚举名，1.12.2 无此常量；XMaterial 按服务端版本解析（1.12.2 → GRASS）
return ItemStack(XMaterial.GRASS_BLOCK.parseMaterial() ?: Material.STONE)
        }

        if (parameters?.isNotEmpty() ?: false) {

            itemStack.itemMeta?.let { meta ->
                parameters["name"]?.toString()?.toComponent()?.let { PlatformCompat.setDisplayName(meta, it) }
                parameters["display-name"]?.toString()?.toComponent()?.let { PlatformCompat.setDisplayName(meta, it) }
                parameters["custom-name"]?.toString()?.toComponent()?.let { PlatformCompat.setCustomName(meta, it) }
                parameters["item-name"]?.toString()?.toComponent()?.let { PlatformCompat.setItemName(meta, it) }
                (parameters["item-model"] ?: parameters["model"])?.toString()?.let { PlatformCompat.setItemModel(meta, it.parseNamespacedKey()) }
                itemStack.setItemMeta(meta)
            }
            parameters["lore"].asListEnhanced()?.toComponent()?.let { PlatformCompat.setLore(itemStack, it) }

        }
        if (!components.isNullOrEmpty()) {
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