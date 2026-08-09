package io.github.zzzyyylllty.sertraline.util.loreformat

import io.github.zzzyyylllty.sertraline.Sertraline.loreFormats
import io.github.zzzyyylllty.sertraline.data.LoreFormat
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * JS 可调用的 LoreFormat 工具类。
 *
 * 在 JavaScript 中通过 Java.type 使用：
 * ```js
 * var LoreFormatUtil = Java.type("io.github.zzzyyylllty.sertraline.util.loreformat.LoreFormatUtil");
 * var lore = LoreFormatUtil.generateLore(itemStack, sItem, "example_lore_format", player);
 * var replaced = LoreFormatUtil.applyLore(itemStack, sItem, "example_lore_format", player);
 * ```
 */
object LoreFormatUtil {

    /**
     * 使用指定的 lore 格式名称生成 lore 组件列表。
     *
     * @param item        要生成 lore 的物品 ItemStack
     * @param sItem       物品的 ModernSItem 数据
     * @param format      Lore 格式名称（对应 lore-formats 目录下的配置）
     * @param player      玩家（用于占位符解析，可选）
     * @param defaultVars 额外的默认数据，会合并到物品的 vars 中供占位符解析使用
     * @return 生成的 Lore Component 列表，如果格式不存在则返回 null
     */
    @JvmStatic
    fun generateLore(
        item: ItemStack,
        sItem: ModernSItem,
        format: String,
        player: Player? = null,
        defaultVars: Map<String, Any?>? = null,
    ): List<Component>? {
        val loreFormat = loreFormats[format] ?: return null
        if (defaultVars != null) {
            val originVars = sItem.getDeepData("sertraline:vars")
            sItem.setDeepData("sertraline:vars", defaultVars)
            try {
                return applyLoreFormat(sItem, player, item.lore(), loreFormat)
            } finally {
                sItem.setDeepData("sertraline:vars", originVars)
            }
        }
        return applyLoreFormat(sItem, player, item.lore(), loreFormat)
    }

    /**
     * 使用指定的 lore 格式名称生成 lore 并直接设置到 ItemStack 上。
     *
     * @param item        要修改的物品 ItemStack
     * @param sItem       物品的 ModernSItem 数据
     * @param format      Lore 格式名称
     * @param player      玩家（用于占位符解析，可选）
     * @param defaultVars 额外的默认数据，会合并到物品的 vars 中供占位符解析使用
     * @return 修改后的 ItemStack（或原物品如果格式不存在）
     */
    @JvmStatic
    fun applyLore(
        item: ItemStack,
        sItem: ModernSItem,
        format: String,
        player: Player? = null,
        defaultVars: Map<String, Any?>? = null,
    ): ItemStack {
        val lore = generateLore(item, sItem, format, player, defaultVars) ?: return item
        item.lore(lore)
        return item
    }

    /**
     * 获取已加载的 LoreFormat 对象。
     *
     * @param format Lore 格式名称
     * @return LoreFormat 对象，不存在则返回 null
     */
    @JvmStatic
    fun getFormat(format: String): LoreFormat? = loreFormats[format]

    /**
     * 获取所有已加载的 Lore 格式名称列表。
     */
    @JvmStatic
    fun getFormatNames(): Set<String> = loreFormats.keys
}
