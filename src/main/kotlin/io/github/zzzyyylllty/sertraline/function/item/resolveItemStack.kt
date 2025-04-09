package io.github.zzzyyylllty.sertraline.function.item

import com.francobm.magicosmetics.api.Cosmetic
import com.willfp.ecoitems.items.EcoItems
import dev.lone.itemsadder.api.ItemsAdder
import github.saukiya.sxitem.SXItem
import ink.ptms.zaphkiel.Zaphkiel
import ink.ptms.zaphkiel.um.Mythic
import io.github.zzzyyylllty.sertraline.Sertraline.console
import io.th0rgal.oraxen.api.OraxenItems
import net.Indyuce.mmoitems.MMOItems
import net.momirealms.craftengine.bukkit.api.CraftEngineItems
import net.momirealms.craftengine.bukkit.plugin.BukkitCraftEngine
import net.momirealms.craftengine.core.plugin.CraftEngine
import net.momirealms.craftengine.core.util.Key
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import pers.neige.neigeitems.manager.ItemManager
import taboolib.common.platform.function.severe
import taboolib.library.xseries.XMaterial
import taboolib.library.xseries.parseToItemStack
import taboolib.module.lang.asLangText
import top.maplex.arim.tools.itemmanager.source.SourceCraftEngine

fun resolveItemStack(s: String, p: Player?): ItemStack? {
    val split = s.split(":")

    var returnItem: ItemStack? = null
    if (split.size == 1) {
        return ItemStack(XMaterial.valueOf(s).parseMaterial() ?: Material.GRASS_BLOCK)
    }

    val prefix = split[0]
    val param = split[1]

    val miType = param.split(".")[0]
    val miId = if (param.split(".").size >= 2) param.split(".")[1] else "1"

    val prefixu = prefix.uppercase()

    when (prefixu) {
        "TL", "TABOOLIB" -> {
            returnItem = try { param.parseToItemStack() } catch (e: Exception) {
                severe(console.asLangText("ERROR_UNABLE_TO_GENERATE_ITEM", s, e))
                null
            }
        }
        "MI", "MMOITEMS" -> {
            returnItem = try { MMOItems.plugin.getMMOItem(MMOItems.plugin.types.get(miType), miId)?.newBuilder()?.build() } catch (e: Exception) {
                severe(console.asLangText("ERROR_UNABLE_TO_GENERATE_ITEM", s, e))
                null
            }
        }
        "NI", "NEIGEITEMS" -> {
            returnItem = try { ItemManager.getItemStack(param, p) } catch (e: Exception) {
                severe(console.asLangText("ERROR_UNABLE_TO_GENERATE_ITEM", s, e))
                null
            }
        }
        "ZA", "ZAPHKIEL", "ZP" -> {
            returnItem = try { Zaphkiel.api().getItemManager().getItem(param)?.buildItemStack(p) } catch (e: Exception) {
                severe(console.asLangText("ERROR_UNABLE_TO_GENERATE_ITEM", s, e))
                null
            }
        }
        "MM", "MYTHICMOBS", "MYTHIC" -> {
            returnItem = try { Mythic.API.getItem(param)?.generateItemStack(1) } catch (e: Exception) {
                severe(console.asLangText("ERROR_UNABLE_TO_GENERATE_ITEM", s, e))
                null
            }
        }
        "MC", "MAGIC", "MAGICCOSMETICS" -> {
            returnItem = try { Cosmetic.getCosmetic(param).itemStack } catch (e: Exception) {
                severe(console.asLangText("ERROR_UNABLE_TO_GENERATE_ITEM", s, e))
                null
            }
        }
        /*
        "EC", "ECO", "ECOITEMS" -> {
            returnItem = try { EcoItems.getByID(param)?.itemStack } catch (e: Exception) {
                severe(console.asLangText("ERROR_UNABLE_TO_GENERATE_ITEM", s, e))
                null
            }
        }*/
        "SX", "SXITEM", "SX-ITEM" -> {
            returnItem = try { SXItem.getItemManager().getItem(param, p) } catch (e: Exception) {
                severe(console.asLangText("ERROR_UNABLE_TO_GENERATE_ITEM", s, e))
                null
            }
        }
        "IA", "ITEMSADDER" -> {
            returnItem = try {
                ItemsAdder.getCustomItem(param)
            } catch (e: Exception) {
                severe(console.asLangText("ERROR_UNABLE_TO_GENERATE_ITEM", s, e))
                null
            }
        }
        "OX", "OXAREN" -> {
            returnItem = try {
                OraxenItems.getItemById(param).build()
            } catch (e: Exception) {
                severe(console.asLangText("ERROR_UNABLE_TO_GENERATE_ITEM", s, e))
                null
            }
        }

        "CE", "CRAFTENGINE" -> {
            returnItem = try {
                if (p == null) CraftEngineItems.byId(Key.from(param))?.buildItemStack()
                else {
                    // val api = CraftEngine.instance()
                    val bukkitApi = BukkitCraftEngine.instance()
                    CraftEngineItems.byId(Key.from(param))?.buildItemStack(bukkitApi.adapt(p))
                }
            } catch (e: Exception) {
                severe(console.asLangText("ERROR_UNABLE_TO_GENERATE_ITEM", s, e))
                null
            }
        }
    }
    if (returnItem == null) severe(console.asLangText("ERROR_GENERATED_ITEM_NOT_EXIST"), s)
    return returnItem
}
