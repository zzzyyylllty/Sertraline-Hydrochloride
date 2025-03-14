package io.github.zzzyyylllty.functions.load.part

import com.francobm.magicosmetics.api.Cosmetic
import com.willfp.ecoitems.items.EcoItems
import dev.lone.itemsadder.api.ItemsAdder
import github.saukiya.sxitem.SXItem
import ink.ptms.zaphkiel.Zaphkiel
import ink.ptms.zaphkiel.um.Mythic
import io.github.zzzyyylllty.SertralineHydrochloride.console
import net.Indyuce.mmoitems.MMOItems
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import pers.neige.neigeitems.manager.ItemManager
import taboolib.common.platform.function.severe
import taboolib.library.xseries.parseToItemStack
import taboolib.module.lang.asLangText
import io.th0rgal.oraxen.api.OraxenItems
import org.bukkit.entity.Player

fun resolveItemStack(s: String, source: String, p: Player?): ItemStack? {
    val split = s.split(":")

    var returnItem: ItemStack? = null
    if (s.length == 1) {
        returnItem = ItemStack(Material.valueOf(s))
    }

    val prefix = split[0]
    val param = split[1]

    val miType = param.split(".")[0]
    val miId = param.split(".")[1]
    
    val prefixu = prefix.uppercase()

    when (prefixu) {
        "TL", "TABOOLIB" -> {
            returnItem = try { param.parseToItemStack() } catch (e: Exception) {
                severe(console.asLangText("enable.load.error_item_unable_to_generate"),source, s, e)
                null
            }
        }
        "MI", "MMOITEMS" -> {
            returnItem = try { MMOItems.plugin.getMMOItem(MMOItems.plugin.types.get(miType), miId)?.newBuilder()?.build() } catch (e: Exception) {
                severe(console.asLangText("enable.load.error_item_unable_to_generate"),source, s, e)
                null
            }
        }
        "NI", "NEIGEITEMS" -> {
            returnItem = try { ItemManager.getItemStack(param, p) } catch (e: Exception) {
                severe(console.asLangText("enable.load.error_item_unable_to_generate"),source, s, e)
                null
            }
        }
        "ZA", "ZAPHKIEL" -> {
            returnItem = try { Zaphkiel.api().getItemManager().getItem(param)?.buildItemStack(p) } catch (e: Exception) {
                severe(console.asLangText("enable.load.error_item_unable_to_generate"),source, s, e)
                null
            }
        }
        "MM", "MYTHICMOBS", "MYTHIC" -> {
            returnItem = try { Mythic.API.getItem(param)?.generateItemStack(1) } catch (e: Exception) {
                severe(console.asLangText("enable.load.error_item_unable_to_generate"),source, s, e)
                null
            }
        }
        "MC", "MAGIC", "MAGICCOSMETICS" -> {
            returnItem = try { Cosmetic.getCosmetic(param).itemStack } catch (e: Exception) {
                severe(console.asLangText("enable.load.error_item_unable_to_generate"),source, s, e)
                null
            }
        }
        /*"EC", "ECO", "ECOITEMS" -> {
            returnItem = try { EcoItems.getByID(param)?.itemStack } catch (e: Exception) {
                severe(console.asLangText("enable.load.error_item_unable_to_generate"),source, s, e)
                null
            }
        }*/
        "SX", "SXITEM", "SX-ITEM" -> {
            returnItem = try { SXItem.getItemManager().getItem(param, p) } catch (e: Exception) {
                severe(console.asLangText("enable.load.error_item_unable_to_generate"),source, s, e)
                null
            }
        }
        "IA", "ITEMSADDER" -> {
            returnItem = try {
                ItemsAdder.getCustomItem(param)
            } catch (e: Exception) {
                severe(console.asLangText("enable.load.error_item_unable_to_generate"),source, s, e)
                null
            }
        }
        "OX", "OXAREN" -> {
            returnItem = try {
                OraxenItems.getItemById(param).build()
            } catch (e: Exception) {
                severe(console.asLangText("enable.load.error_item_unable_to_generate"),source, s, e)
                null
            }
        }
    }
    if (returnItem == null) severe(console.asLangText("enable.load.error_item_not_exist"),source, s)
    return returnItem
}
