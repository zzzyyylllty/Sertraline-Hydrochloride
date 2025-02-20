package io.github.zzzyyylllty.functions.load.part

import com.francobm.magicosmetics.api.Cosmetic
import com.willfp.ecoitems.items.EcoItems
import ink.ptms.zaphkiel.Zaphkiel
import ink.ptms.zaphkiel.um.Mythic
import io.github.zzzyyylllty.SertralineHydrochloride.console
import io.lumine.mythic.core.items.MythicItem
import net.Indyuce.mmoitems.MMOItems
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import pers.neige.neigeitems.NeigeItems
import pers.neige.neigeitems.libs.bot.inker.bukkit.nbt.NeigeItemsUtils
import pers.neige.neigeitems.manager.ItemManager
import taboolib.common.platform.function.severe
import taboolib.library.xseries.parseToItemStack
import taboolib.module.lang.asLangText

fun resolveItemStack(s: String,source: String): Any? {
    val split = s.split(":")

    var returnItem: Any? = null
    if (s.length == 1) {
        returnItem = ItemStack(Material.valueOf(s))
    }

    val prefix = split[0]
    val param = split[1]

    val miType = param.split(".")[0] ?: null
    val miId = param.split(".")[1] ?: null
    
    val prefixu = prefix.uppercase()

    when (prefixu) {
        "TL", "TABOOLIB" -> {
            returnItem = try { param.parseToItemStack() } catch (e: Exception) {
                severe(console.asLangText("enable.load.error_item_unable_to_generate"),source, s, e)
                null
            }
        }
        "MI", "MMOITEMS" -> {
            returnItem = try { MMOItems.plugin.getMMOItem(MMOItems.plugin.types.get(miType), miId)} catch (e: Exception) {
                severe(console.asLangText("enable.load.error_item_unable_to_generate"),source, s, e)
                null
            }
        }
        "NI", "NEIGEITEMS" -> {
            returnItem = try { ItemManager.getItemStack(param)} catch (e: Exception) {
                severe(console.asLangText("enable.load.error_item_unable_to_generate"),source, s, e)
                null
            }
        }
        "ZA", "ZAPHKIEL" -> {
            returnItem = try { Zaphkiel.api().getItemManager().getItem(param)} catch (e: Exception) {
                severe(console.asLangText("enable.load.error_item_unable_to_generate"),source, s, e)
                null
            }
        }
        "MM", "MYTHICMOBS", "MYTHIC" -> {
            returnItem = try { Mythic.API.getItem(param)} catch (e: Exception) {
                severe(console.asLangText("enable.load.error_item_unable_to_generate"),source, s, e)
                null
            }
        }
        "MC", "MAGIC", "MAGICCOSMETICS" -> {
            returnItem = try { Cosmetic.getCosmetic(param)} catch (e: Exception) {
                severe(console.asLangText("enable.load.error_item_unable_to_generate"),source, s, e)
                null
            }
        }
        "EC", "ECO", "ECOITEMS" -> {
            returnItem = try { EcoItems.getByID(param)} catch (e: Exception) {
                severe(console.asLangText("enable.load.error_item_unable_to_generate"),source, s, e)
                null
            }
        }
    }
    if (returnItem == null) severe(console.asLangText("enable.load.error_item_not_exist"),source, s)
    return returnItem
}
