package io.github.zzzyyylllty.sertraline.gui

import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.github.zzzyyylllty.sertraline.Sertraline.itemMap
import io.github.zzzyyylllty.sertraline.config.asListEnhanced
import io.github.zzzyyylllty.sertraline.data.ModernSItem
import io.github.zzzyyylllty.sertraline.item.sertralineItemBuilder
import io.github.zzzyyylllty.sertraline.util.gui.GuiItem
import io.github.zzzyyylllty.sertraline.util.gui.build
import io.github.zzzyyylllty.sertraline.util.minimessage.toComponent
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType.*
import taboolib.library.xseries.XMaterial
import taboolib.module.lang.asLangText
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.PageableChest
import taboolib.platform.util.giveItem

class ItemExplorer {
    fun mainItemExplorer(player: Player) {
        val items = itemMap.values.toList()

        player.openMenu<PageableChest<ModernSItem>>(console.asLangText("Editor_Title")) {

            val suffix by lazy { console.asLangText("Editor_Item_Suffix").asListEnhanced()?.toComponent() ?: emptyList() }


            rows(6)

            map(
                "#########",
                "#########",
                "#########",
                "#########",
                "#########",
                "---B-C---"
            )

            set('-', XMaterial.GRAY_STAINED_GLASS_PANE) { name = " " }

            // 设置可用槽位（通过字符）
            slotsBy('#')

            // 设置元素列表
            elements { items }

            // 生成每个元素对应的物品
            onGenerate(async = true) { player, element, index, slot ->
                val item = sertralineItemBuilder(element.key, player) ?: GuiItem("error", "ARROW").build()
                val meta = item.itemMeta
                val lore = meta.lore()?.toMutableList() ?: mutableListOf()
                lore.addAll(suffix)
                item.lore(lore)
                val tag = item.getItemTag(true)
                tag["sertraline_browse_item"] = "yes"
                item.setItemTag(tag)
            }

            // 元素点击事件
            onClick { event, element ->
                val bukkitPlayer = event.clicker
                when (event.clickEvent().click) {
                    LEFT -> bukkitPlayer.giveItem(sertralineItemBuilder(element.key, bukkitPlayer, amount = 1))
                    SHIFT_LEFT -> {
                        val itemStack = sertralineItemBuilder(element.key, bukkitPlayer)!!
                        itemStack.amount = (itemStack.maxStackSize)
                        bukkitPlayer.giveItem(itemStack)
                    }
                    RIGHT -> {}
                    SHIFT_RIGHT -> {}
                    MIDDLE -> {}
                    else -> {}
                }

            }

            // 设置下一页按钮
            setNextPage(50) { page, hasNextPage ->
                GuiItem("nextpage", "ARROW").build()
            }

            // 设置上一页按钮
            setPreviousPage(48) { page, hasPreviousPage ->
                GuiItem("previouspage", "ARROW").build()
            }
        }
    }
}